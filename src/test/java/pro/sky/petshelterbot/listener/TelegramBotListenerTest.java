package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static pro.sky.petshelterbot.constants.Commands.START;

@SpringBootTest
class TelegramBotListenerTest {

    final private Long CHAT_ID = 123L;

    @MockBean
    private TelegramBot telegramBot;
    @Autowired
    private TelegramBotListener telegramBotListener;


    private String readJsonFromResource(String filename) throws URISyntaxException, IOException {
        return Files.readString(
                Paths.get(TelegramBotListenerTest.class.getResource(
                        filename
                ).toURI()
            )
        );
    }
    private List<Update> getUpdates(String content) throws URISyntaxException, IOException {
        String json = readJsonFromResource( "text_update.json");

        return Collections.singletonList(BotUtils.fromJson(
                        json.replace("%command%", content),
                        Update.class
                )
        );
    }


    @Test
    void process() throws URISyntaxException, IOException {

        when(telegramBot.execute(any())).thenReturn(
                BotUtils.fromJson(readJsonFromResource("send_response.json"),
                        SendResponse.class));

        telegramBotListener.process(getUpdates(START));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        //Verify SendMessage-s:
        //  1) Greet User
        //  2) Next Level Menu
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();
        // Check Menu Message:
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(CHAT_ID);
        Assertions.assertThat(actual.getParameters().size()).isEqualTo(4);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Выберите приют:");
        Assertions.assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        Assertions.assertThat(actual.getParameters().get("parse_mode"))
                .isEqualTo(ParseMode.Markdown.name());
    }
}