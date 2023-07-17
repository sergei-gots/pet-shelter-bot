package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.exceptions.ShelterException;
import pro.sky.petshelterbot.repository.AdopterRepository;
import pro.sky.petshelterbot.repository.ButtonRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.Collection;

/**
 * Handles user's pressing a button and sends a suitable menu to the user
 */
@Component
public class ShelterInfoHandler extends AbstractHandler {

    private final AdopterDialogHandler dialogHandler;


    public ShelterInfoHandler(TelegramBot telegramBot,

                              AdopterRepository adopterRepository,
                              ShelterRepository shelterRepository,
                              UserMessageRepository userMessageRepository,
                              ButtonRepository buttonsRepository,

                              AdopterDialogHandler adopterDialogHandler) {
        super(telegramBot, adopterRepository, shelterRepository, userMessageRepository, buttonsRepository);
        this.dialogHandler = adopterDialogHandler;
    }

    @Override
    public boolean handle(Message message) {
        if (handleStartOrReset(message, message.text())) {
            return true;
        }
        return processCommands(getAdopter(message), message.text());

    }

    private boolean handleStartOrReset(Message message, String key) {
        Adopter adopter = getAdopter(message);

        if(adopter.getShelter() == null) {
            processStart(adopter);
            return true;
        }

        switch(key) {
            case RESET_SHELTER:
            case RESET_SHELTER_RU:
                processResetShelter(adopter);
            case START:
                processStart(adopter);
                return true;
        }
        return false;
    }

    @Override
    public boolean handle(CallbackQuery callbackQuery) {

            logger.debug("handle(CallbackQuery)-method");
            Message message = callbackQuery.message();
            String key = callbackQuery.data();
            Long chatId = message.chat().id();
            Adopter adopter = getAdopter(message);

            logger.debug("handle(CallbackQuery): callbackQuery{key=\"{}\"", key);

            if (key.startsWith(SHELTER_CHOICE)) {
                processShelterChoice(adopter, key);
                return true;
            }
            if (handleStartOrReset(message, key)) {
                return true;
            }
            if (dialogHandler.handle(callbackQuery)) {
                return true;
            }
            if (processCommands(adopter, key)) {
                return true;
            }
            sendUserMessage(adopter, key);
            return true;
        }




    private void processShelterChoice(Adopter adopter, String key) {

        logger.debug("processShelterChoice(adopter={}, key=\"{}\")", adopter, key);
        long shelterId;
        try {
            shelterId = Long.parseLong(key.substring(SHELTER_CHOICE.length()));
        }
        catch(NumberFormatException e) {
            logger.error("processShelterChoice(): invalid key=\"{}\" to parse as shelter_id",
                    key, e);
            return;
        }

        Shelter shelter = shelterRepository
                .findById(shelterId)
                .orElseThrow(() -> new ShelterException("There is no shelter with id=" + shelterId + " in db.))"));

        adopter.setShelter(shelter);
        adopterRepository.save(adopter);

        sendMessage(adopter.getChatId(), "Вы выбрали шелтер \"<b>"
                + shelter.getName() + "</b>\"");

        showShelterInfoMenu(adopter);
    }

    private void processResetShelter(Adopter adopter) {
        dialogHandler.handleCancelVolunteerCall(adopter);
        adopter.setShelter(null);
        adopterRepository.save(adopter);
    }

    public boolean processCommands(Adopter adopter, String key) {
        logger.trace("processCommands(adopter={}, key=\"{}\")",
                adopter, key);

        switch(key) {
            case RESET_SHELTER:
            case RESET_SHELTER_RU:
                adopter.setShelter(null);
                adopterRepository.save(adopter);
            case START:
                processStart(adopter);
                return true;
            case SHELTER_INFO_MENU:
            case MENU:
            case MENU_RU:
                sendMenu(adopter, SHELTER_INFO_MENU);
                return true;
            case ADOPTION_INFO_MENU:
                sendMenu(adopter, ADOPTION_INFO_MENU);
                return true;
            case ABOUT_SHELTER_INFO:
                sendUserMessage(adopter, key);
                return true;
            case OPENING_HOURS_AND_ADDRESS_INFO:
                sendOpeningHours(adopter);
                return true;
            case SECURITY_INFO:
                sendSecurityInfo(adopter);
                return true;
        }
        return false;
    }

    /** Sends information about shelter's opening hours
     */
    public void sendOpeningHours(Adopter adopter) {
        logger.trace("sendOpeningHours");
        Shelter shelter = adopter.getShelter();
        sendMessage(adopter.getChatId(), "<u>Расписание работы и адрес приюта</u>:\n" +
                shelter.getWorkTime() + "\n" +
                "Адрес: " + shelter.getAddress());
    }

    /** Sends security contact information to user
     */
    public void sendSecurityInfo(Adopter adopter) {
        logger.trace("sendSecurityInfo");
        Shelter shelter = adopter.getShelter();
        sendMessage(adopter.getChatId(), "<u>Контактные данные охраны приюта</u>:\n" +
                "Телефон: " + shelter.getTel() + "\n" +
                "Email: " + shelter.getEmail());
    }

    public void processStart(Adopter adopter) {
        sendMessage(adopter.getChatId(), "Здравствуйте, " + adopter.getFirstName());
        if(adopter.getShelter() == null) {
            showShelterChoiceMenu(adopter);
        }
        else {
            showShelterInfoMenu(adopter);
        }
    }
    public void showShelterChoiceMenu(Adopter adopter) {
        Collection<Shelter> shelters = shelterRepository.findAll();
        // Create buttons to choose shelter
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Shelter shelter : shelters) {
            markup.addRow(
                    new InlineKeyboardButton(shelter.getName())
                            .callbackData(SHELTER_CHOICE + shelter.getId().toString())
            );
        }
        sendMenu(adopter, "Выберите приют:", markup);
    }

}