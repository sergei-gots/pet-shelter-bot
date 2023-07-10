package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.AdopterRepository;
import pro.sky.petshelterbot.repository.DialogRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.VolunteerRepository;

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

    private final ShelterRepository shelterRepository;

    private final Random random = new Random();

    public VolunteerChatHandler(TelegramBot telegramBot, DialogRepository dialogRepository, AdopterRepository adopterRepository, VolunteerRepository volunteerRepository, ShelterRepository shelterRepository) {
        super(telegramBot);
        this.dialogRepository = dialogRepository;
        this.adopterRepository = adopterRepository;
        this.volunteerRepository = volunteerRepository;
        this.shelterRepository = shelterRepository;
    }

    public void handleVolunteerCommand(Long chatId) {
        // Отправляем уведомление волонтеру с просьбой зайти в чат
        telegramBot.execute(new SendMessage(
                chatId,
                "Вас позвали в чат. Нажмите кнопку 'Присоединиться к чату' для начала общения.")
                .replyMarkup(new InlineKeyboardMarkup(
                        new InlineKeyboardButton("Присоединиться к чату").callbackData("join_chat")
                )));

        // Создаем отдельный канал для общения волонтера и пользователя
        Long userChatId = createChatChannel();

        // Сохраняем связь между идентификаторами чатов волонтера и пользователя
        //volunteerToUserChatIds.put(chatId, userChatId);
    }

    public void CallbackQuery(CallbackQuery callbackQuery) {

        String data = callbackQuery.data();
        Long chatId = callbackQuery.message().chat().id();

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
        sendMessage(chatId, "Волонтёру отослано уведомление. Волонтёр свяжется с вами " +
                        "насколько это возможно скоро. ");
        sendMessage(volunteer.getChatId(),
                volunteer.getFirstName() + "! C вами хотел бы связаться " + message.chat().firstName());

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
