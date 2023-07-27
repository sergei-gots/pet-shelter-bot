package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.handler.*;
import pro.sky.petshelterbot.handler.ShelterInfoHandler;


import javax.annotation.PostConstruct;
import java.util.List;


@Service
public class TelegramBotListener
        implements UpdatesListener {

    final private Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);
    final private TelegramBot telegramBot;
    final private Handler[] handlers;

    /**
     * @param volunteerDialogHandler to developer inside the constructor:
     *                               volunteerHandler must be placed at first place within
     *                               list of handlers: first of all we check
     *                               if the update we have to handle is sent by volunteer.
     */
    public TelegramBotListener(TelegramBot telegramBot,
                               VolunteerDialogHandler volunteerDialogHandler,
                               BasicAdopterHandler basicAdopterHandler,
                               AdopterInputHandler adopterInputHandler,
                               AdopterDialogHandler adopterDialogHandler,
                               ShelterInfoHandler shelterInfoHandler,
                               DefaultHandler defaultHandler
                               ) {
        this.telegramBot = telegramBot;

        handlers = new Handler[]{
                //Important: volunteerHandler MUST BE at first place
                volunteerDialogHandler,
                basicAdopterHandler,
                //then - adopterInputHandler,
                adopterInputHandler,
                //the last one should be shelterInfoHandler
                shelterInfoHandler,
                //then - adopter dialog handler
                adopterDialogHandler,
                //and if an update is still unhandled, then use the last try here in the listener
                defaultHandler
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
                logger.debug("process(updates): processing update: {}", update);
                try {
                    for (Handler handler : handlers) {
                        if (handler.handle(update)) {
                            break;
                        }
                    }
                }
                catch(Exception e){
                    handlers[handlers.length-1].handle(update);
                    logger.error("processMessage: exception was thrown in a handler", e);
                }
            }
        } catch (Exception e) {
            logger.error("process(updates): exception was thrown", e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
