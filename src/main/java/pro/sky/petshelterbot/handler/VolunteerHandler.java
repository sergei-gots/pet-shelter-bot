package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.DialogRepository;
import pro.sky.petshelterbot.repository.VolunteerRepository;
import pro.sky.petshelterbot.service.PetService;

/**
 * Handles commands receiving from user supposed
 * to be a volunteer.
 * All the commands starts with "/volunteer-role
 */
@Component
public class VolunteerHandler extends AbstractHandler {
    final private PetService petService;
    final private VolunteerRepository volunteerRepository;
    final private DialogRepository dialogRepository;

    public VolunteerHandler(TelegramBot telegramBot, PetService catService, VolunteerRepository volunteerRepository, DialogRepository dialogRepository) {
        super(telegramBot);
        this.petService = catService;
        this.volunteerRepository = volunteerRepository;
        this.dialogRepository = dialogRepository;
    }

    @Override
    public boolean handle(Message message) {

        Long chatId = message.chat().id();
        Volunteer volunteer = volunteerRepository
                .findByChatId(chatId).orElse(null);

        if(volunteer == null) {
            return false;
        }

        logger.debug("handle(message)- message from volunteer={} received", volunteer.getFirstName());

        String text = message.text();

        Dialog dialog = dialogRepository.findByVolunteer(volunteer)
                .orElseThrow(()->new IllegalStateException("No dialog found for a " + volunteer));

        if("/close".equals(text)) {
            closeDialog(dialog);
        }
        else {
            forwardMessageToUser(dialog, text);
        }
        return true;
    }

    private void forwardMessageToUser(Dialog dialog, String text) {
        Adopter adopter = dialog.getAdopter();
        logger.info("forwardMessageToUser()-method.  adopter.first_name={}", adopter.getFirstName());
        sendMessage(adopter.getChatId(), text);
    }

    private void closeDialog(Dialog dialog) {
        logger.info("closeDialog()-method.  adopter.first_name={}", dialog.getAdopter().getFirstName());
        Volunteer volunteer = dialog.getVolunteer();
        dialogRepository.delete(dialog);

        /*if(isThereAdopterWaiting()) {
            openDialogWithAdopterWaiting();
        }*/
    }
}
