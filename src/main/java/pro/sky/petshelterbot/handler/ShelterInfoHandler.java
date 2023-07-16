package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.AdopterRepository;
import pro.sky.petshelterbot.repository.ButtonRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

/**
 * Handles user's pressing a button and sends information about the shelter
 */
@Component
public class ShelterInfoHandler extends AbstractHandler {


    public ShelterInfoHandler(TelegramBot telegramBot, ButtonRepository buttonsRepository,
                              AdopterRepository adopterRepository,
                              UserMessageRepository userMessageRepository, ShelterRepository shelterRepository) {
        super(telegramBot, adopterRepository, shelterRepository, userMessageRepository, buttonsRepository);
    }

    @Override
    public boolean handle(Message message, String key, Long chatId, Long shelterId) {
        if(OPENING_HOURS_AND_ADDRESS_INFO.equals(key)) {
            sendOpeningHours(chatId, shelterId);
            return true;
        }
        if(SECURITY_INFO.equals(key)) {
                sendSecurityInfo(chatId, shelterId);
                return true;
        }
        if(ADOPTION_INFO_MENU.equals(key)) {
            makeButtonList(getAdopter(message), shelterId, key);
            return true;
        }
        return false;
    }

    /** Sends information about shelter's opening hours
     */
    public void sendOpeningHours(Long chatId, Long shelterId) {

        Shelter shelter = getShelter(shelterId);
        sendMessage(chatId, "<u>Расписание работы и адрес приюта</u>:\n" +
                shelter.getWorkTime() + "\n" +
                "Адрес: " + shelter.getAddress());
    }

    /** Sends security contact information to user
     */
    public void sendSecurityInfo(Long chatId, Long shelterId) {

        Shelter shelter = getShelter(shelterId);
        sendMessage(chatId, "<u>Контактные данные охраны приюта</u>:\n" +
                "Телефон: " + shelter.getTel() + "\n" +
                "Email: " + shelter.getEmail());
    }
}




