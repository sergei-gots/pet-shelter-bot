package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
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
        super(telegramBot, adopterRepository, volunteerRepository, shelterRepository, userMessageRepository, buttonRepository, dialogRepository);
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
            sendMessage(chatId, "Подождите, волонтёр скоро свяжется с вами. Приношу извинения за ваше ожидание! " +
                    " Вы также можете снять заявку на диалог с волонтёром комадой " +
                    CANCEL_VOLUNTEER_CALL +  ". Спасибо)");
            return true;
        }

        sendDialogMessageToVolunteer(dialog, message.text());
        return true;


    }
    @Override
    public boolean handle(Message message, String key) {

        if(super.handle(message, key)) {
            return true;
        }

        switch(key) {
            case CALL_VOLUNTEER:
            case CALL_VOLUNTEER_ADOPTION_INFO_MENU:
            case CALL_VOLUNTEER_SHELTER_INFO_MENU:
                handleVolunteerCall(message, key);
                return true;
            default: return false;
        }
    }

    public void handleVolunteerCall(Message message, String key) {
        logger.debug("handleVolunteerCall(message={})", message);
        Adopter adopter = getAdopter(message);
        long chatId = adopter.getChatId();
        if (getDialogIfRequested(chatId) != null) {
            throw new IllegalStateException("Dialog for chatId=" + chatId + " is already requested");
        }
        createDialogRequest(adopter, key);
    }

    private void createDialogRequest(Adopter adopter, String key) {

        logger.trace("createDialogRequest(adopter={})", adopter);

        //Create Dialog Entry
        Dialog dialog = new Dialog(adopter);
        dialogRepository.save(dialog);

        if(CALL_VOLUNTEER_ADOPTION_INFO_MENU.equals(key)) {
            sendMenu(dialog.getAdopter(), ADOPTION_INFO_MENU);
        } else {
            sendMenu(dialog.getAdopter(), SHELTER_INFO_MENU);
        }

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
