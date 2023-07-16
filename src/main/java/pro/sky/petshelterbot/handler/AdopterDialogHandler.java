package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Shelter;
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
                                ShelterRepository shelterRepository,
                                UserMessageRepository userMessageRepository,
                                ButtonRepository buttonRepository,
                                DialogRepository dialogRepository,
                                AdopterRepository adopterRepository,
                                VolunteerRepository volunteerRepository
    ) {
        super(telegramBot, shelterRepository, userMessageRepository, buttonRepository, volunteerRepository, dialogRepository);
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
    public boolean handle(CallbackQuery callbackQuery, Message message, String key, Long chatId, Long shelterId) {
        if (CALL_VOLUNTEER.equals(key)) {
            handleVolunteerCall(callbackQuery, message, chatId, shelterId);
            return true;
        }
        return false;
    }

    public void handleVolunteerCall(CallbackQuery callbackQuery, Message message, Long chatId, Long shelterId) {
        logger.debug("handleVolunteerCall(chatId={}, shelterId={})", chatId, shelterId);
        if (getDialogIfRequested(chatId) != null) {
            throw new IllegalStateException("Dialog for chatId=" + chatId + " is already requested");
        }
        createDialogRequest(message, chatId, shelterId);
    }

    private void createDialogRequest(Message message, Long chatId, Long shelterId) {

        logger.trace("createDialogRequest(message={}, ...", message);

        //Get Adopter if exists or Create a New Adopter Entry
        Adopter adopter = adopterRepository.findByChatId(chatId)
                .orElse(adopterRepository.save(new Adopter(chatId, message.chat().firstName()))
                );

        //Create Dialog Entry
        Shelter shelter = getShelter(shelterId);
        Dialog dialog = new Dialog(adopter, shelter);
        dialogRepository.save(dialog);

        //Get List of Available Volunteers
        List<Volunteer> availableVolunteers = volunteerRepository.findByShelterAndAvailableIsTrue(shelter);

        if (availableVolunteers.size() == 0) {
            //If there isn't any available Volunteer out
            sendMessage(chatId, "В настоящий момент все волонтёры заняты. "
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

    private Dialog getDialogIfRequested(long chatId) {
        return dialogRepository.findByAdopterChatId(chatId).orElse(null);
    }

}
