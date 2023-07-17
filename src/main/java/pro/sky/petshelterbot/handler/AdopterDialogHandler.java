package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.*;

import java.util.List;

/**
 * Operates chat between Adopter and Volunteer on the Adopter's side
 */
@Component
public class AdopterDialogHandler extends AbstractDialogHandler {

    private final AdopterRepository adopterRepository;

    public AdopterDialogHandler(TelegramBot telegramBot,
                                AdopterRepository adopterRepository,
                                ShelterRepository shelterRepository,
                                UserMessageRepository userMessageRepository,
                                ButtonRepository buttonRepository,
                                DialogRepository dialogRepository,
                                VolunteerRepository volunteerRepository
    ) {
        super(telegramBot, adopterRepository, shelterRepository, userMessageRepository, buttonRepository, volunteerRepository, dialogRepository);
        this.adopterRepository = adopterRepository;
    }


    @Override
    public boolean handle(Message message) {
        Long chatId = message.chat().id();
        Dialog dialog = getDialogIfRequested(message.chat().id());
        if (dialog == null) {
            return false;
        }

        if (dialog.getVolunteer() == null) {
            sendMessage(chatId, "Подождите, волонтёр скоро свяжется с вами. Приношу извинения за ваше ожидание! Спасибо)");
            return true;
        }

        sendDialogMessageToVolunteer(dialog, message.text());
        return true;


    }

    @Override
    public boolean handle(CallbackQuery callbackQuery) {

        if (CALL_VOLUNTEER.equals(callbackQuery.data())) {
            handleVolunteerCall(callbackQuery.message());
            return true;
        }
        return false;
    }

    public void handleVolunteerCall(Message message) {
        logger.debug("handleVolunteerCall(message={})", message);
        Adopter adopter = getAdopter(message);
        long chatId = adopter.getChatId();
        if (getDialogIfRequested(chatId) != null) {
            throw new IllegalStateException("Dialog for chatId=" + chatId + " is already requested");
        }
        createDialogRequest(adopter);
    }

    private void createDialogRequest(Adopter adopter) {

        logger.trace("createDialogRequest(adopter={})", adopter);

        //Create Dialog Entry
        Dialog dialog = new Dialog(adopter);
        dialogRepository.save(dialog);

        //Get List of Available Volunteers
        List<Volunteer> availableVolunteers =
                volunteerRepository.findByShelterAndAvailableIsTrue(adopter.getChatShelter());

        if (availableVolunteers.size() == 0) {
            //If there isn't any available Volunteer out
            sendMessage(adopter.getChatId(), "В настоящий момент все волонтёры заняты. "
                    + "Как только один из волонтёров освободится, он свяжется с вами.");
            return;
        }

        //Else notify all the available volunteers about new dialog request
        for (Volunteer volunteer : availableVolunteers) {
            sendMessage(volunteer.getChatId(),
                    volunteer.getFirstName() + "! C вами хотел бы связаться " + adopter.getFirstName() +
                            ". Нажмите кнопку 'Присоединиться к чату' для начала общения.",
                    "Присоединиться к чату",
                    JOIN_DIALOG
            );
        }
        sendMessage(adopter.getChatId(), "Волонтёру отослано уведомление. Волонтёр свяжется с вами " +
                "насколько это возможно скоро. ");
    }

}
