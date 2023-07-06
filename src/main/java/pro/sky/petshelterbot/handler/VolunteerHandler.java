package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.service.PetService;

/**
 * Handles commands receiving from user supposed
 * to be a volunteer.
 * All the commands starts with "/volunteer-role
 */
@Component
public class VolunteerHandler extends AbstractHandler {
    final private PetService petService;

    public VolunteerHandler(TelegramBot telegramBot, PetService catService) {
        super(telegramBot);
        this.petService = catService;
    }

    @Override
    public boolean handle(Message message) {

        String text = message.text();
        if (!text.startsWith("/volunteer-role-")) {
            return false;
        }

        Chat chat = message.chat();
        logger.info("- Received {} command from user {}", text, chat.firstName());

        if (text.equals("/volunteer-role-want-to-be-a-volunteer")) {
            logger.info("");
            SendMessage testCatAddedMessage = new SendMessage(chat.id(), "---");
            telegramBot.execute(testCatAddedMessage);
        }
        return true;
    }
}
