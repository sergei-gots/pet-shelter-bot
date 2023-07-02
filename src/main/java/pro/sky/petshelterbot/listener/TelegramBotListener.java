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
import pro.sky.petshelterbot.handler.SergeiDevStageHandler;
import pro.sky.petshelterbot.handler.VolunteerHandler;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotListener implements UpdatesListener {

    final private Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);
    final private TelegramBot telegramBot;
    final private VolunteerHandler volunteerHandler;
    final private SergeiDevStageHandler sergeiDevStageHandler;

    final private Handler[] handlers;

    public TelegramBotListener(TelegramBot telegramBot, VolunteerHandler volunteerHandler, SergeiDevStageHandler sergeiDevStageHandler) {
        this.telegramBot = telegramBot;
        this.volunteerHandler = volunteerHandler;
        this.sergeiDevStageHandler = sergeiDevStageHandler;
        handlers = new Handler[] {
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
        } catch(Exception e) {
            logger.error("process()-Method: Exception` e={} was caught", e, e);
            e.printStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processMessage(Message message) {
        logger.info("processMessage({})-Method", message);

        String text = message.text();
        if(text == null) {
            return;
        }
        Chat chat = message.chat();
        String firstName = chat.firstName();

        if ("/start".equals(text)) {
            handleStart(chat, firstName);
            return;
        }

        for (Handler handler: handlers) {
            if(handler.handle(message)) {
                return;
            }
        }

        logger.info("- There is no suitable handler for text=\"{}\" received from user={}",
                firstName, text);
    }

    private void handleStart(Chat chat, String userFirstName) {
        logger.info("- Received /start command from user: " + userFirstName);
        SendMessage welcomeMessage = new SendMessage(
                chat.id(), "Здравствуйте, " + userFirstName);
        telegramBot.execute(welcomeMessage);
    }

    private void processCallbackQuery(CallbackQuery callbackQuery) {
        logger.info("processCallbackQuery({})-Method", callbackQuery);

        if ("start".equals(callbackQuery.data())) {
            SendMessage welcomeMessage = new SendMessage(
                    callbackQuery.message().chat().id(),
                    "Здравствуйте, " + callbackQuery.from().firstName());
            telegramBot.execute(welcomeMessage);
        }
    }
}
