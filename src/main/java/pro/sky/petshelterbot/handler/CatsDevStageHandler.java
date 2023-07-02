package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.service.CatService;

/**
 * Development stage class for debugging/testing
 * functionality being implementing by Sergei
 */
@Component
public class CatsDevStageHandler extends AbstractHandler{
    private final CatService catService;

    public CatsDevStageHandler(TelegramBot telegramBot, CatService catService) {
        super(telegramBot);
        this.catService = catService;
    }

    @Override
    public boolean handle(Message message) {

        String text = message.text();
        if(!text.startsWith("/sergei-test-")) {
            return false;
        }

        Chat chat = message.chat();
        logger.info("- Received {} command from user {}", text, chat.firstName());

        if(text.equals("/sergei-test-save-cat")) {
            String catInfo = catService.addTestCatToDb().toString();
            logger.info("- Test-cat={} was added to db", catInfo);
            SendMessage testCatAddedMessage = new SendMessage(chat.id(), "Added " + catInfo);
            telegramBot.execute(testCatAddedMessage);
        }

        return true;
    }
}
