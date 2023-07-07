package pro.sky.petshelterbot.handler.dog;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.handler.cat.CatShelterHandler;
import pro.sky.petshelterbot.repository.UserMessageRepository;

/**
 * Handles user's pressing a button and sends a suitable menu to the user
 */
@Component
public class DogShelterHandler {
    final private Logger logger = LoggerFactory.getLogger(CatShelterHandler.class);
    final private TelegramBot telegramBot;
    final private DogShelterInfoHandler dogShelterInfoHandler;
    final private DogAdoptionInfoHandler dogAdoptionInfoHandler;

    private final UserMessageRepository userMessageRepository;

    public DogShelterHandler(TelegramBot telegramBot,
                             DogShelterInfoHandler dogShelterInfoHandler,
                             DogAdoptionInfoHandler dogAdoptionInfoHandler,
                             UserMessageRepository userMessageRepository) {
        this.telegramBot = telegramBot;
        this.dogShelterInfoHandler = dogShelterInfoHandler;
        this.dogAdoptionInfoHandler = dogAdoptionInfoHandler;
        this.userMessageRepository = userMessageRepository;
    }

    public void processCallbackQuery(CallbackQuery callbackQuery) {
        logger.info("processCallbackQuery({})-Method", callbackQuery);

        String data = callbackQuery.data();
        Message message = callbackQuery.message();
        long chatId = message.chat().id();

        switch (data) {
            case "dog_shelter_info":
                dogShelterInfoHandler.sendShelterInfo(chatId);
                break;
            case "dog_shelter":
                handleShelterCommand(chatId);
                break;
            case "dog_schedule_info":
                dogShelterInfoHandler.sendScheduleInfo(chatId);
                break;
            case "dog_security_info":
                dogShelterInfoHandler.sendSecurityInfo(chatId);
                break;
            case "dog_safety_info":
                dogShelterInfoHandler.sendSafetyInfo(chatId);
                break;
            case "dog_contact_info":
                dogShelterInfoHandler.sendContactInfo(chatId);
                break;
            case "dog_adoption_info":
                dogAdoptionInfoHandler.sendAdoptionInfo(chatId);
                break;
            default:
                // Handle unknown data
                break;
        }

        String userMessage = userMessageRepository.getMessageByKey(data);
        if (userMessage != null) {
            telegramBot.execute(new SendMessage(chatId, userMessage));
        }

    }

    public void handleShelterCommand(Long chatId) {
        // Create buttons
        InlineKeyboardButton button1 = new InlineKeyboardButton("Узнать информацию о приюте").callbackData("dog_shelter_info");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Как взять животное из приюта").callbackData("dog_adoption_info");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Прислать отчет о питомце").callbackData("dog_report_info");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Позвать волонтера").callbackData("dog_volunteer_info");


        // Create InlineKeyboardMarkup and give it buttons
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                new InlineKeyboardButton[] {button1},
                new InlineKeyboardButton[] {button2},
                new InlineKeyboardButton[] {button3},
                new InlineKeyboardButton[] {button4}
        );

        // Send buttons to user
        telegramBot.execute(new SendMessage(chatId, "Выберите действие:")
                .replyMarkup(markup));
    }


}
