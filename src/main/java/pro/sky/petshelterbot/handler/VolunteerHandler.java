package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
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
 * Also operates chat between Adopter and Volunteer on the Volunteer's side
 *
 **/

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

    @Override
    public boolean handle(CallbackQuery callbackQuery) {
        Volunteer volunteer = volunteerRepository.findByChatId(callbackQuery.message().chat().id()).orElse(null);
        if(volunteer == null) {
            return false;
        }
        logger.info("handle(callbackQuery)-method.  callbackQuery.data={}", callbackQuery.data());
        Dialog dialog = dialogRepository.findByVolunteer(volunteer)
                .orElseThrow(()-> new IllegalStateException("No dialog found for volunteer=" + volunteer));

        Adopter adopter = dialog.getAdopter();
        if(Volunteer.JOIN_CHAT.equals(callbackQuery.data())) {
            sendMessage(adopter.getChatId(),
                    volunteer.getFirstName() + "> " + adopter.getFirstName() + ", здравствуйте! Расскажите, какой у вас вопрос?");
            sendMessage(volunteer.getChatId(), "Отлично, вы в чате. Пользователю направлено приветствие и предложение сформировать интересующий вопрос.");
            return true;
        }
        return false;
    }

    private void forwardMessageToUser(Dialog dialog, String text) {
        Adopter adopter = dialog.getAdopter();
        logger.debug("forwardMessageToUser()-method.  adopter.first_name={}", adopter.getFirstName());
        sendMessage(adopter.getChatId(), dialog.getVolunteer().getFirstName() + "> " + text);
    }

    private void closeDialog(Dialog dialog) {
        logger.debug("closeDialog()-method.  adopter.first_name={}", dialog.getAdopter().getFirstName());
        Volunteer volunteer = dialog.getVolunteer();
        dialogRepository.delete(dialog);
    }
}
