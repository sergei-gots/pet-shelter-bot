package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandler implements Handler{

    final protected Logger logger = LoggerFactory.getLogger(getClass());
    final protected TelegramBot telegramBot;

    protected AbstractHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
}
