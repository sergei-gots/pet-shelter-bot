package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Dialog;
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

        Dialog dialog = dialogRepository.findByVolunteer(volunteer)
                .orElseThrow(() -> new IllegalStateException("No dialog found for a " + volunteer));

        if (CLOSE_DIALOG.equals(text)) {
            processCloseDialog(dialog);
        } else {
            sendDialogMessageToAdopter(dialog, text);
        }
        return true;
    }

    @Override
    public boolean handle(CallbackQuery callbackQuery) {
        Volunteer volunteer = volunteerRepository.findByChatId(callbackQuery.message().chat().id()).orElse(null);
        if (volunteer == null) {
            return false;
        }

        logger.info("handle(callbackQuery)-method.  callbackQuery.data={}", callbackQuery.data());

        pro.sky.petshelterbot.entity.Dialog dialog = dialogRepository.findByVolunteer(volunteer)
                .orElseThrow(() -> new IllegalStateException("No dialog found for volunteer=" + volunteer));

        switch(callbackQuery.data()) {
            case JOIN_DIALOG: processJoinDialog(dialog);
                            return true;
            case CLOSE_DIALOG:  processCloseDialog(dialog);
            case HAVE_A_BREAK:  processHaveABreak(volunteer);
            case RESUME_SERVICE:processResumeService(volunteer);

        }
        return false;
    }

    private void processCloseDialog(Dialog dialog) {
        Volunteer volunteer = dialog.getVolunteer();

        logger.debug("processCloseDialog()-method between " +
                "Adopter.first_name=\"{}\" and  Volunter.first_name=\"{}\"",
                dialog.getAdopter().getFirstName(), volunteer.getFirstName());

        dialogRepository.delete(dialog);

        Dialog dialogInWaiting =   dialogRepository.findFirstByVolunteerIsNullAndShelterOrderByIdAsc(volunteer.getShelter()).orElse(null);
        if(dialogInWaiting == null) {
            volunteer.setAvailable(true);
            volunteerRepository.save(volunteer);
        } else {
            dialogInWaiting.setVolunteer(volunteer);
            dialogRepository.save(dialogInWaiting);
            sendJoinInvitationToVolunteerAndNotifyAdopter(dialog);
        }
    }

    private void processHaveABreak(Volunteer volunteer) {

    }

    private void processResumeService(Volunteer volunteer) {

    }

    private void processJoinDialog(Dialog dialog) {

        sendDialogMessageToAdopter(dialog,
                dialog.getAdopter().getFirstName() + ", здравствуйте! Расскажите, какой у вас вопрос?");
        sendMessage(dialog.getVolunteer().getChatId(),
                "Отлично, вы в чате. Пользователю направлено приветствие и предложение сформировать интересующий вопрос.");
    }


}
