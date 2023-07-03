package pro.sky.petshelterbot.handler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

/**
 * Handles '/start' and sends to the user welcome message and menu
 */
@Component
public class StartHandler extends AbstractHandler {

    private final TelegramBot telegramBot;

    public StartHandler(TelegramBot telegramBot) {
        super(telegramBot);
        this.telegramBot = telegramBot;
    }

    /** handles command '/start' */
    public boolean handle(Message message) {
        if (!message.text().equals("/start")) {
            return false;
        }
        SendMessage welcomeMessage = new SendMessage(message.chat().id(), "Здравствуйте, " + message.chat().firstName());
        telegramBot.execute(welcomeMessage);

        // Create buttons to choose shelter
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Приют для кошек").callbackData("cat_shelter"),
                new InlineKeyboardButton("Приют для собак").callbackData("dog_shelter"));

        // Send buttons to user
        telegramBot.execute(new SendMessage(message.chat().id(), "Выберите приют:")
                .replyMarkup(markup));

        return true;
    }
}