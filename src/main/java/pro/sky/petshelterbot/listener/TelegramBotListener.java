package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.handler.*;
import pro.sky.petshelterbot.handler.ShelterHandler;


import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotListener
        implements UpdatesListener, Handler {

    final private Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);
    final private TelegramBot telegramBot;

    final private Handler[] handlers;
    private final ShelterHandler shelterHandler;


    public TelegramBotListener(TelegramBot telegramBot,
                               VolunteerHandler volunteerHandler,
                               DevStageDBHandler catsDevStageHandler,
                               StartHandler startHandler,
                               DialogHandler dialogHandler,
                               ShelterHandler shelterHandler) {
        this.telegramBot = telegramBot;

        handlers = new Handler[]{
                //Important: volunteerHandler MUST BE at first place
                volunteerHandler,
                dialogHandler,
                startHandler,
                catsDevStageHandler
        };
        this.shelterHandler = shelterHandler;

    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            for (Update update : updates) {
                logger.debug("process(updates)-method: processing update: {}", update);

                if (!handle(update.message())) {
                    shelterHandler.handle(update.callbackQuery());
                }
            }
        } catch (Exception e) {
            logger.error("process(updates)-method: exception` e={} was caught", e, e);
            e.printStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public boolean handle(Message message) {
        if(message == null) {
            return false;
        }
        logger.debug("processMessage(Message message={})-method.", message);
        if (message.text() == null) {
            throw new IllegalArgumentException("Message.text() is null");
        }
        for (Handler handler : handlers) {
            if (handler.handle(message)) {
                return true;
            }
        }
        logger.trace("processMessage: there is no suitable handler for text=\"{}\" received from user={} with chat_id={}",
                message.text(), message.chat().firstName(), message.chat().id());
        return false;
    }
}
