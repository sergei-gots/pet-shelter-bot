package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractUpdateHandler implements Handler {

    final protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Handles update.
     * @param update Telegram bot update
     * @return if a command inside message's text
     *      * is intended to be handled within the handler then returns true,
     *      * if the command is not in scope of handled commands
     *      * and message won't be handled then returns false
     */
    public boolean handle(Update update)                 {
        Message message = update.message();
        if (message == null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            return handleCallbackQuery(callbackQuery.message(), callbackQuery.data());
        }
        if (handleImg(update)) {
              return true;
        }
        if (message.text() == null) {
            logger.warn("handle: current Update cannot be handled within this class");
            return false;
        }
        if (message.text().startsWith("/")) {
            return handleCallbackQuery(message, message.text());
        }
        return handleMessage(message);
    }

    protected boolean handleMessage(Message message) { return false; }
    protected boolean handleCallbackQuery(Message message, String key) { return false; }
    protected boolean handleImg(Update update) { return false; }

}
