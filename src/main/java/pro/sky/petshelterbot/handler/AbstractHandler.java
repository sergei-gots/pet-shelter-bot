package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.ButtonRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.Collection;
import java.util.NoSuchElementException;

public abstract class AbstractHandler implements Handler{

    final protected Logger logger = LoggerFactory.getLogger(getClass());
    final protected TelegramBot telegramBot;
    final protected ShelterRepository shelterRepository;
    final protected ButtonRepository buttonsRepository;
    final protected UserMessageRepository userMessageRepository;

    protected AbstractHandler(TelegramBot telegramBot, ShelterRepository shelterRepository,
                              UserMessageRepository userMessageRepository,
                              ButtonRepository buttonsRepository) {
        this.telegramBot = telegramBot;
        this.shelterRepository = shelterRepository;
        this.userMessageRepository = userMessageRepository;
        this.buttonsRepository = buttonsRepository;
    }


    /** @return Shelter-entity by id
     * @throws NoSuchElementException in case then Shelter with the id=shelterId is not listed in the database.
     */
    protected Shelter getShelter(Long shelterId) {
        logger.trace("getShelter(shelterId={})", shelterId);
        return shelterRepository.findById(shelterId)
                .orElseThrow(()->new NoSuchElementException("The shelter with id=" + shelterId + "is not listed in the db."));
    }

    protected String getUserMessage(String key, Long shelterId) {
        logger.trace("getUserMessage(key={}, shelterId={})", key, shelterId);
        return  (userMessageRepository.findByShelterAndKey(
                    getShelter(shelterId), key)
                    .orElseThrow(
                            ()->new NoSuchElementException("The user_message with key=\"" + key + "\"is not listed in the db."))
        ).getMessage();
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

    protected boolean makeButtonList(Long chatId, Long shelterId, String chapter, String title) {

        Collection<Button> buttons = buttonsRepository.findByShelterIdAndChapterOrderById(shelterId, chapter);
        if (buttons.size() == 0) {
            buttons = buttonsRepository.findByChapterOrderById(chapter);
        }
        if (buttons.size() == 0) {
            return false;
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Button button : buttons) {
            markup.addRow(new InlineKeyboardButton(button.getText()).callbackData(shelterId + "-" + button.getKey()));
        }

        sendMenu(chatId, title, markup);

        return true;

    }

    protected void sendMenu(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendResponse response =  telegramBot.execute(new SendMessage(chatId, text).replyMarkup(markup));
        logger.trace("sendMenu()-method: response.message().messageId()={}", response.message().messageId());
    }

}
