package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.ButtonsRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.Collection;

/**
 * Handles user's pressing a button and sends information about the shelter
 */
@Component
public class ShelterInfoHandler extends AbstractHandler {

    private final ButtonsRepository buttonsRepository;
    private final UserMessageRepository userMessageRepository;
    private final ShelterRepository shelterRepository;

    public ShelterInfoHandler(TelegramBot telegramBot, ButtonsRepository buttonsRepository, UserMessageRepository userMessageRepository, ShelterRepository shelterRepository) {
        super(telegramBot);
        this.buttonsRepository = buttonsRepository;
        this.userMessageRepository = userMessageRepository;
        this.shelterRepository = shelterRepository;
    }

    @Override
    public boolean handle(Message message, String key, Long chatId, Long shelterId) {
        if("schedule_info".equals(key)) {
            shelterWorkTime(chatId, shelterId);
            return true;
        }
        if("security_info".equals(key)) {
                shelterWorkTime(chatId, shelterId);
                return true;
        }

        return false;
    }

    public void sendShelterInfo(Long chatId, Long shelterId) {
        // Create buttons

        Collection<Button> buttons = buttonsRepository.findByShelterIdAndChapterOrderById(shelterId, "shelter_info");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        buttons.stream()
                .forEach(button -> {
                    markup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getKey()));
                });

        // Send buttons to user
        telegramBot.execute(new SendMessage(chatId, "Выберите, что вас интересует:")
                .replyMarkup(markup));
    }

    public void shelterWorkTime(Long chatId, Long shelterId) {
        // Send schedule information to user
        Shelter shelter = shelterRepository.findById(shelterId).get();
        telegramBot.execute(new SendMessage(chatId, "Расписание работы и адрес приюта:\n" +
                shelter.getWorkTime() + "\n" +
                "Адрес: " + shelter.getAddress()));
    }

    public void sendSecurityInfo(Long chatId, Long shelterId) {
        // Send security contact information to user
        Shelter shelter = shelterRepository.findById(shelterId).get();
        telegramBot.execute(new SendMessage(chatId, "Контактные данные охраны приюта:\n" +
                "Телефон: " + shelter.getTel() + "\n" +
                "Email: " + shelter.getEmail()));
    }

    public void sendContactInfo(Long chatId) {
        // Send contact information request to user
        telegramBot.execute(new SendMessage(chatId, "Оставьте свои контактные данные и наш сотрудник свяжется с вами"));
    }
}




