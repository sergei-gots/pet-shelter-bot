package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.repository.*;

/**
 * Operates chat between Adopter and Volunteer on the Adopter's side
 */
@Component
public class AdopterInputHandler extends AbstractHandler {


    public AdopterInputHandler(TelegramBot telegramBot,
                               AdopterRepository adopterRepository,
                               VolunteerRepository volunteerRepository,
                               ShelterRepository shelterRepository,
                               UserMessageRepository userMessageRepository,
                               ButtonRepository buttonRepository,
                               DialogRepository dialogRepository
    ) {
        super(telegramBot, adopterRepository, volunteerRepository, shelterRepository, userMessageRepository, buttonRepository, dialogRepository);
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

    @Override
    public boolean handle(Message message) {

        if(super.handle(message)) {
            return true;
        }

        Adopter adopter = getAdopter(message);
        Adopter.ChatState  chatState = adopter.getChatState();

        logger.debug("handle(Message): adopter.firstName=\"{}\", .chatState=\"{}\".",
                adopter.getFirstName(), chatState);

        switch (chatState) {
            case ADOPTER_INPUTS_PHONE_NUMBER:       processInputPhoneNumber(message);
            case ADOPTER_INPUTS_REPORT_DIET:        processInputReportDiet(message);
            case ADOPTER_INPUTS_REPORT_WELL_BEING:  processInputReportWellBeing(message);
            case ADOPTER_INPUTS_REPORT_BEHAVIOUR:   processInputReportBehaviour(message);
            case ADOPTER_INPUTS_REPORT_IMAGE:       processInputReportImage(message);
        }
        return true;
    }

    private void processEnterContacts(Message message) {
        logger.debug("processEnterContacts(message={})", message);

        Adopter adopter = getAdopter(message);
        adopter.setChatState(ChatState.ADOPTER_INPUTS_PHONE_NUMBER);
        deletePreviousMenu(adopter);
        adopterRepository.save(adopter);

        sendMessage(adopter.getChatId(), "Пожалуйста, введите номер телефона для связи с вами (/menu для отмены)." +
                " Формат номера: 7-7XX-XXX-XX-XX:");

    }

    private void processInputPhoneNumber(Message message) {
        logger.debug("processInputPhoneNumber(message.text={})", message.text());
        String phoneNumber= message.text().replaceAll("\\D+","");

        int length = phoneNumber.length();
        logger.debug("processInputPhoneNumber(): phone_number={}, length={}", phoneNumber, length);
        //Todo - store these constants in the db
        if(!(length == 11 && phoneNumber.startsWith("77"))) {
            sendMessage(message.chat().id(),
                    "Мы не смогли распознать телефонный номер. Пожалуйста, введите номер " +
                            "в формате 7-7XX-XXX-XX-XX:");
        }
        else  {
            Adopter adopter = getAdopter(message);
            adopter.setPhoneNumber(phoneNumber);
            adopter.setChatState(ChatState.ADOPTER_IN_ADOPTION_INFO_MENU);
            adopterRepository.save(adopter);

            sendMessage(adopter.getChatId(),
                    "Ваш телефонный номер: "
                            + phoneNumber.replaceFirst(
                                    "(\\d{1})(\\d{3})(\\d{3})(\\d{2})(\\d+)", "+$1-$2-$3-$4-$5")
                            + ". Спасибо:) \nДля продолжения нажмите " + MENU);

        }
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



    private void processEnterReport(Message message) {
        logger.debug("processEnterContacts(message={})", message);


        Adopter adopter = getAdopter(message);
        adopter.setChatState(Adopter.ChatState.ADOPTER_INPUTS_REPORT_DIET);
        deletePreviousMenu(adopter);
        adopterRepository.save(adopter);

    }

}
