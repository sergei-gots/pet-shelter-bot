package pro.sky.petshelterbot.handler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.ButtonRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.Collection;

/**
 * Handles '/start' and sends to the user welcome message and menu
 */
@Component
public class StartHandler extends AbstractHandler {


    public StartHandler(TelegramBot telegramBot,
                        ShelterRepository shelterRepository,
                        UserMessageRepository userMessageRepository,
                        ButtonRepository buttonRepository) {
        super(telegramBot, shelterRepository, userMessageRepository, buttonRepository);
    }

    /** handles command '/start' */
    public boolean handle(Message message) {
        if (!message.text().equals(START)) {
            return false;
        }
        Long chatId = message.chat().id();
        SendMessage welcomeMessage = new SendMessage(chatId, "Здравствуйте, " + message.chat().firstName());
        telegramBot.execute(welcomeMessage);

        Collection<Shelter> shelters = shelterRepository.findAll();


        // Create buttons to choose shelter
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Shelter shelter : shelters) {
            markup.addRow(
                    new InlineKeyboardButton(shelter.getName())
                            .callbackData(shelter.getId() + "-" + START_INFO_MENU)
            );
        }

        sendMenu(chatId, "Выберите приют:", markup);



        return true;
    }

}