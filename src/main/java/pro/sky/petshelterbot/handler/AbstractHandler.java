package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.jetbrains.annotations.NotNull;
import pro.sky.petshelterbot.entity.*;
import pro.sky.petshelterbot.exceptions.ShelterException;
import pro.sky.petshelterbot.repository.*;

import java.util.Collection;
import java.util.NoSuchElementException;

public abstract class AbstractHandler extends AbstractMetaHandler {

    final protected TelegramBot telegramBot;
    final protected AdopterRepository adopterRepository;
    final protected VolunteerRepository volunteerRepository;
    final protected ShelterRepository shelterRepository;
    final protected ButtonRepository buttonsRepository;
    final protected UserMessageRepository userMessageRepository;
    final protected DialogRepository dialogRepository;

    protected AbstractHandler(TelegramBot telegramBot,
                              AdopterRepository adopterRepository,
                              VolunteerRepository volunteerRepository,
                              ShelterRepository shelterRepository,
                              UserMessageRepository userMessageRepository,
                              ButtonRepository buttonsRepository,
                              DialogRepository dialogRepository) {
        this.telegramBot = telegramBot;
        this.adopterRepository = adopterRepository;
        this.volunteerRepository = volunteerRepository;
        this.shelterRepository = shelterRepository;
        this.userMessageRepository = userMessageRepository;
        this.buttonsRepository = buttonsRepository;
        this.dialogRepository = dialogRepository;

    }

    protected boolean handleChatStateDefault(Adopter adopter) {
        switch(adopter.getChatState()) {
            case ADOPTER_IN_SHELTER_INFO_MENU:
                showShelterInfoMenu(adopter);
                return true;
            case ADOPTER_IN_ADOPTION_INFO_MENU:
                showAdoptionInfoMenu(adopter);
                return true;
        }
        return false;
    }


    @Override
    public boolean handleImg(Update update) {
        if (update.message().text() != null) {
            return false;
        }
        Adopter adopter = getAdopter(update.message());
        if(adopter.getChatState()
                != ChatState.ADOPTER_INPUTS_REPORT_IMAGE) {
            sendMessage(adopter.getChatId(),
                    "В настоящее время пересылка фотографий или документов в чате не предусмотрена кроме загрузки фотографии питомца для отчёта-)");
            return true;
        }
        return false;
    }

    @NotNull
    protected Adopter getAdopter(Message message) {
        Long chatId = message.chat().id();
        Adopter adopter = adopterRepository.findByChatId(chatId).orElse(null);
        if (adopter == null) {
            adopter = adopterRepository.save(new Adopter(message.chat().id(), message.chat().firstName()));
        }
        return adopter;
    }

    /** @return Shelter-entity by id
     * @throws NoSuchElementException in case then Shelter with the id=shelterId is not listed in the database.
     */
    protected Shelter getShelter(Long shelterId) {
        logger.trace("getShelter(shelterId={})", shelterId);
        return shelterRepository.findById(shelterId)
                .orElseThrow(()->new NoSuchElementException(
                        "The shelter with id=" + shelterId + "is not listed in the db."));
    }

    protected boolean isShelterToBeAssigned(Adopter adopter, String currentKey) {
        if (adopter.getShelter() != null) {
            return false;
        }
        if (adopter.getChatState() == ChatState.ADOPTER_CHOICES_SHELTER) {
            processShelterChoice(adopter, currentKey);
        } else {
            reselectShelter(adopter);
        }
        return true;
    }

    protected void reselectShelter(Adopter adopter) {
        handleCancelVolunteerCall(adopter, RESET_SHELTER);
        adopter.setShelter(null);
        showShelterChoiceMenu(adopter);
        adopter.setChatState(ChatState.ADOPTER_CHOICES_SHELTER);
        adopterRepository.save(adopter);
    }

    protected void processResumeChat(Adopter adopter) {
        if(adopter.getShelter() == null) {
            reselectShelter(adopter);
        }
        else {
            showShelterInfoMenu(adopter);
        }
    }

