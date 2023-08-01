package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.*;
import pro.sky.petshelterbot.repository.*;

import java.util.Collection;

import static pro.sky.petshelterbot.constants.PetShelterBotConstants.MessageKey.VOLUNTEER_IS_NOTIFIED;

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

    public void sendHandshakes(Dialog dialog) {
        Volunteer volunteer = dialog.getVolunteer();
        Adopter adopter = dialog.getAdopter();

        sendMessage(volunteer.getChatId(),
                volunteer.getFirstName() + "! C вами хотел бы связаться " + adopter.getFirstName() +
                        ". Нажмите кнопку 'Присоединиться к чату' для начала общения.");
        sendMenu(volunteer, JOIN_DIALOG);
        sendUserMessage(adopter, VOLUNTEER_IS_NOTIFIED);
    }

}
