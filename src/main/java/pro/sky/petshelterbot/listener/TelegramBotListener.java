package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.handler.Handler;
import pro.sky.petshelterbot.handler.CatsDevStageHandler;
import pro.sky.petshelterbot.handler.VolunteerHandler;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotListener implements UpdatesListener {

    final private Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);
    final private TelegramBot telegramBot;

    final private Handler[] handlers;

    public TelegramBotListener(TelegramBot telegramBot, VolunteerHandler volunteerHandler, CatsDevStageHandler sergeiDevStageHandler) {
        this.telegramBot = telegramBot;
        handlers = new Handler[]{
                this::handleStart,
                volunteerHandler,
                sergeiDevStageHandler
        };
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {

        logger.info("process(updates)-Method invoked");

        try {
            updates.forEach(update -> {
                logger.info("- Processing update: {}", update);
                if (update.message() != null) {
                    processMessage(update.message());
                } else if (update.callbackQuery() != null) {
                    processCallbackQuery(update.callbackQuery());
                }
            });
        } catch (Exception e) {
            logger.error("process()-Method: Exception` e={} was caught", e, e);
            e.printStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processMessage(Message message) {
        logger.info("processMessage({})-Method", message);

        if (message.text() == null) {
            throw new IllegalArgumentException("Message.text() is null");
        }

        for (Handler handler : handlers) {
            if (handler.handle(message)) {
                return;
            }
        }

        logger.info("- There is no suitable handler for text=\"{}\" received from user={}", message.chat().firstName(), message.text());
    }


    /** @return true if the command is /start **/
    private boolean handleStart(Message message) {
        if (!message.text().equals("/start")) {
            return false;
        }
        Chat chat = message.chat();
        String firstName = chat.firstName();
        logger.info("- Received /start command from user: " + firstName);
        SendMessage welcomeMessage = new SendMessage(chat.id(), "Здравствуйте, " + firstName);
        telegramBot.execute(welcomeMessage);
        return true;
    }

    private void processCallbackQuery(CallbackQuery callbackQuery) {
        logger.info("processCallbackQuery({})-Method", callbackQuery);

        if ("start".equals(callbackQuery.data())) {
            SendMessage welcomeMessage = new SendMessage(callbackQuery.message().chat().id(), "Здравствуйте, " + callbackQuery.from().firstName());
            telegramBot.execute(welcomeMessage);
        }
    }
}
