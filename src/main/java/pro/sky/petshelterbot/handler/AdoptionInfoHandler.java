package pro.sky.petshelterbot.handler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.repository.ButtonsRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.Collection;

/**
 * Handles user's pressing a button and sends information about how to adopt a pet
 */
@Component
public class AdoptionInfoHandler extends AbstractHandler {

    private final ButtonsRepository buttonsRepository;

    public AdoptionInfoHandler(
            TelegramBot telegramBot,
            ShelterRepository shelterRepository,
            UserMessageRepository userMessageRepository, ButtonsRepository buttonsRepository) {
        super(telegramBot, shelterRepository, userMessageRepository);
        this.buttonsRepository = buttonsRepository;
    }

    public void sendAdoptionInfo(Long chatId) {
        // Create buttons
        Collection<Button> buttons = buttonsRepository.findByShelterIdAndChapterOrderById(1L, "adoption_info");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        buttons.stream()
                .forEach(button -> {
                    markup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getKey()));
                });

        // Send buttons to user
        telegramBot.execute(
                new SendMessage(chatId, getUserMessage("choose_info", 1L))
                .replyMarkup(markup));
    }
}