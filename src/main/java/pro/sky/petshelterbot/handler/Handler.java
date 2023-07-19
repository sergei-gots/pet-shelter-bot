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
    default boolean handle(Update update)                 { return false; }
    default boolean handle(CallbackQuery callbackQuery)   { return false; }
    default boolean handle(Message message)  { return false; }

    default boolean handle(Message message, String key) { return false; }

}
