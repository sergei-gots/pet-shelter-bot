package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.DialogRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;
import pro.sky.petshelterbot.repository.VolunteerRepository;

/**
 * Handles commands receiving from user supposed
 * to be a volunteer.
 * Also operates chat between Adopter and Volunteer on the Volunteer's side
 **/

@Component
public class VolunteerDialogHandler extends AbstractDialogHandler {

    public VolunteerDialogHandler(TelegramBot telegramBot,
                                  ShelterRepository shelterRepository,
                                  UserMessageRepository userMessageRepository,
                                  VolunteerRepository volunteerRepository, DialogRepository dialogRepository) {
        super(telegramBot, shelterRepository, userMessageRepository, volunteerRepository, dialogRepository);
    }

    @Override
    public boolean handle(Message message) {

        Long chatId = message.chat().id();
        Volunteer volunteer = volunteerRepository
                .findByChatId(chatId).orElse(null);

        if (volunteer == null) {
            return false;
        }

        logger.debug("handle(message)- message from volunteer={} received", volunteer.getFirstName());

        String text = message.text();

        if(processCommand(volunteer, text)) {
            return true;
        }

        Dialog dialog = getDialog(volunteer);
        if(dialog == null) {
            sendPersonalizedMessage(volunteer,
                    "ваш чат находится в ожидании запросов. Поэтому прямая переписка сейчас не поддерживается" );
            return true;
        }

        sendDialogMessageToAdopter(getDialog(volunteer), text);
        return true;
    }

    private boolean processCommand(Volunteer volunteer, String text) {
        switch(text) {
            case START:         processStart(volunteer);            return true;
            case JOIN_DIALOG:   processJoinDialog(volunteer);       return true;
            case CLOSE_DIALOG:
            case CLOSE_DIALOG_RU:
                                processCloseDialog(volunteer);      return true;
            case HAVE_A_BREAK:  processHaveABreak(volunteer);       return true;
            case RESUME_SERVICE:processResumeService(volunteer);    return true;
        }
        return false;
    }

    private void processStart(Volunteer volunteer) {
        logger.debug("processJoinDialog()-method. Volunteer.first_name=\"{}\"",
                volunteer.getFirstName());
        sendPersonalizedMessage(volunteer,
                "добро пожаловать в состав волонтёров шелтера "
                        + volunteer.getShelter().getName()+ " !");
    }


    @Override
    public boolean handle(CallbackQuery callbackQuery) {
        Volunteer volunteer = volunteerRepository.findByChatId(callbackQuery.message().chat().id()).orElse(null);
        if (volunteer == null) {
            return false;
        }

        logger.info("handle(callbackQuery)-method.  callbackQuery.data={}", callbackQuery.data());
        return processCommand(volunteer, callbackQuery.data());
    }

    private Dialog getDialog(Volunteer volunteer) {
        return dialogRepository.findByVolunteer(volunteer)
                .orElse(null);
    }
    private void processCloseDialog(Volunteer volunteer) {

        Dialog dialog = getDialog(volunteer);
        if(dialog == null) {
            logger.warn("processCloseDialog() - Dialog for " +
                    "volunteer.getFirstName()=\"{}\" does not exist.", volunteer.getFirstName());
            logger.warn("processCloseDialog() - volunteer.isAvaliable()=\"{}\"", volunteer.isAvailable());
            return;
        }
        logger.debug("processCloseDialog() - Dialog between " +
                        "adopter.getFirstName()=\"{}\" and  volunteer.getFirstName()=\"{}\" will be closed.",
                dialog.getAdopter().getFirstName(), volunteer.getFirstName());
        sendPersonalizedMessage(volunteer,"консультация закрыта. ваш чат переведён в режим ожидания новых запросов от пользователей.");
        sendPersonalizedMessage(dialog.getAdopter(),"диалог с волонтёром шелтера завершён. Если у вас возникнут новые вопросы, " +
                "обращайтесь к нам ещё. Всего вам доброго-)");


        dialogRepository.delete(dialog);

        Dialog dialogInWaiting =   dialogRepository.findFirstByVolunteerIsNullAndShelterOrderByIdAsc(volunteer.getShelter()).orElse(null);
        if(dialogInWaiting == null) {
            volunteer.setAvailable(true);
            volunteerRepository.save(volunteer);

        } else {
            dialogInWaiting.setVolunteer(volunteer);
            dialogRepository.save(dialogInWaiting);
            sendJoinInvitationsToVolunteersAndNotifyAdopter(dialog);
        }
    }

    private void processHaveABreak(Volunteer volunteer) {

    }

    private void processResumeService(Volunteer volunteer) {

    }

    private void processJoinDialog(Volunteer volunteer) {

        logger.debug("processJoinDialog()-method. Volunteer.first_name=\"{}\"",
            volunteer.getFirstName());

        Dialog dialogToJoin = getFirstWaitingDialog(volunteer.getShelter());

        if(dialogToJoin == null) {
            logger.debug("processJoinDialog()-method. dialogToJoin == null is true");
            logger.trace("processJoinDialog()-method. volunteer.getFirstName()=\"{}\" will be notified that all the dialogs have been picked up",
                    volunteer.getFirstName());
            ReplyKeyboardRemove clearKeyboardMarkup = new ReplyKeyboardRemove();
            telegramBot.execute(
                    new SendMessage(volunteer.getChatId(), volunteer.getFirstName() + ", спасибо! Запрос на диалог уже был подхвачен вашим коллегой. " +
                            "Мы известим вас о новых запросах на консультацию:)")
                            .replyMarkup(clearKeyboardMarkup)
            );
            return;
        }

        logger.debug("processJoinDialog()-method. volunteer.getFirstName=\"{}\" will be joined to the dialog",
                volunteer.getFirstName());
        dialogToJoin.setVolunteer(volunteer);
        volunteer.setAvailable(false);
        dialogRepository.save(dialogToJoin);
        volunteerRepository.save(volunteer);

        sendDialogMessageToAdopter(dialogToJoin,
                dialogToJoin.getAdopter().getFirstName() + ", здравствуйте! Расскажите, какой у вас вопрос?");
        sendMessage(volunteer.getChatId(),
                "Отлично, вы в чате. Пользователю направлено приветствие и предложение сформировать интересующий вопрос.\n" +
                "Для завершения диалога используйте команды: \n\t" + CLOSE_DIALOG_RU + " или " + CLOSE_DIALOG);

        Dialog nextDialog = getFirstWaitingDialog(volunteer.getShelter());

        if(nextDialog == null) {
            logger.debug("processJoinDialog()-method. nextToJoin == null is true");

            ReplyKeyboardRemove clearKeyboardMarkup = new ReplyKeyboardRemove();
            for(Volunteer availableVolunteer : volunteerRepository.findByShelterAndAvailableIsTrue(volunteer.getShelter())) {
                logger.trace("processJoinDialog()-method. volunteer.getFirstName()=\"{}\" will be notified that all the dialogs have been picked up",
                        availableVolunteer.getFirstName());
                telegramBot.execute(
                        new SendMessage(availableVolunteer.getChatId(), "Все запросы на консультацию были подхвачены, спасибо!" +
                        "Мы известим вас о новых запросах на консультацию:)")
                        .replyMarkup(clearKeyboardMarkup)
                                .replyMarkup(clearKeyboardMarkup)
                );
            }
        }
    }

    private Dialog getFirstWaitingDialog(Shelter shelter) {
        return dialogRepository.findFirstByVolunteerIsNullAndShelterOrderByIdAsc(shelter).orElse(null);
    }


}
