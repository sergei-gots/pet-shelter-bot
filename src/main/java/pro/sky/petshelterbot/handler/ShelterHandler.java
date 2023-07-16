package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.exceptions.ShelterException;
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
                          ShelterRepository shelterRepository,
                          ShelterInfoHandler shelterInfoHandler,
                          AdopterDialogHandler volunteerChatHandler,
                          UserMessageRepository userMessageRepository,
                          ButtonRepository buttonsRepository) {
        super(telegramBot, shelterRepository, userMessageRepository, buttonsRepository);
        this.shelterInfoHandler = shelterInfoHandler;
        this.dialogHandler = volunteerChatHandler;
    }

    @Override
    public boolean handle(CallbackQuery callbackQuery) {


        logger.debug("handle(CallbackQuery)-method");
        String queryData = callbackQuery.data();
        Message message = callbackQuery.message();
        Long chatId = message.chat().id();



        try {
            String[] queryDataArray = queryData.split("-");
            Long shelterId = Long.parseLong(queryDataArray[0]);

            String key = queryDataArray[1];
            logger.debug("handle(CallbackQuery)-method: callbackQuery.queryData() contains Long shelter_id={} and String key={}", shelterId, key);

            if(START_INFO_MENU.equals(key)) {
                DeleteMessage deleteMessage = new DeleteMessage(chatId, message.messageId());
                telegramBot.execute(deleteMessage);

                sendMessage(chatId, "Вы выбрали шелтер \"<b>"
                        + shelterRepository
                        .findById(shelterId)
                        .orElseThrow(() -> new ShelterException("There is no shelter with id=" + shelterId + " in db.))"))
                        .getName() + "</b>\"");
            }


            if (makeButtonList(chatId, shelterId, key, "Выберите, что вас интересует:")) {
                return true;
            }
            if (dialogHandler.handle(callbackQuery, message, key, chatId, shelterId)) {
                return true;
            }
            if (shelterInfoHandler.handle(message, key, chatId, shelterId)) {
                return true;
            }
            sendUserMessage(chatId, key, shelterId);

        } catch (Exception e) {
            logger.error("handle(CallBackQuery)-method: exception  was thrown. ", e);
        }
        return false;
    }

    /** Retrieves message by (key, shelter_id) from the table containing user_messages and send
     * it to the user. If message is not found user will be notified that developers have been
     * working on fixing it.
     * @param chatId - chat id to send
     * @param key - user_messages.key for message
     * @param shelterId - user_message.shelter_id for message
     */
    private void sendUserMessage(Long chatId, String key, Long shelterId) {
        logger.trace("sendUserMessage(chatId={}, key=\"{}\", shelterId={}",
                key, chatId, shelterId);
        String userMessage = getUserMessage(key, shelterId);
        if (userMessage == null) {
            userMessage = "Раздел не создан. Разработчики скоро сформируют этот раздел";
            logger.error("processCallBackQuery-method: user_message with shelter_id={}, key={} is not listed.",
                    shelterId, key);
        }
        telegramBot.execute(new SendMessage(chatId, userMessage).parseMode(ParseMode.HTML));
    }


}