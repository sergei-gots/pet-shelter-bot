package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.handler.*;
import pro.sky.petshelterbot.handler.ShelterInfoHandler;


import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotListener
        implements UpdatesListener, Handler {

    final private Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);
    final private TelegramBot telegramBot;

    final private Handler[] handlers;

    /**
     * @param volunteerDialogHandler to developer inside the constructor:
     *                         volunteerHandler must be placed at first place within
     *                         list of handlers: first of all we check
     *                         if the update we have to handle is sent by volunteer.
     */
    public TelegramBotListener(TelegramBot telegramBot,
                               VolunteerDialogHandler volunteerDialogHandler,
                               AdopterInputHandler adopterInputHandler,
                               AdopterDialogHandler adopterDialogHandler,
                               ShelterInfoHandler shelterInfoHandler
                               ) {
        this.telegramBot = telegramBot;

        handlers = new Handler[]{
                //Important: volunteerHandler MUST BE at first place
                volunteerDialogHandler,
                //then - adopter dialog handler
                adopterDialogHandler,
                //then - adopterInputHandler,
                adopterInputHandler,
                //the last one should be shelterInfoHandler
                shelterInfoHandler
        };
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
                    handle(update.callbackQuery());
                }
            }
        } catch (Exception e) {
            logger.error("process(updates)-method: exception` e={} was caught", e, e);
            e.printStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public boolean handle(CallbackQuery callbackQuery) {
        if (callbackQuery.data() == null) {
            logger.debug("handle(CallbackQuery=null)");
            return false;
        }

        for (Handler handler : handlers) {
            if (handler.handle(callbackQuery)) {
                return true;
            }
        }
        return false;
    }

    /** @return false if and only if the message is null
     * otherwise it will be assumed that the message is handled
     * and further handling is not necessary
     *
     * @throws IllegalArgumentException if message.text() is null
     */
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
        telegramBot.execute(new SendMessage(message.chat().id(), "Воспользуйтесь пунктами меню или напишите " +
                "и отправьте команду '/меню' или '/menu', если меню далеко вверху."));
        logger.trace("processMessage: there is no suitable handler for text=\"{}\" received from user={} with chat_id={}",
                message.text(), message.chat().firstName(), message.chat().id());
        return true;
    }
}
