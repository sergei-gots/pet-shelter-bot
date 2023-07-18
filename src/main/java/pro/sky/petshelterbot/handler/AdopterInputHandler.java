package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.constants.TelegramChatStates;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.repository.*;

/**
 * Operates chat between Adopter and Volunteer on the Adopter's side
 */
@Component
public class AdopterInputHandler extends AbstractHandler {


    public AdopterInputHandler(TelegramBot telegramBot,
                               AdopterRepository adopterRepository,
                               ShelterRepository shelterRepository,
                               UserMessageRepository userMessageRepository,
                               ButtonRepository buttonRepository
    ) {
        super(telegramBot, adopterRepository, shelterRepository, userMessageRepository, buttonRepository);
    }


    @Override
    public boolean handle(Message message) {

        Adopter adopter = getAdopter(message);
        Adopter.ChatState  chatState = adopter.getChatState();

        if(!adopter.isAdopterInputState(chatState)) {
            return false;
        }
        logger.debug("handle(Message): adopter.firstName=\"{}\", .chatState=\"{}\".",
                adopter.getFirstName(), chatState);

        switch (chatState) {
            case ADOPTER_INPUTS_CONTACTS:           processInputContacts(message);
            case ADOPTER_INPUTS_REPORT_DIET:        processInputReportDiet(message);
            case ADOPTER_INPUTS_REPORT_WELL_BEING:  processInputReportWellBeing(message);
            case ADOPTER_INPUTS_REPORT_BEHAVIOUR:   processInputReportBehaviour(message);
            case ADOPTER_INPUTS_REPORT_IMAGE:       processInputReportImage(message);
        }
        return true;
    }

    private void processInputContacts(Message message) {
        logger.debug("processInputContacts(message={})", message);
        Adopter adopter = getAdopter(message);
        adopter.setChatState(ChatState.ADOPTER_INPUTS_CONTACTS);
        sendMessage(adopter.getChatId(), "Пожалуйста, введите номер телефона для связи с вами или нажмите /backtomenu для возврата в меню");
    }

    private void processInputReportDiet(Message message) {
        logger.debug("processInputContacts(message={})", message);
    }

    private void processInputReportWellBeing(Message message) {
        logger.debug("processInputContacts(message={})", message);
    }

    private void processInputReportBehaviour(Message message) {
        logger.debug("processInputContacts(message={})", message);
    }

    private void processInputReportImage(Message message) {
        logger.debug("processInputContacts(message={})", message);
    }

    @Override
    public boolean handle(CallbackQuery callbackQuery) {
        String key = callbackQuery.data();
        Message message = callbackQuery.message();
        Adopter adopter = getAdopter(message);
        Adopter.ChatState chatState = adopter.getChatState();
        if(adopter.isAdopterInputState(chatState)) {
            return false;
        }
        logger.debug("handle(CallbackQuery): adopter.firstName=\"{}\", .chatState=\"{}\".",
                adopter.getFirstName(), chatState);
        switch (key) {
            case ENTER_CONTACTS:
                processEnterContacts(message);
                return true;
            case ENTER_REPORT:
                processEnterReport(message);
                return true;
            default: logger.warn("handle(CallbackQuery): there is no processing provided for key=\"{}\".", key);
            return false;
        }
    }

    private void processEnterContacts(Message message) {
        logger.debug("processEnterContacts(message={})", message);

        Adopter adopter = getAdopter(message);
        adopter.setChatState(Adopter.ChatState.ADOPTER_INPUTS_CONTACTS);
        deletePreviousMenu(adopter);
        adopterRepository.save(adopter);
    }

    private void processEnterReport(Message message) {
        logger.debug("processEnterContacts(message={})", message);


        Adopter adopter = getAdopter(message);
        adopter.setChatState(Adopter.ChatState.ADOPTER_INPUTS_REPORT_DIET);
        deletePreviousMenu(adopter);
        adopterRepository.save(adopter);

    }

}
