package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.constants.DialogCommands;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.DialogRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;
import pro.sky.petshelterbot.repository.VolunteerRepository;
import pro.sky.petshelterbot.service.PetService;

/**
 * Handles commands receiving from user supposed
 * to be a volunteer.
 * Also operates chat between Adopter and Volunteer on the Volunteer's side
 **/

@Component
public abstract class AbstractDialogHandler extends AbstractHandler
        implements DialogCommands {
    final protected VolunteerRepository volunteerRepository;
    final protected DialogRepository dialogRepository;

    public AbstractDialogHandler(TelegramBot telegramBot,
                                 ShelterRepository shelterRepository,
                                 UserMessageRepository userMessageRepository,
                                 VolunteerRepository volunteerRepository, DialogRepository dialogRepository) {
        super(telegramBot, shelterRepository, userMessageRepository);
        this.volunteerRepository = volunteerRepository;
        this.dialogRepository = dialogRepository;
    }

    protected void sendDialogMessageToAdopter(Dialog dialog, String text) {
        logger.trace("sendDialogMessageToAdopter()-method.  adopter.first_name={}", dialog.getAdopter().getFirstName());
        sendMessage(dialog.getAdopter().getChatId(),
                dialog.getVolunteer().getFirstName() + "> " + text);
    }

    protected void sendDialogMessageToVolunteer(Dialog dialog, String text) {
        sendMessage(dialog.getAdopter().getChatId(),
                dialog.getVolunteer().getFirstName() + "> " + text);
    }

    public void sendJoinInvitationToVolunteerAndNotifyAdopter(Dialog dialog){
        Volunteer volunteer = dialog.getVolunteer();
        Adopter adopter = dialog.getAdopter();
        sendMessage(volunteer.getChatId(),
                volunteer.getFirstName() + "! C вами хотел бы связаться " + adopter.getFirstName() +
                        ". Нажмите кнопку 'Присоединиться к чату' для начала общения.",
                "Присоединиться к чату",
                JOIN_DIALOG
        );
        sendMessage(dialog.getAdopter().getChatId(), "Волонтёру отослано уведомление. Волонтёр свяжется с вами " +
                "насколько это возможно скоро. ");
    }
}
