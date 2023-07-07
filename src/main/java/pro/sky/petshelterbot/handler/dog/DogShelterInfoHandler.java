package pro.sky.petshelterbot.handler.dog;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.repository.ButtonsRepository;

import java.util.Collection;

/**
 * Handles user's pressing a button and sends information about the shelter
 */
@Component
public class DogShelterInfoHandler {

    private final TelegramBot telegramBot;
    private final ButtonsRepository buttonsRepository;

    public DogShelterInfoHandler(TelegramBot telegramBot, ButtonsRepository buttonsRepository) {
        this.telegramBot = telegramBot;
        this.buttonsRepository = buttonsRepository;
    }

    public void sendShelterInfo(Long chatId) {
        // Create buttons
        Collection<Button> buttons = buttonsRepository.getButtonsByShelterId("dog", "shelter_info");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        buttons.stream()
                .forEach(button -> {
                    markup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getKey()));
                });

        // Send buttons to user
        telegramBot.execute(new SendMessage(chatId, "Выберите, что вас интересует:")
                .replyMarkup(markup));
    }

    public void sendScheduleInfo(Long chatId) {
        // Send schedule information to user
        telegramBot.execute(new SendMessage(chatId, "Расписание работы и адрес приюта:\n" +
                "Понедельник-пятница: 9:00-18:00\n" +
                "Адрес: ул. Лермонтова, 12"));
    }

    public void sendSecurityInfo(Long chatId) {
        // Send security contact information to user
        telegramBot.execute(new SendMessage(chatId, "Контактные данные охраны приюта:\n" +
                "Телефон: +7 (234) 456-78-91\n" +
                "Email: dog@shelter.com"));
    }

    public void sendSafetyInfo(Long chatId) {
        // Send safety recommendations to user
        telegramBot.execute(new SendMessage(chatId, "Рекомендации по безопасности при общении с животными:\n" +
                "- Не подходите к животному сзади\n" +
                "- Не трогайте животное во время его еды или сна\n" +
                "- Обращайте внимание на жесты и мимику животного"));
    }

    public void sendContactInfo(Long chatId) {
        // Send contact information request to user
        telegramBot.execute(new SendMessage(chatId, "Оставьте свои контактные данные и наш сотрудник свяжется с вами"));
    }
}