package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.*;
import pro.sky.petshelterbot.repository.*;

import java.util.Collection;

/**
 * Handles commands receiving from user supposed
 * to be a volunteer.
 * Also operates chat between Adopter and Volunteer on the Volunteer's side
 **/

@Component
public abstract class AbstractDialogHandler extends AbstractHandler  {
    final protected VolunteerRepository volunteerRepository;
    final protected DialogRepository dialogRepository;

    public AbstractDialogHandler(TelegramBot telegramBot,
                                 AdopterRepository adopterRepository,
                                 ShelterRepository shelterRepository,
                                 UserMessageRepository userMessageRepository,
                                 ButtonRepository buttonRepository,
                                 VolunteerRepository volunteerRepository, DialogRepository dialogRepository) {
        super(telegramBot, adopterRepository, shelterRepository, userMessageRepository, buttonRepository);
        this.volunteerRepository = volunteerRepository;
        this.dialogRepository = dialogRepository;
    }

    protected Dialog nextDialogInWaiting(Shelter shelter) {
        Collection<Dialog> dialogsInWaiting
                = dialogRepository.findWaitingDialogsByVolunteerShelterOrderByIdAsc(shelter);
        if (dialogsInWaiting.isEmpty()) {
            return null;
        } else {
            return dialogsInWaiting.iterator().next();
        }
    }

    protected void sendDialogMessageToAdopter(Dialog dialog, String text) {
        logger.trace("sendDialogMessageToAdopter()-method.  adopter.getFirstName()=\"{}\"", dialog.getAdopter().getFirstName());
        sendMessage(dialog.getAdopter().getChatId(),
                dialog.getVolunteer().getFirstName() + "> " + text);
    }

    protected void sendDialogMessageToVolunteer(Dialog dialog, String text) {
        logger.trace("sendDialogMessageToVolunteer()-method.  volutnteer.getFirstName()=\"{}\"", dialog.getVolunteer().getFirstName());
        sendMessage(dialog.getVolunteer().getChatId(),
                dialog.getAdopter().getFirstName() + "> " + text);
    }

    public void sendPersonalizedMessage(AbstractPerson person, String text) {
        sendMessage(person.getChatId(), person.getFirstName() + ", " + text);
    }

    @Override
    public boolean handle(Message message, String key) {
        switch(key) {
            case CANCEL_VOLUNTEER_CALL:
            case CLOSE_DIALOG:
            case CLOSE_DIALOG_RU:
                handleCancelVolunteerCall(getAdopter(message));
                return true;
            default: return false;
        }
    }


    public void handleCancelVolunteerCall(Adopter adopter) {
        long chatId = adopter.getChatId();
        logger.debug("handleCancelVolunteerCall(adopter.chat_id={})", chatId);
        Dialog dialog = getDialogIfRequested(chatId);
        if (dialog == null) {
            showShelterInfoMenu(adopter);
            logger.debug("Dialog for chatId=" + chatId + " is not listed in db. It could be ok.");
            return;
        }
        Volunteer volunteer = dialog.getVolunteer();
        dialogRepository.delete(dialog);
        showShelterInfoMenu(adopter);
        if(volunteer != null) {
            deletePreviousMenu(volunteer);
            showShelterInfoMenu(adopter);
            sendDialogMessageToAdopter(dialog, "Всего вам наилучшего:) Если у вас возникнут вопросы, обращайтесь ещё!");
            sendMessage(volunteer.getChatId(), "Диалог c "
                    + adopter.getFirstName() + " завершён. Спасибо:)");
            volunteer.setAvailable(true);
            volunteerRepository.save(volunteer);
        } else {
            sendMessage(chatId, "Заявка на диалог с волонтёром снята");
        }
    }
    public void sendHandshakes(Dialog dialog){
        Volunteer volunteer = dialog.getVolunteer();
        Adopter adopter = dialog.getAdopter();

        long volunteerChatId = volunteer.getChatId();
        long adopterChatId = adopter.getChatId();

        sendMessage(volunteerChatId,
                volunteer.getFirstName() + "! C вами хотел бы связаться " + adopter.getFirstName() +
                        ". Нажмите кнопку 'Присоединиться к чату' для начала общения.");
        sendMenu(volunteer, JOIN_DIALOG);

        deletePreviousMenu(adopter);

        sendMenu(adopter, CLOSE_DIALOG);
        sendMessage(adopterChatId, "Волонтёру отослано уведомление. Волонтёр свяжется с вами " +
                "насколько это возможно скоро. Основное меню на время диалога будет скрыто ", new ReplyKeyboardRemove());
    }


    protected void sendMenu(Volunteer volunteer, String chapter) {
        sendMenuAbstractPerson(volunteer, chapter);
        volunteerRepository.save(volunteer);
    }

    protected void sendMenu(Volunteer volunteer, String header, InlineKeyboardMarkup markup) {
        sendMenuAbstractPerson(volunteer, header, markup);
        volunteerRepository.save(volunteer);
    }
    protected void deletePreviousMenu(Volunteer volunteer) {
        deletePreviousMenuAbstractPerson(volunteer);
        volunteerRepository.save(volunteer);
    }

}
