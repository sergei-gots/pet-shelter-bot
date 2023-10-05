package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.*;


/**
 * Handles user's pressing a button and sends a suitable menu to the user
 */
@Component
public class ShelterInfoHandler extends AbstractHandler {

    public ShelterInfoHandler(TelegramBot telegramBot,

                              AdopterRepository adopterRepository,
                              VolunteerRepository volunteerRepository,
                              ShelterRepository shelterRepository,
                              UserMessageRepository userMessageRepository,
                              ButtonRepository buttonsRepository,
                              DialogRepository dialogRepository

                              ) {
        super(telegramBot, adopterRepository, volunteerRepository,
                shelterRepository, userMessageRepository, buttonsRepository,
                dialogRepository);

    }

    @Override
    public boolean handleCallbackQuery(Message message, String key) {
        logger.debug("handle(): chatId={}, key={}", message.chat().id(), key);

        Adopter adopter = getAdopter(message);

        if (adopter.getChatState() == ChatState.ADOPTER_CHOICES_SHELTER) {
            processShelterChoice(adopter, key);
            return true;
        }

        switch (key) {
            case OPENING_HOURS_AND_ADDRESS_INFO:
                sendOpeningHours(adopter);
                return true;
            case SECURITY_INFO:
                sendSecurityInfo(adopter);
                return true;
            }
        return sendUserMessage(adopter, key);
    }





    /** Sends information about shelter's opening hours
     */
    public void sendOpeningHours(Adopter adopter) {
        logger.trace("sendOpeningHours");
        Shelter shelter = adopter.getShelter();
        sendMessage(adopter.getChatId(), "<u>Расписание работы и адрес приюта</u>:\n" +
                shelter.getWorkTime() + "\n" +
                "Адрес: " + shelter.getAddress());
    }

    /** Sends security contact information to user
     */
    public void sendSecurityInfo(Adopter adopter) {
        logger.trace("sendSecurityInfo");
        Shelter shelter = adopter.getShelter();
        sendMessage(adopter.getChatId(), "<u>Контактные данные охраны приюта</u>:\n" +
                "Телефон: " + shelter.getTel() + "\n" +
                "Email: " + shelter.getEmail());
    }


}