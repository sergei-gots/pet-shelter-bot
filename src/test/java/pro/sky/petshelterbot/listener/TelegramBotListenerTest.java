package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static pro.sky.petshelterbot.constants.Commands.START;

@ExtendWith(MockitoExtension.class)
class TelegramBotListenerTest {

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private TelegramBotListener telegramBotListener;

    private Update getUpdate(String content) throws URISyntaxException, IOException {
        String json = Files.readString(
                Paths.get(TelegramBotListenerTest.class.getResource(
                                "text_update.json"
                        ).toURI()
                )
        );

        return BotUtils.fromJson(
                json.replace("%command%", content),
                Update.class
        );
    }

    @Test
    void process() throws URISyntaxException, IOException {

        telegramBotListener.process(
                Collections.singletonList(
                        getUpdate(START)));
    }
}