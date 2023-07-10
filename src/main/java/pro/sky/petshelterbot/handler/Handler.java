package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.model.Message;

public interface Handler {
    /**
     * Handles message.
     * @param message Telegram bot message
     * @return if a command inside message's text
     *      * is intended to be handled within the handler then returns true,
     *      * if the command is not in scope of handled commands
     *      * and message won't be handled then returns false
     */
    default boolean handle(Message message)  { return false; };
    default boolean handle(String key, Long chatId, Long shelterId) { return false; };
}
