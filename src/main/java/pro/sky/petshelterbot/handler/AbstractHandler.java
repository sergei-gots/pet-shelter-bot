package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandler implements Handler{

    final protected Logger logger = LoggerFactory.getLogger(getClass());
    final protected TelegramBot telegramBot;

    protected AbstractHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    protected void sendMessage(Long chatId, String text) {
        logger.trace("sendMessage(chatId={}, text=\"{}\")", chatId, text);
        SendMessage sendMessage = new SendMessage(chatId, text);
        telegramBot.execute(sendMessage);
    }

    protected void sendMessage(Long chatId, String text, String buttonLabel, String callbackData) {
        logger.trace("sendMessage(chatId={}, text=\"{}\", buttonLabel=\"{}\", callbackData=\"{}\")",
                chatId, text, buttonLabel, callbackData);
        telegramBot.execute(new SendMessage(
                chatId,
                text)
                .replyMarkup(new InlineKeyboardMarkup(
                        new InlineKeyboardButton(buttonLabel).callbackData(callbackData)
                )));
    }

}
