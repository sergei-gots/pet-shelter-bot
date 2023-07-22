package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.*;

import static pro.sky.petshelterbot.constants.PetShelterBotConstants.MessageKey.DIALOG_INIT_CLARIFICATION_FOR_VOLUNTEER;
import static pro.sky.petshelterbot.constants.TelegramChatStates.ChatState.ADOPTER_IN_DIALOG;
import static pro.sky.petshelterbot.constants.TelegramChatStates.ChatState.INITIAL_STATE;

/**
 * Handles commands receiving from user supposed
 * to be a volunteer.
 * Also operates chat between Adopter and Volunteer on the Volunteer's side
 **/

@Component
public class VolunteerDialogHandler extends AbstractDialogHandler {

    public VolunteerDialogHandler(TelegramBot telegramBot,
                                  AdopterRepository adopterRepository,
                                  ShelterRepository shelterRepository,
                                  UserMessageRepository userMessageRepository,
                                  ButtonRepository buttonRepository,
                                  VolunteerRepository volunteerRepository, DialogRepository dialogRepository) {
        super(telegramBot, adopterRepository, volunteerRepository, shelterRepository, userMessageRepository, buttonRepository, dialogRepository);
    }

    @Override
    public boolean handleMessage(Message message) {

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

        forwardDialogMessage(volunteer, dialog.getAdopter(), text);
        return true;
    }

    private boolean processCommand(Volunteer volunteer, String text) {
        switch(text) {
            case START:         processStart(volunteer);            return true;
            case JOIN_DIALOG:   processJoinDialog(volunteer);       return true;
            case CLOSE_DIALOG:
            case CLOSE_DIALOG_RU:
                processCloseDialog(volunteer);      return true;
        }
        return false;
    }

    private void processStart(Volunteer volunteer) {
        logger.debug("processJoinDialog()-method. Volunteer.first_name=\"{}\"",
                volunteer.getFirstName());
        sendPersonalizedMessage(volunteer,
                "добро пожаловать в волонтёры шелтера "
                        + volunteer.getShelter().getName()+ " !");
    }


    @Override
     public boolean handleCallbackQuery(Message message, String key) {
        Volunteer volunteer = volunteerRepository.findByChatId(message.chat().id()).orElse(null);
        if (volunteer == null) {
            return false;
        }

        logger.info("handle(callbackQuery)-method.  callbackQuery.data={}", key);
        return processCommand(volunteer, key);
    }

    private Dialog getDialog(Volunteer volunteer) {
        return dialogRepository.findByVolunteer(volunteer)
                .orElse(null);
    }
    private void processJoinDialog(Volunteer volunteer) {

        logger.debug("processJoinDialog()-method. Volunteer.first_name=\"{}\"",
                volunteer.getFirstName());

        Dialog dialogToJoin = nextDialogInWaiting(volunteer.getShelter());

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


        Adopter adopter = dialogToJoin.getAdopter();
        adopter.setChatState(ADOPTER_IN_DIALOG);
        sendMenu(adopter, DIALOG);
        forwardDialogMessage(volunteer, adopter,
                adopter.getFirstName() + ", здравствуйте! Расскажите, какой у вас вопрос?");

        volunteer.setChatState(ADOPTER_IN_DIALOG);
        sendMenu(volunteer, DIALOG_VOLUNTEER_PART);
        sendUserMessage(volunteer, DIALOG_INIT_CLARIFICATION_FOR_VOLUNTEER);

        Dialog nextDialog = nextDialogInWaiting(volunteer.getShelter());
        if(nextDialog == null) {
            logger.debug("processJoinDialog()-method. nextToJoin == null is true");
            notifyAllAvailableShelterVolunteersAboutNoRequest(volunteer.getShelter());
        }
    }

    private void processCloseDialog(Volunteer volunteer) {

        Dialog dialog = getDialog(volunteer);
        if(dialog == null) {
            logger.warn("processCloseDialog() - Dialog for " +
                    "volunteer.getFirstName()=\"{}\" does not exist.", volunteer.getFirstName());
            logger.warn("processCloseDialog() - volunteer.avaliable=\"{}\"", volunteer.isAvailable());
            return;
        }
        Adopter adopter = dialog.getAdopter();
        logger.debug("processCloseDialog() - Dialog between " +
                        "adopter.getFirstName()=\"{}\" and  volunteer.getFirstName()=\"{}\" will be closed.",
                adopter.getFirstName(), volunteer.getFirstName());
        sendPersonalizedMessage(volunteer,"консультация закрыта. ваш чат переведён в режим ожидания новых запросов от пользователей.");
        showShelterInfoMenu(adopter);
        sendPersonalizedMessage(dialog.getAdopter(),"диалог с волонтёром шелтера завершён. Если у вас возникнут новые вопросы, " +
                "обращайтесь к нам ещё. Всего вам доброго-)");
        dialogRepository.delete(dialog);

        Dialog nextDialog = nextDialogInWaiting(volunteer.getShelter());
        if(nextDialog == null) {
            volunteer.setChatState(INITIAL_STATE);
            volunteer.setAvailable(true);
            volunteerRepository.save(volunteer);
        } else {
            nextDialog.setVolunteer(volunteer);
            dialogRepository.save(nextDialog);
            sendHandshakes(dialog);
        }
    }




}