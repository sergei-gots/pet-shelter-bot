package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.*;
import pro.sky.petshelterbot.repository.*;

import java.util.Collection;

/**
 * Handles commands receiving from user supposed
 * to be a volunteer.
 * Also operates chat between Adopter and Volunteer on the Volunteer's side
 **/

@Component
public abstract class AbstractDialogHandler extends AbstractHandler {

    public AbstractDialogHandler(TelegramBot telegramBot,
                                 AdopterRepository adopterRepository,
                                 VolunteerRepository volunteerRepository,
                                 ShelterRepository shelterRepository,
                                 UserMessageRepository userMessageRepository,
                                 ButtonRepository buttonRepository,
                                 DialogRepository dialogRepository) {
        super(telegramBot, adopterRepository, volunteerRepository, shelterRepository, userMessageRepository, buttonRepository, dialogRepository);
    }

    protected Dialog nextDialogInWaiting(Shelter shelter) {
        Collection<Dialog> dialogsInWaiting
                = dialogRepository.findWaitingDialogsByVolunteerShelterOrderByIdAsc(shelter);
        if (dialogsInWaiting.isEmpty()) {
            return null;
        } else {
            return dialogsInWaiting.iterator().next();
        }
    }


    public void sendPersonalizedMessage(Person person, String text) {
        sendMessage(person.getChatId(), person.getFirstName() + ", " + text);
    }

    @Override
    public boolean handle(Message message, String key) {


        Adopter adopter = getAdopter(message);
        if (isShelterToBeAssigned(adopter, key)) {
            return true;
        }

        switch (key) {
            case CANCEL_VOLUNTEER_CALL:
            case CANCEL_VOLUNTEER_CALL_ADOPTION_INFO_MENU:
            case CANCEL_VOLUNTEER_CALL_SHELTER_INFO_MENU:
            case CLOSE_DIALOG:
            case CLOSE_DIALOG_RU:
                handleCancelVolunteerCall(adopter, key);
                return true;

            default:
                return false;
        }
    }

    public void sendHandshakes(Dialog dialog) {
        Volunteer volunteer = dialog.getVolunteer();
        Adopter adopter = dialog.getAdopter();

        long volunteerChatId = volunteer.getChatId();
        long adopterChatId = adopter.getChatId();

        sendMessage(volunteerChatId,
                volunteer.getFirstName() + "! C вами хотел бы связаться " + adopter.getFirstName() +
                        ". Нажмите кнопку 'Присоединиться к чату' для начала общения.");
        sendMenu(volunteer, JOIN_DIALOG);

        sendMenu(adopter, CLOSE_DIALOG);
        sendMessage(adopterChatId, "Волонтёру отослано уведомление. Волонтёр свяжется с вами " +
                "насколько это возможно скоро. Основное меню на время диалога будет скрыто ", new ReplyKeyboardRemove());
    }

}
