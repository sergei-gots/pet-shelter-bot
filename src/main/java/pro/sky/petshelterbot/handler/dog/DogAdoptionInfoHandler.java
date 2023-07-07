package pro.sky.petshelterbot.handler.dog;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.repository.ButtonsRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles user's pressing a button and sends information about how to adopt a pet
 */
@Component
public class DogAdoptionInfoHandler {
    private final TelegramBot telegramBot;
    private final UserMessageRepository userMessageRepository;
    private final ButtonsRepository buttonsRepository;

    public DogAdoptionInfoHandler(TelegramBot telegramBot, UserMessageRepository userMessageRepository, ButtonsRepository buttonsRepository) {
        this.telegramBot = telegramBot;
        this.userMessageRepository = userMessageRepository;
        this.buttonsRepository = buttonsRepository;
    }

    public void sendAdoptionInfo(Long chatId) {
        // Create buttons

        Collection<Button> buttons = buttonsRepository.getButtonsByShelterId("dog", "adoption_info");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        buttons.stream()
                        .forEach(button -> {
                            markup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getKey()));
                        });

        // Send buttons to user
        telegramBot.execute(new SendMessage(chatId, userMessageRepository.getMessageByKey("choose_info"))
                .replyMarkup(markup));
    }
}
