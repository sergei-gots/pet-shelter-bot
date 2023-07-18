package pro.sky.petshelterbot.entity;

public interface Person {
    /**
     * Person will be identified by its chat_id which is persistent within
     * Telegram
     */
    long getChatId();

    void setChatId(long chatId);

    String getFirstName();

    Shelter getShelter();

    Integer getChatMenuMessageId();

    void resetChatMenuMessageId();

    void setChatMenuMessageId(Integer chatMenuMessageId);
}
