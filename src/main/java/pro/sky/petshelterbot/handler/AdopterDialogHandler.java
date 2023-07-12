package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.*;

import java.util.List;
import java.util.Random;

/**
 * Operates chat between Adopter and Volunteer on the Adopter's side
 */
@Component
public class AdopterDialogHandler extends AbstractDialogHandler  {

    private final AdopterRepository adopterRepository;

    private final Random random = new Random();

    public AdopterDialogHandler(TelegramBot telegramBot,
                                ShelterRepository shelterRepository,
                                UserMessageRepository userMessageRepository,
                                DialogRepository dialogRepository,
                                AdopterRepository adopterRepository,
                                VolunteerRepository volunteerRepository
    ) {
        super(telegramBot, shelterRepository, userMessageRepository, volunteerRepository, dialogRepository);
        this.adopterRepository = adopterRepository;
    }


    @Override
    public boolean handle(Message message) {
        Long chatId = message.chat().id();
        Dialog dialog = getDialogIfRequested(message.chat().id());
        if(dialog == null) {
            return false;
        }

        if(dialog.getVolunteer() == null) {
            sendMessage(chatId, "Подождите, волонтёр скоро свяжется с вами. Приношу извинения за ваше ожидание! Спасибо)");
            return true;
        }

        sendMessage(dialog.getVolunteer().getChatId(),
                dialog.getAdopter().getFirstName().toUpperCase() +
                        "> " + message.text());
        return true;


    }

    @Override
    public boolean handle(Message message, String key, Long chatId, Long shelterId) {
        if (CALL_VOLUNTEER.equals(key)) {
            handleVolunteerCall(message, chatId, shelterId);
            return true;
        }
        return false;
    }

    public void handleVolunteerCall(Message message, Long chatId, Long shelterId) {
        logger.debug("handleVolunteerCall(chatId={}, shelterId={})", chatId, shelterId);
        if (getDialogIfRequested(chatId) != null) {
         //   throw new IllegalStateException("Dialog for chatId=" + chatId + " is already open");
        }
        createDialogRequest(message, chatId, shelterId);
    }

    private void createDialogRequest(Message message, Long chatId, Long shelterId) {

        logger.trace("createDialogRequest(message={}, ...", message);

        Dialog dialog = createDialogEntry(message, chatId, shelterId);
        if (dialog.getVolunteer() == null) {
            sendMessage(chatId, "В настоящий момент все волонтёры заняты. "
                    + "Как только один из волонтёров освободится, он свяжется с вами.");
            return;
        }
        sendJoinInvitationToVolunteerAndNotifyAdopter(dialog);
    }

    private Dialog createDialogEntry(Message message, Long chatId, Long shelterId) {

        logger.trace("createDialogEntry(message={}, ...", message);

        Adopter adopter = adopterRepository.findByChatId(chatId)
                .orElse(adopterRepository.save(new Adopter(chatId, message.chat().firstName()))
                );
        List<Volunteer> availableVolunteers = volunteerRepository.findByShelterIdAndAvailable(shelterId, true);
        if (availableVolunteers.size() == 0) {
           Dialog dialog = new Dialog(adopter, shelterRepository.findById(shelterId)
                    .orElseThrow(() -> new IllegalArgumentException("shelter_id=" + shelterId + "is not listed.")));
           dialogRepository.save(dialog);
           return dialog;
        }

        Volunteer volunteer = availableVolunteers.get(random.nextInt(availableVolunteers.size()));
        Dialog dialog = new Dialog(adopter, volunteer);
        volunteer.setAvailable(false);
        dialogRepository.save(dialog);
        volunteerRepository.save(volunteer);
        return dialog;
    }

    private Dialog getDialogIfOpen(long chatId) {
       Dialog dialog = dialogRepository.findByAdopterChatId(chatId).orElse(null);

        if(dialog == null || dialog.getVolunteer() == null) {
            return null;
        }
        return dialog;
    }

    private Dialog getDialogIfRequested(long chatId)
    {
        return dialogRepository.findFirstByAdopterChatId(chatId).orElse(null);
    }

}
