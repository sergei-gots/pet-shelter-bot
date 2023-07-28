package pro.sky.petshelterbot.entity;

import pro.sky.petshelterbot.constants.TelegramChatStates;

public interface Person extends TelegramChatStates {
    /**
     * Person will be identified by its chat_id which is persistent within
     * Telegram
     */
    Long getChatId();

    void setChatId(Long chatId);

    String getFirstName();

    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    Shelter getShelter();

    Integer getChatMenuMessageId();

    void resetChatMenuMessageId();

    void setChatMenuMessageId(Integer chatMenuMessageId);
}
