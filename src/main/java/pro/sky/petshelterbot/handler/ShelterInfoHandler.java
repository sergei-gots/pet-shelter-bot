package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Handles user's pressing a button and sends information about the shelter
 */
@Component
public class ShelterInfoHandler {

    final private Logger logger = LoggerFactory.getLogger(ShelterInfoHandler.class);
    final private TelegramBot telegramBot;

    public ShelterInfoHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processCallbackQuery(CallbackQuery callbackQuery) {
        logger.info("processCallbackQuery({})-Method", callbackQuery);

        String data = callbackQuery.data();
        Message message = callbackQuery.message();
        long chatId = message.chat().id();

        if (data.equals("shelter_info")) {
            sendShelterInfo(Long.toString(chatId));
        }
    }
    public void sendShelterInfo(String chatId) {
        // Create buttons
        InlineKeyboardButton button1 = new InlineKeyboardButton("Расписание работы и адрес").callbackData("schedule_info");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Контактные данные охраны").callbackData("security_info");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Рекомендации по безопасности").callbackData("safety_info");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Оставить контактные данные").callbackData("contact_info");
        InlineKeyboardButton button5 = new InlineKeyboardButton("Позвать волонтера").callbackData("volunteer_info");

        // Create InlineKeyboardMarkup and give it buttons
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                new InlineKeyboardButton[] {button1},
                new InlineKeyboardButton[] {button2},
                new InlineKeyboardButton[] {button3},
                new InlineKeyboardButton[] {button4},
                new InlineKeyboardButton[] {button5}
        );

        // Send buttons to user
        telegramBot.execute(new SendMessage(chatId, "Выберите, что вас интересует:")
                .replyMarkup(markup));
    }


}
