package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.repository.ButtonsRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.Collection;

/**
 * Handles user's pressing a button and sends a suitable menu to the user
 */
@Component
public class ShelterHandler extends AbstractHandler {

    private final ShelterInfoHandler shelterInfoHandler;
    private final VolunteerHandler volunteerHandler;
    private final VolunteerChatHandler volunteerChatHandler;

    private final UserMessageRepository userMessageRepository;
    private final ButtonsRepository buttonsRepository;

    public ShelterHandler(TelegramBot telegramBot,
                          ShelterInfoHandler shelterInfoHandler,
                          VolunteerHandler volunteerHandler,
                          VolunteerChatHandler volunteerChatHandler,
                          UserMessageRepository userMessageRepository, ButtonsRepository buttonsRepository) {
        super(telegramBot);
        this.shelterInfoHandler = shelterInfoHandler;
        this.volunteerHandler = volunteerHandler;
        this.volunteerChatHandler = volunteerChatHandler;
        this.userMessageRepository = userMessageRepository;
        this.buttonsRepository = buttonsRepository;
    }

    @Override
    public boolean handle(CallbackQuery callbackQuery) {

        if (callbackQuery.data() == null) {
            logger.debug("handle(CallbackQuery=null)");
            return false;
        }

        if(volunteerHandler.handle(callbackQuery)) {
            return true;
        }

        logger.debug("handle(CallbackQuery)-method");
        String queryData = callbackQuery.data();
        Message message = callbackQuery.message();
        Long chatId = message.chat().id();

        try {
            String[] queryDataArray = queryData.split("-");
            Long shelterId = Long.parseLong(queryDataArray[0]);
            String key = queryDataArray[1];
            logger.debug("handle(CallbackQuery)-method: callbackQuery.queryData() contains Long shelter_id={} and String key={}", shelterId, key);

            if (makeButtonList(chatId, shelterId, key, "Выберите, что вас интересует:")) {
                return true;
            }
            if (volunteerChatHandler.handle(message, key, chatId, shelterId)) {
                return true;
            }
            if (shelterInfoHandler.handle(message, key, chatId, shelterId)) {
                return true;
            }
            sendUserMessage(key, chatId, shelterId);

        } catch (Exception e) {
            logger.error("handle(CallBackQuery)-method: exception  was thrown. ", e);
        }
        return false;
    }

    private void sendUserMessage(String key, long chatId, Long shelterId) {
        String userMessage = userMessageRepository.findByShelterIdAndKey(shelterId, key);
        if (userMessage == null) {
            userMessage = "Раздел не создан. Разработчики скоро сформируют этот раздел";
            logger.error("processCallBackQuery-method: user_message with shelter_id={}, key={} is not listed.",
                    shelterId, key);
        }
        telegramBot.execute(new SendMessage(chatId, userMessage));
    }

    private boolean makeButtonList(Long chatId, Long shelterId, String chapter, String title) {

        Collection<Button> buttons = buttonsRepository.findByShelterIdAndChapterOrderById(shelterId, chapter);
        if (buttons.size() == 0) {
            buttons = buttonsRepository.findByChapterOrderById(chapter);
        }
        if (buttons.size() == 0) {
            return false;
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Button button : buttons) {
            markup.addRow(new InlineKeyboardButton(button.getText()).callbackData(shelterId + "-" + button.getKey()));
        }

        telegramBot.execute(new SendMessage(chatId, title)
                .replyMarkup(markup));
        return true;

    }

}