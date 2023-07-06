package pro.sky.petshelterbot.handler.dog;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

/**
 * Handles user's pressing a button and sends information about the shelter
 */
@Component
public class DogShelterInfoHandler {

    final private TelegramBot telegramBot;

    public DogShelterInfoHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void sendShelterInfo(Long chatId) {
        // Create buttons
        InlineKeyboardButton button1 = new InlineKeyboardButton("Расписание работы и адрес").callbackData("dog_schedule_info");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Контактные данные охраны").callbackData("dog_security_info");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Рекомендации по безопасности").callbackData("dog_safety_info");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Оставить контактные данные").callbackData("dog_contact_info");
        InlineKeyboardButton button5 = new InlineKeyboardButton("Позвать волонтера").callbackData("dog_volunteer_info");

        // Create InlineKeyboardMarkup and give it buttons
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                new InlineKeyboardButton[] {button1},
                new InlineKeyboardButton[] {button2},
                new InlineKeyboardButton[] {button3},
                new InlineKeyboardButton[] {button4},
                new InlineKeyboardButton[] {button5}
        );

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