    public void showShelterChoiceMenu(Adopter adopter) {

        //If menu has been already sent
        if(adopter.getChatState().equals(ChatState.ADOPTER_CHOICES_SHELTER)) {
            return;
        }

        Collection<Shelter> shelters = shelterRepository.findAll();
        // Create buttons to choose shelter
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Shelter shelter : shelters) {
            markup.addRow(
                    new InlineKeyboardButton(shelter.getName())
                            .callbackData(SHELTER_CHOICE + shelter.getId().toString())
            );
        }
        sendMenu(adopter, "Выберите приют:", markup);
    }

    protected void processShelterChoice(Adopter adopter, String key) {

        logger.debug("processShelterChoice(adopter={}, key=\"{}\")", adopter, key);
        long shelterId;
        try {
            shelterId = Long.parseLong(key.substring(SHELTER_CHOICE.length()));
        } catch (NumberFormatException e) {
            logger.error("processShelterChoice(): invalid key=\"{}\" to parse as shelter_id",
                    key, e);
            return;
        }

        Shelter shelter = shelterRepository
                .findById(shelterId)
                .orElseThrow(() -> new ShelterException("There is no shelter with id=" + shelterId + " in db.))"));

        adopter.setShelter(shelter);
        adopterRepository.save(adopter);

        sendMessage(adopter.getChatId(), "Вы выбрали шелтер \"<b>"
                + shelter.getName() + "</b>\"");

        showShelterInfoMenu(adopter);
    }


    protected String getUserMessage(MessageKey key) {
        return getUserMessage(key.name());
    }
    protected String getUserMessage(String key) {
            logger.trace("getUserMessage(key={})", key);
            return  userMessageRepository.findFirstByKeyAndShelterIsNull(key)
                    .orElse(new UserMessage())
            .getMessage();
        }
        protected String getUserMessage(String key, Shelter shelter) {
            Long shelterId = (shelter!=null) ? shelter.getId() : null;
            logger.trace("getUserMessage(key={}, shelter.id={})",
                    key, shelterId);
            UserMessage userMessage =  userMessageRepository
                    .findFirstByKeyAndShelterId(key, shelterId)
                    .orElse(null);
            if(userMessage == null) {
                logger.trace("getUserMessage: try to find by key={}, shelter.id=null", key);
                userMessage = userMessageRepository.findFirstByKeyAndShelterIsNull(key)
                        .orElseThrow(()->new NoSuchElementException(
                                "The user_message with key=\"" + key + "\" is not listed in the db.")
                        );
            }
            return userMessage.getMessage();
        }

    protected void greetUser(Person person) {
        sendMessage(person.getChatId(), "Здравствуйте, " + person.getFirstName());
    }

    protected void sendMessage(Long chatId, String text, Keyboard keyboard) {
        logger.trace("sendMessage(chatId={}, text=\"{}\") with kbMarkUp", chatId, text);
        if(text == null) {
            return;
        }

        telegramBot.execute(new SendMessage(chatId, text).parseMode(ParseMode.HTML).replyMarkup(keyboard));
    }

    protected void sendMessage(Long chatId, String text) {
        logger.trace("sendMessage(chatId={}, text=\"{}\")", chatId, text);
        telegramBot.execute(new SendMessage(chatId, text).parseMode(ParseMode.HTML)).message().messageId();
    }

    /** Retrieves message by (key, shelter_id) from the table containing user_messages and send
     * it to the user. If message is not found user will be notified that developers have been
     * working on fixing it.
     * @param key - user_messages.key for message
     */
    protected boolean sendUserMessage(long chatId, String key, Shelter shelter) {
        Long shelterId = (shelter!=null)? shelter.getId() : null;
        logger.trace("sendUserMessage(chatId={}, key=\"{}\", shelter.id={})",
                chatId, key, shelterId);
        String userMessage;
        try {
            userMessage = getUserMessage(key, shelter);
        } catch(NoSuchElementException e) {
            logger.error("sendUserMessage-method: user_message {key={}, shelter.id=\"{}\" is not listed in the db.",
                    key, shelterId);
            return false;
        }
        telegramBot.execute(new SendMessage(chatId, userMessage).parseMode(ParseMode.HTML));
        return true;
    }

    protected void sendUserMessage(Person person, MessageKey messageKey) {
        sendUserMessage(person, messageKey.name());
    }
    protected boolean sendUserMessage(Person person, String key) {
        return sendUserMessage(person.getChatId(), key, person.getShelter());
    }

    protected void sendMenu(Person person, String chapter) {
        sendMenu(person, chapter, "");
    }

    protected void sendMenu(Person person, String chapter, String title) {

        Shelter shelter = person.getShelter();
        title = (title.isEmpty()) ? getUserMessage(chapter, shelter) : title;
               sendMenu(
                        person,
                        title,
                        createMenu(person.getChatId(), chapter, shelter)
        );
    }

    protected void sendMenu(Person person, String header, InlineKeyboardMarkup markup) {

        long chatId = person.getChatId();
        logger.trace("sendMenu(): chatId={}, header=\"{}\"", chatId, header);

        deletePreviousMenu(person);
        SendResponse response =  telegramBot
                .execute(new SendMessage(chatId, header)
                        .replyMarkup(markup));
        int messageId = response.message().messageId();
        logger.trace("sendMenu(): menu-message-Id()={}", messageId);
        person.setChatMenuMessageId(messageId);
        if (person instanceof Adopter) {
            adopterRepository.save((Adopter) person);
        }
        else if(person instanceof Volunteer) {
            volunteerRepository.save((Volunteer) person);
        }
        else {
            logger.error("sendMenu(): person with firstName={} is instance of unsupported class ",
                    person.getClass());
        }
    }

    /**
     *  Do not forget to update person in db after call this method
     */
    protected void deletePreviousMenu(Person person) {
        Integer chatMenuMessageId = person.getChatMenuMessageId();
        if(chatMenuMessageId==null) {
            logger.debug("deletePreviousMenu(Person={}) => menu for adopter was not defined", person);
            return;
        }
        telegramBot.execute(new DeleteMessage(person.getChatId(), chatMenuMessageId));
        logger.trace("deletePreviousMenu(Adopter={})", person);
        person.resetChatMenuMessageId();
    }
    protected InlineKeyboardMarkup createMenu(long chatId, String chapter, Shelter shelter) {

        Collection<Button> buttons = buttonsRepository.findByShelterAndChapterOrderById(shelter, chapter);
        buttons.addAll(buttonsRepository.findByChapterAndShelterIsNullOrderById(chapter));

        if (buttons.size() == 0) {
            logger.error("makeButtonList(): There isn't button list in db for person={}, shelter.id={}, chapter=\"{}\".",
                    chatId, chapter, shelter);
            throw(new IllegalStateException("makeButtonList(): There isn't button list in db for chatId=" +
                    chatId + ", shelter.id=" + shelter.getId() + ", chapter=\"" + chapter + "\"."));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Button button : buttons) {
            String key = button.getKey();
            if(getDialogIfRequested(chatId) != null) {
                if(key.startsWith(CALL_VOLUNTEER) ||
                        key.equals(ENTER_CONTACTS) ||
                        key.equals(ENTER_REPORT)
                ) {
                    continue;
                }
            }
            else {
                if(key.startsWith(CANCEL_VOLUNTEER_CALL)) {
                    continue;
                }
            }
            markup.addRow(new InlineKeyboardButton(button.getText()).callbackData(key));
        }

        return markup;
    }
    protected Dialog getDialogIfRequested(Adopter adopter) {
        return dialogRepository.findByAdopter(adopter).orElse(null);
    }

    protected Dialog getDialogIfRequested(Long chatId) {
        return dialogRepository.findByAdopterChatId(chatId).orElse(null);
    }

    protected void showCurrentMenu(Adopter adopter) {
        logger.debug("showCurrentMenu(adopter={}): chat_state={}",
                adopter.getChatId(), adopter.getChatState());

        if(adopter.getChatState().equals(ChatState.ADOPTER_IN_ADOPTION_INFO_MENU)){
            showAdoptionInfoMenu(adopter);
        }
        else {
            showShelterInfoMenu(adopter);
        }
    }

    protected void showShelterInfoMenu(Adopter adopter) {
        sendMenu(adopter, SHELTER_INFO_MENU);
        adopter.setChatState(ChatState.ADOPTER_IN_SHELTER_INFO_MENU);
        adopterRepository.save(adopter);
    }

    protected void showAdoptionInfoMenu(Adopter adopter) {
        sendMenu(adopter, ADOPTION_INFO_MENU);
        adopter.setChatState(ChatState.ADOPTER_IN_ADOPTION_INFO_MENU);
        adopterRepository.save(adopter);
    }

    protected void forwardDialogMessage(Person sender, Person receiver, String text) {
        logger.trace("forwardMessageWithinDialog.  sender.firstName={}, receiver.firstName={}",
                sender.getFirstName(), receiver.getFirstName());
        sendMessage(receiver.getChatId(),
                sender.getFirstName() + "> " + text);
    }

    public boolean handleCancelVolunteerCall(Adopter adopter, String key) {
        if( !key.startsWith(CANCEL_VOLUNTEER_CALL)
                && !key.equals(CLOSE_DIALOG)
                && !key.equals(CLOSE_DIALOG_RU)
                && !key.equals(RESET_SHELTER)
        ) {
            return false;
        }

        long chatId = adopter.getChatId();
        logger.debug("handleCancelVolunteerCall(adopter.chat_id={})", chatId);
        Dialog dialog = getDialogIfRequested(adopter);
        Volunteer volunteer = null;
        if (dialog != null) {
            volunteer = dialog.getVolunteer();
            dialogRepository.delete(dialog);
        } else {
            logger.debug("Dialog for adopter.chatId=" + chatId + " is not listed in db. It could be ok.");
        }

        if(volunteer != null) {
            deletePreviousMenu(volunteer);
            showShelterInfoMenu(adopter);
            forwardDialogMessage(volunteer, adopter, "Всего вам наилучшего:) Если у вас возникнут вопросы, обращайтесь ещё!");
            sendMessage(volunteer.getChatId(), "Диалог c "
                    + adopter.getFirstName() + " завершён. Спасибо:)");
            volunteer.setAvailable(true);
            volunteerRepository.save(volunteer);
            return true;
        } else {
            notifyAllAvailableShelterVolunteersAboutNoRequest(adopter.getShelter());
        }
        sendMessage(chatId, "Заявка на диалог с волонтёром снята");
        showCurrentMenu(adopter);
        return true;
    }

    protected void notifyAllAvailableShelterVolunteersAboutNoRequest(Shelter shelter) {
        for(Volunteer availableVolunteer : volunteerRepository.findByShelterAndAvailableIsTrue(shelter)) {
            logger.trace("processJoinDialog()-method. volunteer.getFirstName()=\"{}\" will be notified that all the dialogs have been picked up",
                    availableVolunteer.getFirstName());
            deletePreviousMenu(availableVolunteer);
            telegramBot.execute(
                    new SendMessage(availableVolunteer.getChatId(), "Все запросы на консультацию были подхвачены, спасибо!" +
                            "Мы известим вас о новых запросах на консультацию:)"));
        }
    }
}
