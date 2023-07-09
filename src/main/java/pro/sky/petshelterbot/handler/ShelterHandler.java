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
        String key=queryData;
        Message message = callbackQuery.message();
        long chatId = message.chat().id();

        Long shelterId = null;
        try {
            shelterId = Long.parseLong(queryData);
            handleShelterCommand(chatId, shelterId);
        } catch (NumberFormatException e) {
            logger.debug("processCallBackQuery-method: callbackQuery.queryData()={} is not a Long", queryData);
        }

        try {
            String[]  dataArray = queryData.split("-");
            shelterId = Long.parseLong(dataArray[0]);
            key = dataArray[1];
            logger.debug("processCallBackQuery-method: callbackQuery.queryData() contains Long shelter_id={} and String key={}",  shelterId, key);
            if (!buttonListMaker(chatId, shelterId, queryData, "Выберите, что вас интересует:")) {
                switch(key) {
                    case "volunteer_call":
                        logger.warn("processCallBackQuery-method: volunteer_call key received. Should be implemented. ");
                        break;
                    default:  {
                        String userMessage = userMessageRepository.findAllByShelterIdAndKey(shelterId, key);
                        telegramBot.execute(new SendMessage(chatId, userMessage));
                    }
                }

            }

        } catch (Exception e) {
            logger.error("processCallBackQuery-method: exception  was thrown. ",  e);
        }


        switch (key) {
            case "schedule_info":
                shelterInfoHandler.shelterWorkTime(chatId, shelterId);
                break;
            case "security_info":
                shelterInfoHandler.sendSecurityInfo(chatId, shelterId);
                break;
            default:
                logger.error("processCallBackQuery-method: handler for queryData ={} is not listed",  queryData);
                break;
        }
    }

    private boolean buttonListMaker(Long chatId, Long shelterId, String chapter, String title) {

        Collection<Button> buttons = buttonsRepository.getButtonsByShelterId(shelterId, chapter);

        if (buttons.size() > 0) {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            buttons.stream()
                    .forEach(button -> {
                        markup.addRow(new InlineKeyboardButton(button.getText()).callbackData(shelterId + "-" + button.getKey()));
                    });

            // Send buttons to user
            telegramBot.execute(new SendMessage(chatId, title)
                    .replyMarkup(markup));
            return true;
        }
        return false;
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