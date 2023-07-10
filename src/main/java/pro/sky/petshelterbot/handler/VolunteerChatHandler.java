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

/** Operates chat between Adopter and Volunteer on the Adopter's side
 *
 */
@Component
public class VolunteerChatHandler extends AbstractHandler {

    private final DialogRepository dialogRepository;
    private final AdopterRepository adopterRepository;
    private final VolunteerRepository volunteerRepository;


    private final Random random = new Random();

    public VolunteerChatHandler(TelegramBot telegramBot,
                                ShelterRepository shelterRepository,
                                UserMessageRepository userMessageRepository,
                                DialogRepository dialogRepository,
                                AdopterRepository adopterRepository,
                                VolunteerRepository volunteerRepository
                                ) {
        super(telegramBot, shelterRepository, userMessageRepository);
        this.dialogRepository = dialogRepository;
        this.adopterRepository = adopterRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public boolean handle(Message message, String key, Long chatId, Long shelterId) {
        if ("volunteer_call".equals(key)) {
            handleVolunteerCall(message, chatId, shelterId);
            return true;
        }
        return false;
    }

    public void handleVolunteerCall(Message message, Long chatId, Long shelterId) {
        logger.debug("handleVolunteerCall(chatId={}, shelterId={})", chatId, shelterId);
        if (isDialogOpen(chatId) || isDialogRequested(chatId)) {
            throw new IllegalStateException("Dialog for chatId=" + chatId + " is already open");
        }
        createDialogRequest(message, chatId, shelterId);
    }

    private void createDialogRequest(Message message, Long chatId, Long shelterId) {

        logger.trace("createDialogRequest(message={}, ...", message);

        Volunteer volunteer = createDialogEntry(message, chatId, shelterId);
        if (volunteer == null) {
            sendMessage(chatId, "В настоящий момент все волонтёры заняты. "
                            + "Как только один из волонтёров освободится, он свяжется с вами.");
            return;
        }
        sendMessage(volunteer.getChatId(),
                volunteer.getFirstName() + "! C вами хотел бы связаться " + message.chat().firstName() +
                        ". Нажмите кнопку 'Присоединиться к чату' для начала общения.",
                        "Присоединиться к чату",
                        Volunteer.JOIN_CHAT
                );
        sendMessage(chatId, "Волонтёру отослано уведомление. Волонтёр свяжется с вами " +
                "насколько это возможно скоро. ");

    }

    private Volunteer createDialogEntry(Message message, Long chatId, Long shelterId) {

        logger.trace("createDialogEntry(message={}, ...", message);

        Adopter adopter = adopterRepository.findByChatId(chatId)
                .orElse(adopterRepository.save(new Adopter(chatId, message.chat().firstName()))
                );
        List<Volunteer> availableVolunteers = volunteerRepository.findByShelterIdAndAvailable(shelterId, true);
        if (availableVolunteers.size() == 0) {
            Dialog dialog = new Dialog(adopter, shelterRepository.findById(shelterId)
                    .orElseThrow(()->new IllegalArgumentException("shelter_id=" + shelterId  + "is not listed.")));
            return null;
        }

        Volunteer volunteer = availableVolunteers.get(random.nextInt(availableVolunteers.size()));
        Dialog dialog = new Dialog(adopter, volunteer);
        volunteer.setAvailable(false);
        dialogRepository.save(dialog);
        volunteerRepository.save(volunteer);
        return volunteer;
    }

    private boolean isDialogOpen(long chatId) {

        return false;
    }

    private boolean isDialogRequested(long chatId) {
        return false;
    }

    private void closeDialog(Long chatId) {
    }

}
