package pro.sky.petshelterbot.handler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Handles user's pressing a button and sends a suitable menu to the user
 */
@Component
public class ShelterHandler {

    final private Logger logger = LoggerFactory.getLogger(ShelterHandler.class);
    final private TelegramBot telegramBot;
    final private ShelterInfoHandler shelterInfoHandler;

    public ShelterHandler(TelegramBot telegramBot, ShelterInfoHandler shelterInfoHandler) {
        this.telegramBot = telegramBot;
        this.shelterInfoHandler = shelterInfoHandler;
    }

    public void processCallbackQuery(CallbackQuery callbackQuery) {
        logger.info("processCallbackQuery({})-Method", callbackQuery);

        String data = callbackQuery.data();
        Message message = callbackQuery.message();
        long chatId = message.chat().id();
        int messageId = message.messageId();

        if (data.equals("shelter_info")) {
            shelterInfoHandler.sendShelterInfo(Long.toString(chatId));
            telegramBot.execute(new DeleteMessage(chatId, messageId));
        } else if (data.equals("cat_shelter") || data.equals("dog_shelter")) {
            handleShelterCommand(Long.toString(chatId));
            telegramBot.execute(new DeleteMessage(chatId, messageId));
        }
    }

    public void handleShelterCommand(String chatId) {
        // Create buttons
        InlineKeyboardButton button1 = new InlineKeyboardButton("Узнать информацию о приюте").callbackData("shelter_info");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Как взять животное из приюта").callbackData("adoption_info");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Прислать отчет о питомце").callbackData("report_info");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Позвать волонтера").callbackData("volunteer_info");


        // Create InlineKeyboardMarkup and give it buttons
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                new InlineKeyboardButton[] {button1},
                new InlineKeyboardButton[] {button2},
                new InlineKeyboardButton[] {button3},
                new InlineKeyboardButton[] {button4}
        );

        // Send buttons to user
        telegramBot.execute(new SendMessage(chatId, "Выберите действие:")
                .replyMarkup(markup));
    }
}