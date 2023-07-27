package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.model.Update;
import pro.sky.petshelterbot.constants.PetShelterBotConstants;

public interface Handler extends PetShelterBotConstants {
    /**
     * Handles update.
     *
     * @param update Telegram bot update
     * @return if a command inside message's text
     * * is intended to be handled within the handler then returns true,
     * * if the command is not in scope of handled commands
     * * and message won't be handled then returns false
     */
    boolean handle(Update update);
}
