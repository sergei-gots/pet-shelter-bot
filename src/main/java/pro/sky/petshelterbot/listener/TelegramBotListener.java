package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.handler.Handlers;
import pro.sky.petshelterbot.util.Logged;


import javax.annotation.PostConstruct;
import java.util.List;


@Service
public class TelegramBotListener
    extends Logged
        implements UpdatesListener {
    final private TelegramBot telegramBot;

    final private Handlers handlers;

    public TelegramBotListener(TelegramBot telegramBot,
                               Handlers handlers
                               ) {
        this.telegramBot = telegramBot;
        this.handlers = handlers;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            for (Update update : updates) {
                handlers.handle(update);
            }
        } catch (Exception e) {
            logger.error("process(updates): exception was thrown", e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
