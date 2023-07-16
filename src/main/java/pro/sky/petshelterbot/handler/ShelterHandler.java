package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.exceptions.ShelterException;
import pro.sky.petshelterbot.repository.AdopterRepository;
import pro.sky.petshelterbot.repository.ButtonRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

/**
 * Handles user's pressing a button and sends a suitable menu to the user
 */
@Component
public class ShelterHandler extends AbstractHandler {

    private final ShelterInfoHandler shelterInfoHandler;
    private final AdopterDialogHandler dialogHandler;


    public ShelterHandler(TelegramBot telegramBot,

                          AdopterRepository adopterRepository,
                          ShelterRepository shelterRepository,
                          UserMessageRepository userMessageRepository,
                          ButtonRepository buttonsRepository,

                          ShelterInfoHandler shelterInfoHandler,
                          AdopterDialogHandler volunteerChatHandler) {
        super(telegramBot, adopterRepository, shelterRepository, userMessageRepository, buttonsRepository);
        this.shelterInfoHandler = shelterInfoHandler;
        this.dialogHandler = volunteerChatHandler;
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

            if (processStartMenu(adopter, shelterId, key)) {
                return true;
            }

            if (processShelterInfoMenu(adopter, shelterId, key)) {
                return true;
            }


            if (dialogHandler.handle(callbackQuery, message, key, chatId, shelterId)) {
                return true;
            }
            if (shelterInfoHandler.handle(message, key, chatId, shelterId)) {
                return true;
            }
            sendUserMessage(adopter, key, shelterId);

        } catch (Exception e) {
            logger.error("handle(CallBackQuery)-method: exception  was thrown. ", e);
        }
        return false;
    }

    private boolean processShelterInfoMenu(Adopter adopter, Long shelterId, String key) {
        if(!SHELTER_INFO_MENU.equals(key)) {
            return false;
        }
        logger.debug("processShelterInfoMenu(...)");
        deletePreviousMenu(adopter);
        return makeButtonList(adopter, shelterId, key);
    }

    private boolean processStartMenu(Adopter adopter, Long shelterId, String key) {
        if(!START_MENU.equals(key)) {
            return false;
        }

        logger.debug("processStartMenu(...)");
        deletePreviousMenu(adopter);

        sendMessage(adopter.getChatId(), "Вы выбрали шелтер \"<b>"
                + shelterRepository
                .findById(shelterId)
                .orElseThrow(() -> new ShelterException("There is no shelter with id=" + shelterId + " in db.))"))
                .getName() + "</b>\"");
        return makeButtonList(adopter, shelterId, SHELTER_INFO_MENU);
    }



}