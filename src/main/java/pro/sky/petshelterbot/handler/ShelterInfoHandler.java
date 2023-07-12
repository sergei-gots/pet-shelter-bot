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

    public ShelterInfoHandler(TelegramBot telegramBot, ButtonsRepository buttonsRepository, UserMessageRepository userMessageRepository, ShelterRepository shelterRepository) {
        super(telegramBot, shelterRepository, userMessageRepository);
        this.buttonsRepository = buttonsRepository;
        this.userMessageRepository = userMessageRepository;
    }

    @Override
    public boolean handle(Message message, String key, Long chatId, Long shelterId) {
        if(Button.OPENING_HOURS_AND_ADDRESS_INFO.equals(key)) {
            sendOpeningHours(chatId, shelterId);
            return true;
        }
        if(Button.SECURITY_INFO.equals(key)) {
                sendSecurityInfo(chatId, shelterId);
                return true;
        }
        return false;
    }

    /** Sends information about shelter's opening hours
     */
    public void sendOpeningHours(Long chatId, Long shelterId) {

        Shelter shelter = getShelter(shelterId);
        telegramBot.execute(new SendMessage(chatId, "Расписание работы и адрес приюта:\n" +
                shelter.getWorkTime() + "\n" +
                "Адрес: " + shelter.getAddress()));
    }

    /** Sends security contact information to user
     */
    public void sendSecurityInfo(Long chatId, Long shelterId) {

        Shelter shelter = getShelter(shelterId);
        telegramBot.execute(new SendMessage(chatId, "Контактные данные охраны приюта:\n" +
                "Телефон: " + shelter.getTel() + "\n" +
                "Email: " + shelter.getEmail()));
    }
}




