package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import pro.sky.petshelterbot.constants.ChapterNames;
import pro.sky.petshelterbot.constants.Commands;
import pro.sky.petshelterbot.constants.TelegramChatStates;

public interface Handler extends ChapterNames, Commands, TelegramChatStates {
    /**
     * Handles update.
     * @param update Telegram bot update
     * @return if a command inside message's text
     *      * is intended to be handled within the handler then returns true,
     *      * if the command is not in scope of handled commands
     *      * and message won't be handled then returns false
     */
    default boolean handle(Update update)                 {
        Message message = update.message();
        if (message == null) {
            return handle(update.callbackQuery());
        }
        if (handlePhoto(message)) {
              return true;
        }
        if (message.text() == null) {
            warn("handle: current Update cannot be handled within this class");
            return false;
        }
        return handle(message, message.text());
    }
    default  boolean handle(CallbackQuery callbackQuery)   {
        return handle(callbackQuery.message(), callbackQuery.data());
    }
    default boolean handle(Message message)  {
        return handle(message, message.text());
    }

    default boolean handle(Message message, String key) { return false; }

    default boolean handlePhoto(Message message) { return false; }

    /**
     *  Prints warning-message
     *
     */
    default void warn(String s) {
        System.out.println(getClass() + ": " + s);
    }

}
