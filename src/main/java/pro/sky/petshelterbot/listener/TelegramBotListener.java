package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.handler.Handler;
import pro.sky.petshelterbot.handler.CatsDevStageHandler;
import pro.sky.petshelterbot.handler.ShelterHandler;
import pro.sky.petshelterbot.handler.VolunteerHandler;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotListener implements UpdatesListener {

    final private Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);
    final private TelegramBot telegramBot;

    final private Handler[] handlers;

    private final ShelterHandler shelterHandler;

    public TelegramBotListener(TelegramBot telegramBot,
                               VolunteerHandler volunteerHandler,
                               CatsDevStageHandler catsDevStageHandler,
                               ShelterHandler shelterHandler) {
        this.telegramBot = telegramBot;
        handlers = new Handler[]{
                this::handleStart,
                volunteerHandler,
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

        logger.info("process(updates)-Method invoked");

        try {
            updates.forEach(update -> {
                logger.info("- Processing update: {}", update);
                if (update.message() != null) {
                    processMessage(update.message());
                } else if (update.callbackQuery() != null) {
                    shelterHandler.processCallbackQuery(update.callbackQuery());
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

        logger.info("- There is no suitable handler for text=\"{}\" received from user={}",
                message.chat().firstName(), message.text());
    }


    // @return true if the command is /start /
    private boolean handleStart(Message message) {
        if (!message.text().equals("/start")) {
            return false;
        }
        SendMessage welcomeMessage = new SendMessage(message.chat().id(), "Здравствуйте, " + message.chat().firstName());
        telegramBot.execute(welcomeMessage);

        // Создаем кнопки выбора приюта
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Приют для кошек").callbackData("cat_shelter"),
                new InlineKeyboardButton("Приют для собак").callbackData("dog_shelter"));

        // Отправляем кнопки пользователю
        telegramBot.execute(new SendMessage(message.chat().id(), "Выберите приют:")
                .replyMarkup(markup));

        return true;
    }

}