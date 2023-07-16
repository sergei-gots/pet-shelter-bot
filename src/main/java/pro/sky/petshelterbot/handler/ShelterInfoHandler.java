package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.exceptions.ShelterException;
import pro.sky.petshelterbot.repository.AdopterRepository;
import pro.sky.petshelterbot.repository.ButtonRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

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
    public boolean handle(CallbackQuery callbackQuery) {

        logger.debug("handle(CallbackQuery)-method");
        String queryData = callbackQuery.data();
        Message message = callbackQuery.message();
        Long chatId = message.chat().id();
        Adopter adopter = getAdopter(message);

        try {
            String[] queryDataArray = queryData.split("-");
            Long shelterId = Long.parseLong(queryDataArray[0]);

            String key = queryDataArray[1];
            logger.debug("handle(CallbackQuery): callbackQuery{shelter_id={}, key=\"{}\"", shelterId, key);

            if(dialogHandler.handle(callbackQuery, message, key, chatId, shelterId)) {
                return true;
            }
            if(processCommands(adopter, key, shelterId)) {
                return true;
            }
            sendUserMessage(adopter, key, shelterId);
            return true;

        } catch (Exception e) {
            logger.error("handle(CallBackQuery)-method: exception  was thrown. ", e);
        }
        return false;
    }

    private void processShelterInfoMenu(Adopter adopter, String key, Long shelterId) {
        logger.debug("processShelterInfoMenu(...)");
        deletePreviousMenu(adopter);
        makeButtonList(adopter, key, shelterId);
    }

    private void processStartMenu(Adopter adopter, String key, Long shelterId) {

        logger.debug("processStartMenu(...)");
        deletePreviousMenu(adopter);

        sendMessage(adopter.getChatId(), "Вы выбрали шелтер \"<b>"
                + shelterRepository
                .findById(shelterId)
                .orElseThrow(() -> new ShelterException("There is no shelter with id=" + shelterId + " in db.))"))
                .getName() + "</b>\"");
        makeButtonList(adopter, SHELTER_INFO_MENU, shelterId);
    }


    public boolean processCommands(Adopter adopter, String key, Long shelterId) {
        logger.trace("processCommands");
        Long chatId = adopter.getChatId();
        switch(key) {
            case START_MENU:
                processStartMenu(adopter, key, shelterId);
                return true;
            case SHELTER_INFO_MENU:
                processShelterInfoMenu(adopter, key, shelterId);
                return true;
            case ADOPTION_INFO_MENU:
                processAdoptionInfoMenu(adopter, key, shelterId);
                return true;
            case ABOUT_SHELTER_INFO:
                sendUserMessage(adopter, key, shelterId);
                return true;
            case OPENING_HOURS_AND_ADDRESS_INFO:
                sendOpeningHours(chatId, shelterId);
                return true;
            case SECURITY_INFO:
                sendSecurityInfo(chatId, shelterId);
                return true;
        }
        return false;
    }

    private void processAdoptionInfoMenu(Adopter adopter, String key, Long shelterId) {
        logger.trace("processAdoptionInfoMenu");
        deletePreviousMenu(adopter);
        makeButtonList(adopter, key, shelterId);
    }


    /** Sends information about shelter's opening hours
     */
    public void sendOpeningHours(Long chatId, Long shelterId) {
        logger.trace("sendOpeningHours");
        Shelter shelter = getShelter(shelterId);
        sendMessage(chatId, "<u>Расписание работы и адрес приюта</u>:\n" +
                shelter.getWorkTime() + "\n" +
                "Адрес: " + shelter.getAddress());
    }

    /** Sends security contact information to user
     */
    public void sendSecurityInfo(Long chatId, Long shelterId) {
        logger.trace("sendSecurityInfo");
        Shelter shelter = getShelter(shelterId);
        sendMessage(chatId, "<u>Контактные данные охраны приюта</u>:\n" +
                "Телефон: " + shelter.getTel() + "\n" +
                "Email: " + shelter.getEmail());
    }
}