package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.*;

import java.util.List;

import static pro.sky.petshelterbot.constants.PetShelterBotConstants.MessageKey.VOLUNTEER_IS_NOTIFIED;

/**
 * Operates chat between Adopter and Volunteer on the Adopter's side
 */
@Component
public class AdopterDialogHandler extends AbstractDialogHandler {


    public AdopterDialogHandler(TelegramBot telegramBot,
                                AdopterRepository adopterRepository,
                                ShelterRepository shelterRepository,
                                UserMessageRepository userMessageRepository,
                                ButtonRepository buttonRepository,
                                DialogRepository dialogRepository,
                                VolunteerRepository volunteerRepository
    ) {
        super(telegramBot, adopterRepository, volunteerRepository, shelterRepository, userMessageRepository, buttonRepository, dialogRepository);
    }


    public boolean handleDefault(Message message) {

        Long chatId = message.chat().id();
        Adopter adopter = getAdopter(message);
        Dialog dialog = getDialogIfRequested(adopter);
        if (dialog == null) {
            return false;
        }

        Volunteer volunteer = dialog.getVolunteer();
        if (volunteer == null) {
            sendMessage(chatId, "Подождите, волонтёр скоро свяжется с вами. Приношу извинения за ваше ожидание! " +
                    " Вы также можете снять заявку на диалог с волонтёром комадой " +
                    CANCEL_VOLUNTEER_CALL +  ". Спасибо)");
            return true;
        }

        forwardDialogMessage(adopter, volunteer, message.text());
        return true;
    }

    @Override
    public boolean handleMessage(Message message) {

        Adopter adopter = getAdopter(message);

        if(adopter.getChatState() != ChatState.ADOPTER_IN_DIALOG) {
            return false;
        }

        Dialog dialog = getDialogIfRequested(adopter);

        if (dialog == null) {
            logger.warn("handle(message)- dialog for adopter.Id={}, chatState={} is NULL",
                    adopter.getChatId(), adopter.getChatState());
            sendMessage(adopter.getChatId(), "Диалог с волонтёром закончился внештатно. Но всё в порядке. ");
            adopter.setChatState(ChatState.ADOPTER_IN_SHELTER_INFO_MENU);
            sendMenu(adopter, CONTINUE);

            return true;
        }

        Volunteer volunteer = dialog.getVolunteer();
        logger.debug("handle(message) forward message from adopter={} to volunteer={}",
                adopter.getFirstName(), volunteer.getFirstName());
        forwardDialogMessage(adopter, volunteer, message.text());
        return true;
    }

    @Override
    public boolean handleCallbackQuery(Message message, String key) {

            Adopter adopter = getAdopter(message);
            if (isShelterToBeAssigned(adopter, key)) {
                return true;
            }

            if (handleVolunteerCall(message, key)) {
                return true;
            }
            if (handleCancelVolunteerCall(adopter, key)) {
                return true;
            }
            return handleDefault(message);
    }

    public boolean handleVolunteerCall(Message message, String key) {

        if(!key.startsWith(CALL_VOLUNTEER)) {
            return false;
        }

        logger.debug("handleVolunteerCall(message={})", message);
        Adopter adopter = getAdopter(message);
        long chatId = adopter.getChatId();
        if (getDialogIfRequested(adopter) != null) {
            throw new IllegalStateException("Dialog for chatId=" + chatId + " is already requested");
        }
        createDialogRequest(adopter);
        return true;
    }

    private void createDialogRequest(Adopter adopter) {

        logger.trace("createDialogRequest(adopter={})", adopter);

        //Create Dialog Entry
        Dialog dialog = new Dialog(adopter);
        dialogRepository.save(dialog);

        showCurrentMenu(adopter);

        //Get List of Available Volunteers
        List<Volunteer> availableVolunteers =
                volunteerRepository.findByShelterAndAvailableIsTrue(adopter.getShelter());

        if (availableVolunteers.size() == 0) {
            //If there isn't any available Volunteer out
            sendMessage(adopter.getChatId(), "В настоящий момент все волонтёры заняты. " +
                     "Как только один из волонтёров освободится, он свяжется с вами. " +
                    getUserMessage(MessageKey.WAIT_FOR_VOLUNTEER_BOT_RESTRICTIONS)
            );
            return;
        }

        //Else notify all the available volunteers about new dialog request
        for (Volunteer volunteer : availableVolunteers) {
            sendMessage(volunteer.getChatId(),
                    volunteer.getFirstName() + "! C вами хотел бы связаться " + adopter.getFirstName());
            sendMenu(volunteer, JOIN_DIALOG);
        }
        sendUserMessage (adopter, VOLUNTEER_IS_NOTIFIED);
    }
}
