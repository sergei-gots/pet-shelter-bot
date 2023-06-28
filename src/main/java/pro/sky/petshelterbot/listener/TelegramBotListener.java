package pro.sky.petshelterbot.listener;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null) {
                processMessage(update.message());
            } else if (update.callbackQuery() != null) {
                processCallbackQuery(update.callbackQuery());
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processMessage(Message message) {
        if (message.text() != null && message.text().equals("/start")) {
            String firstName = message.chat().firstName();
            SendMessage welcomeMessage = new SendMessage(message.chat().id(), "Здравствуйте, " + firstName);
            telegramBot.execute(welcomeMessage);
        }
    }

    private void processCallbackQuery(CallbackQuery callbackQuery) {
        if (callbackQuery.data().equals("start")) {
            String firstName = callbackQuery.from().firstName();
            SendMessage welcomeMessage = new SendMessage(callbackQuery.message().chat().id(), "Здравствуйте, " + firstName);
            telegramBot.execute(welcomeMessage);
        }
    }
}
