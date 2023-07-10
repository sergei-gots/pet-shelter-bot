package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.repository.ButtonsRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.Collection;

/**
 * Handles user's pressing a button and sends a suitable menu to the user
 */
@Component
public class ShelterHandler {
    private final Logger logger = LoggerFactory.getLogger(ShelterHandler.class);
    private final TelegramBot telegramBot;
    private final ShelterInfoHandler shelterInfoHandler;
    private final AdoptionInfoHandler catadoptionInfoHandler;
    private final VolunteerChatHandler volunteerChatHandler;

    private final UserMessageRepository userMessageRepository;
    private final ButtonsRepository buttonsRepository;

    public ShelterHandler(TelegramBot telegramBot,
                          ShelterInfoHandler shelterInfoHandler,
                          AdoptionInfoHandler catadoptionInfoHandler,
                          VolunteerChatHandler volunteerChatHandler,
                          UserMessageRepository userMessageRepository, ButtonsRepository buttonsRepository) {
        this.telegramBot = telegramBot;
        this.shelterInfoHandler = shelterInfoHandler;
        this.catadoptionInfoHandler = catadoptionInfoHandler;
        this.volunteerChatHandler = volunteerChatHandler;
        this.userMessageRepository = userMessageRepository;
        this.buttonsRepository = buttonsRepository;
    }

    public void processCallbackQuery(CallbackQuery callbackQuery) {

        logger.debug("processCallBackQuery-method");
        String queryData = callbackQuery.data();
        Message message = callbackQuery.message();
        Long chatId = message.chat().id();

        try {
            String[] queryDataArray = queryData.split("-");
            Long shelterId = Long.parseLong(queryDataArray[0]);
            String key = queryDataArray[1];
            logger.debug("processCallBackQuery-method: callbackQuery.queryData() contains Long shelter_id={} and String key={}", shelterId, key);

            if (makeButtonList(chatId, shelterId, key, "Выберите, что вас интересует:")) {
                return;
            }
            if (volunteerChatHandler.handle(key, chatId, shelterId)) {
                return;
            }
            if (shelterInfoHandler.handle(key, chatId, shelterId)) {
                return;
            }
            sendUserMessage(key, chatId, shelterId);

        } catch (Exception e) {
            logger.error("processCallBackQuery-method: exception  was thrown. ", e);
        }
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

    public void handleShelterCommand(Long chatId, Long shelterId) {
        // Create buttons

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.addRow(new InlineKeyboardButton("Узнать информацию о приюте").callbackData(shelterId + "-shelter_info"));
        markup.addRow(new InlineKeyboardButton("Как взять животное из приюта").callbackData(shelterId + "-adoption_info"));
        markup.addRow(new InlineKeyboardButton("Прислать отчет о питомце").callbackData(shelterId + "-report_info"));
        markup.addRow(new InlineKeyboardButton("Позвать волонтера").callbackData(shelterId + "-volunteer_info"));

        // Send buttons to user
        telegramBot.execute(new SendMessage(chatId, "Выберите действие:")
                .replyMarkup(markup));
    }

}