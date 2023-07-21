package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.UserMessage;
import pro.sky.petshelterbot.handler.*;
import pro.sky.petshelterbot.handler.ShelterInfoHandler;
import pro.sky.petshelterbot.repository.UserMessageRepository;


import javax.annotation.PostConstruct;
import java.util.List;

import static pro.sky.petshelterbot.constants.ChapterNames.MessageKey.NOT_IMPLEMENTED_YET;

@Service
public class TelegramBotListener
        implements UpdatesListener, Handler {

    final private Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);
    final private TelegramBot telegramBot;
    final private UserMessageRepository userMessageRepository;
    final private Handler[] handlers;

    /**
     * @param volunteerDialogHandler to developer inside the constructor:
     *                               volunteerHandler must be placed at first place within
     *                               list of handlers: first of all we check
     *                               if the update we have to handle is sent by volunteer.
     */
    public TelegramBotListener(TelegramBot telegramBot,
                               UserMessageRepository userMessageRepository, VolunteerDialogHandler volunteerDialogHandler,
                               AdopterInputHandler adopterInputHandler,
                               AdopterDialogHandler adopterDialogHandler,
                               ShelterInfoHandler shelterInfoHandler
                               ) {
        this.telegramBot = telegramBot;
        this.userMessageRepository = userMessageRepository;

        handlers = new Handler[]{
                //Important: volunteerHandler MUST BE at first place
                volunteerDialogHandler,
                //then - adopterInputHandler,
                adopterInputHandler,
                //the last one should be shelterInfoHandler
                shelterInfoHandler,
                //then - adopter dialog handler
                adopterDialogHandler,
                //and if an update is still unhandled, then use the last try here in the listener
                this
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
                    saySorry(update);
                    logger.error("processMessage: exception was thrown in a handler", e);

                }
            }
        } catch (Exception e) {
            logger.error("process(updates): exception was caught", e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * @return false if and only if the message is null
     * otherwise it will be assumed that the message is handled
     * and further handling is not necessary
     * @throws IllegalArgumentException if message.text() is null
     */
    @Override
    public boolean handle(Update update) {
        Message message = update.message();
        saySorry(update);
        logger.trace("processMessage: there is no suitable handler for text=\"{}\" received from user={} with chat_id={}",
                message.text(), message.chat().firstName(), message.chat().id());
        return true;
    }

    private void saySorry(Update update) {
        Message message = update.message();
        UserMessage userMessage = userMessageRepository.findFirstByKeyAndShelterIsNull(NOT_IMPLEMENTED_YET.name())
                .orElseThrow(()->new IllegalStateException("No user message for key=" + NOT_IMPLEMENTED_YET));
        telegramBot.execute(new SendMessage(message.chat().id(), userMessage.getMessage() + "\n /menu"));
    }

}
