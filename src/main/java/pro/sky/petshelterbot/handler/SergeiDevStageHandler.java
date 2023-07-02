package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.listener.TelegramBotListener;
import pro.sky.petshelterbot.service.CatService;

@Component
public class SergeiDevStageHandler implements Handler{

    final private Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);
    final private TelegramBot telegramBot;
    private final CatService catService;

    public SergeiDevStageHandler(TelegramBot telegramBot, CatService catService) {
        this.telegramBot = telegramBot;
        this.catService = catService;
    }

    @Override
    public void handle(Message message) {
        Chat chat = message.chat();
        logger.info("- Received /test-save-cat command from user: " + chat.firstName());
        String catInfo = catService.addTestCatToDb().toString();
        logger.info("- Test-cat={} was added to db", catInfo);
        SendMessage testCatAddedMessage = new SendMessage(chat.id(), "Added " + catInfo);
        telegramBot.execute(testCatAddedMessage);
    }
}
