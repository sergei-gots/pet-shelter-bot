package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.UserMessage;
import pro.sky.petshelterbot.repository.*;

import static pro.sky.petshelterbot.constants.PetShelterBotConstants.MessageKey.NOT_IMPLEMENTED_YET;

@Component
public class DefaultHandler extends AbstractHandler {
    public DefaultHandler(TelegramBot telegramBot,
                               AdopterRepository adopterRepository,
                               VolunteerRepository volunteerRepository,
                               ShelterRepository shelterRepository,
                               UserMessageRepository userMessageRepository,
                               ButtonRepository buttonsRepository,
                               DialogRepository dialogRepository) {
        super(telegramBot, adopterRepository, volunteerRepository,
                shelterRepository, userMessageRepository,buttonsRepository, dialogRepository);
    }
    /**
     * @return false if and only if the message is null
     * otherwise it will be assumed that the message is handled
     * and further handling is not necessary
     * @throws IllegalArgumentException if message.text() is null
     */
    @Override
    public boolean handle(Update update) {
        Message message = update.message();
        saySorryToUser(update);
        logger.trace("processMessage: there is no suitable handler for text=\"{}\" received from user={} with chat_id={}",
                message.text(), message.chat().firstName(), message.chat().id());
        return true;
    }

    private void saySorryToUser(Update update) {
        Message message = update.message();
        UserMessage userMessage = userMessageRepository.findFirstByKeyAndShelterIsNull(NOT_IMPLEMENTED_YET.name())
                .orElseThrow(()->new IllegalStateException("No user message for key=" + NOT_IMPLEMENTED_YET));
        telegramBot.execute(new SendMessage(message.chat().id(), userMessage.getMessage() + "\n /menu"));
    }

}
