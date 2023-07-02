package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.model.Message;

public interface Handler {
    void handle(Message message);
}
