package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.*;
import pro.sky.petshelterbot.repository.*;


@Component
public class BasicAdopterHandler extends AbstractHandler {


    public BasicAdopterHandler(TelegramBot telegramBot,
                                  AdopterRepository adopterRepository,
                                  VolunteerRepository volunteerRepository,
                                  ShelterRepository shelterRepository,
                                  UserMessageRepository userMessageRepository,
                                  ButtonRepository buttonsRepository,
                                  DialogRepository dialogRepository) {
        super(telegramBot, adopterRepository, volunteerRepository,
                shelterRepository, userMessageRepository,buttonsRepository, dialogRepository);
    }

    @Override
    public boolean handleCallbackQuery(Message message, String key) {
        logger.debug("handle(): chatId={}, key={}", message.chat().id(), key);

        Adopter adopter = getAdopter(message);
        ChatState chatState = adopter.getChatState();
        logger.debug("handle(): adopter.chatState={}", chatState);

        if(chatState == ChatState.ADOPTER_CHOICES_SHELTER) {
            processShelterChoice(adopter, key);
            return true;
        }

        if (adopter.getShelter() == null) {
            greetUser(adopter);
            sendMessage(adopter.getChatId(), "По каким-то причинам мы не можем вспомнить, " +
                    "с каким из приютов вы взаимодействовали в последний раз.");
            reselectShelter(adopter);
            return true;
        }

        switch (key) {
            case START:
                greetUser(adopter);
                processResumeChat(adopter);
                return true;
            case RESET:
                greetUser(adopter);
            case RESET_SHELTER:
            case RESET_SHELTER_RU:
                reselectShelter(adopter);
                return true;
            case SHELTER_INFO_MENU:
                showShelterInfoMenu(adopter);
                return true;
            case MENU:
            case MENU_RU:
                showCurrentMenu(adopter);
                return true;
            case ADOPTION_INFO_MENU:
                showAdoptionInfoMenu(adopter);
                return true;
            case ABOUT_SHELTER_INFO:
                sendUserMessage(adopter, key);
                return true;
            case SHELTER_CHOICE:
                processShelterChoice(adopter, key);
                return true;
        }
        return false;
    }

}
