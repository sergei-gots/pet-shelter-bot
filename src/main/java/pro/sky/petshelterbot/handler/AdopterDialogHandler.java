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


    public AdopterDialogHandler(TelegramBot telegramBot,
                                AdopterRepository adopterRepository,
                                ShelterRepository shelterRepository,
                                UserMessageRepository userMessageRepository,
                                ButtonRepository buttonRepository,
                                DialogRepository dialogRepository,
                                VolunteerRepository volunteerRepository
    ) {
        super(telegramBot, adopterRepository, shelterRepository, userMessageRepository, buttonRepository, volunteerRepository, dialogRepository);
    }


    @Override
    public boolean handle(Message message) {
        if(handle(message, message.text())) {
            return true;
        }

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

        return handle(callbackQuery.message(), callbackQuery.data());
    }

    @Override
    public boolean handle(Message message, String key) {

        if(getAdopter(message).getShelter() == null) {
            return false;
        }
        switch(key) {
            case CALL_VOLUNTEER:
                handleVolunteerCall(message);
                return true;
            default: return super.handle(message, key);
        }
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

        showShelterInfoMenu(dialog.getAdopter());

        //Get List of Available Volunteers
        List<Volunteer> availableVolunteers =
                volunteerRepository.findByShelterAndAvailableIsTrue(adopter.getShelter());

        if (availableVolunteers.size() == 0) {
            //If there isn't any available Volunteer out
            sendMessage(adopter.getChatId(), "В настоящий момент все волонтёры заняты. " +
                     "Как только один из волонтёров освободится, он свяжется с вами. " +
                    getUserMessage(MessageKey.WAIT_FOR_VOLUNTEER_BOT_RESTRICTIONS)
            );
            return;
        }

        //Else notify all the available volunteers about new dialog request
        for (Volunteer volunteer : availableVolunteers) {
            long chatId = volunteer.getChatId();
            sendMessage(chatId,
                    volunteer.getFirstName() + "! C вами хотел бы связаться " + adopter.getFirstName());
            sendMenu(volunteer, JOIN_DIALOG);
        }
        sendMessage(adopter.getChatId(), "Волонтёру отослано уведомление. Волонтёр свяжется с вами " +
                "насколько это возможно скоро. ");
    }
}
