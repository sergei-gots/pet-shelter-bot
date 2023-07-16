package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.entity.Shelter;
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
        //return  adopterRepository.findByChatId(message.chat().id())
          //      .orElse(adopterRepository.save(new Adopter(message.chat().id(), message.chat().firstName())));
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

    protected String getUserMessage(String key) {
            logger.trace("getUserMessage(key={})", key);
            return  userMessageRepository.findByKeyAndShelterIsNull(key)
                    .orElseThrow(()->new NoSuchElementException(
                            "The user_message with key=\"" + key + "\"is not listed in the db."))
            .getMessage();
        }
        protected String getUserMessage(String key, Long shelterId) {
            logger.trace("getUserMessage(key={}, shelterId={})", key, shelterId);
            return  userMessageRepository.findByKeyAndShelterId(key, shelterId)
                    .orElseThrow(()->new NoSuchElementException(
                            "The user_message with key=\"" + key + "\"is not listed in the db."))
            .getMessage();
        }


    protected void sendMessage(Long chatId, String text) {
        logger.trace("sendMessage(chatId={}, text=\"{}\")", chatId, text);
        telegramBot.execute(new SendMessage(chatId, text).parseMode(ParseMode.HTML));
    }

    protected void sendMessage(Long chatId, String text, String buttonLabel, String callbackData) {
        logger.trace("sendMessage(chatId={}, text=\"{}\", buttonLabel=\"{}\", callbackData=\"{}\")",
                chatId, text, buttonLabel, callbackData);
        telegramBot.execute(new SendMessage(
                chatId,
                text).parseMode(ParseMode.HTML)
                .replyMarkup(new InlineKeyboardMarkup(
                        new InlineKeyboardButton(buttonLabel).callbackData(callbackData)
                )));
    }

    /** Retrieves message by (key, shelter_id) from the table containing user_messages and send
     * it to the user. If message is not found user will be notified that developers have been
     * working on fixing it.
     * @param adopter - adopter
     * @param key - user_messages.key for message
     * @param shelterId - user_message.shelter_id for message
     */
    protected void sendUserMessage(Adopter adopter, String key, Long shelterId) {
        logger.trace("sendUserMessage(chatId={}, key=\"{}\", shelterId={})",
                adopter.getChatId(), key, shelterId);
        String userMessage;
        try {
            userMessage = getUserMessage(key, shelterId);
        } catch(NoSuchElementException e) {
            userMessage = "Раздел не создан. Разработчики скоро сформируют этот раздел";
            logger.error("sendUserMessage-method: user_message {shelter_id={}, key=\"{}\" is not listed in the db.",
                    shelterId, key);
        }
        telegramBot.execute(new SendMessage(adopter.getChatId(), userMessage).parseMode(ParseMode.HTML));
    }
    protected void makeButtonList(Adopter adopter, String chapter, Long shelterId) {

        Collection<Button> buttons = buttonsRepository.findByShelterIdAndChapterOrderById(shelterId, chapter);
        buttons.addAll(buttonsRepository.findByChapterAndShelterIdIsNullOrderById(chapter));


        if (buttons.size() == 0) {
            logger.error("makeButtonList(...): There isn't button list in db for {}, shelterId={}, chaptr=\"{}\".",
                    adopter, shelterId, chapter);
            return;
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Button button : buttons) {
            markup.addRow(new InlineKeyboardButton(button.getText()).callbackData(shelterId + "-" + button.getKey()));
        }

        sendMenu(adopter, getUserMessage(chapter), markup);
    }

    protected void sendMenu(Adopter adopter, String text, InlineKeyboardMarkup markup) {
        Long chatId = adopter.getChatId();
        SendResponse response =  telegramBot.execute(new SendMessage(chatId, text)
                .replyMarkup(markup));
        int messageId = response.message().messageId();
        logger.trace("sendMenu()-method: response.message().messageId()={}", messageId);

        adopter.setChatMenuMessageId(messageId);
        adopterRepository.save(adopter);
    }

    protected void deletePreviousMenu(Adopter adopter) {
        int chatMenuMessageId = adopter.getChatMenuMessageId();
        if(chatMenuMessageId < 1) {
            logger.error("deletePreviousMenu(Adopter={}) => menu for adopter was not defined", adopter);
            return;
        }
        telegramBot.execute(new DeleteMessage(adopter.getChatId(), chatMenuMessageId));
        logger.trace("deletePreviousMenu(Adopter={})", adopter);
        adopter.setChatMenuMessageId(-1);
        adopterRepository.save(adopter);
    }


}
