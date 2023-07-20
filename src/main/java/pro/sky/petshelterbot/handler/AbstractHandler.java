package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.petshelterbot.entity.*;
import pro.sky.petshelterbot.repository.AdopterRepository;
import pro.sky.petshelterbot.repository.ButtonRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.Collection;
import java.util.NoSuchElementException;

public abstract class AbstractHandler implements Handler{

    final protected Logger logger = LoggerFactory.getLogger(getClass());
    final protected TelegramBot telegramBot;
    final protected AdopterRepository adopterRepository;
    final protected ShelterRepository shelterRepository;
    final protected ButtonRepository buttonsRepository;
    final protected UserMessageRepository userMessageRepository;

    protected AbstractHandler(TelegramBot telegramBot,
                              AdopterRepository adopterRepository,
                              ShelterRepository shelterRepository,
                              UserMessageRepository userMessageRepository,
                              ButtonRepository buttonsRepository) {
        this.telegramBot = telegramBot;
        this.adopterRepository = adopterRepository;
        this.shelterRepository = shelterRepository;
        this.userMessageRepository = userMessageRepository;
        this.buttonsRepository = buttonsRepository;
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
            logger.trace("getUserMessage(key={}, shelter.id={})", key, shelter.getId());
            UserMessage userMessage =  userMessageRepository
                    .findFirstByKeyAndShelterId(key, shelter.getId())
                    .orElse(null);
            if(userMessage == null) {
                logger.trace("getUserMessage: try to find by key={}, shelter.id=null", key);
                userMessage = userMessageRepository.findFirstByKeyAndShelterIsNull(key)
                        .orElseThrow(()->new NoSuchElementException(
                                "The user_message with key=\"" + key + "\"is not listed in the db.")
                        );
            }

            return userMessage.getMessage();
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
        if(text == null) {
            return;
        }
        telegramBot.execute(new SendMessage(chatId, text).parseMode(ParseMode.HTML));
    }

    /** Retrieves message by (key, shelter_id) from the table containing user_messages and send
     * it to the user. If message is not found user will be notified that developers have been
     * working on fixing it.
     * @param key - user_messages.key for message
     */
    protected void sendUserMessage(long chatId, String key, Shelter shelter) {

        logger.trace("sendUserMessage(chatId={}, key=\"{}\", shelter.id={})",
                chatId, key, shelter.getId());
        String userMessage;
        try {
            userMessage = getUserMessage(key, shelter);
        } catch(NoSuchElementException e) {
            userMessage = "Раздел не создан. Разработчики скоро сформируют этот раздел";
            logger.error("sendUserMessage-method: user_message {shelter.id={}, key=\"{}\" is not listed in the db.",
                    key, shelter.getId());
        }
        telegramBot.execute(new SendMessage(chatId, userMessage).parseMode(ParseMode.HTML));
    }

    protected void sendUserMessage(Person person, String key) {
        sendUserMessage(person.getChatId(), key, person.getShelter());
    }


    protected void sendMenu(Adopter adopter, String chapter) {

        long chatId = adopter.getChatId();
        Shelter shelter = adopter.getShelter();

        deletePreviousMenu(adopter);

        adopter.setChatMenuMessageId(
                sendMenu(
                        chatId,
                        getUserMessage(chapter, shelter),
                        createMenu(chatId, chapter, shelter)
                )
        );

        adopterRepository.save(adopter);
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

     protected int sendMenu(long chatId, String header, InlineKeyboardMarkup markup) {
        logger.trace("sendMenu(): chatId={}, header=\"{}\"", chatId, header);

        SendResponse response =  telegramBot
                .execute(new SendMessage(chatId, header)
                .replyMarkup(markup));
        int messageId = response.message().messageId();
        logger.trace("sendMenu(): response.message().messageId()={}", messageId);
        return messageId;
    }

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

    protected Dialog getDialogIfRequested(Long adopterChatId) {
        return adopterRepository.findDialogByAdopterChatId(adopterChatId).orElse(null);
    }

    public void showShelterInfoMenu(Adopter adopter) {
        sendMenu(adopter, SHELTER_INFO_MENU);
    }
}
