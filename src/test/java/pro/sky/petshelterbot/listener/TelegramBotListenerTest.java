package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.petshelterbot.handler.*;
import pro.sky.petshelterbot.repository.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static pro.sky.petshelterbot.constants.Commands.START;

@ExtendWith(MockitoExtension.class)
class TelegramBotListenerTest {

    final private Long CHAT_ID = 123L;

    @Mock
    private TelegramBot telegramBot;
    @Mock
    private AdopterRepository adopterRepository;
    @Mock
    private VolunteerRepository volunteerRepository;
    @Mock
    private ShelterRepository shelterRepository;
    @Mock
    private UserMessageRepository userMessageRepository;
    @Mock
    private ButtonRepository buttonsRepository;
    @Mock
    private DialogRepository dialogRepository;
    @Mock
    private BasicAdopterHandler basicAdopterHandler ;
    @Mock
    private VolunteerDialogHandler volunteerDialogHandler;
    @Mock
    private AdopterInputHandler adopterInputHandler;
    @Mock
    AdopterDialogHandler adopterDialogHandler;
    @Mock
    ShelterInfoHandler shelterInfoHandler;
    @Mock
    DefaultHandler defaultHandler;


    @InjectMocks
    private TelegramBotListener telegramBotListener;

    private List<Update> getUpdates(String content) throws URISyntaxException, IOException {
        String json = Files.readString(
                Paths.get(TelegramBotListenerTest.class.getResource(
                                "text_update.json"
                        ).toURI()
                )
        );

        return Collections.singletonList(BotUtils.fromJson(
                        json.replace("%command%", content),
                        Update.class
                )
        );
    }

    @Test
    void process() throws URISyntaxException, IOException {

        telegramBotListener.process(getUpdates(START));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(CHAT_ID);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Для планирования задачи отправьте её в формате:\n*01.01.2022 20:00 Сделать домашнюю работу*");
        Assertions.assertThat(actual.getParameters().get("parse_mode"))
                .isEqualTo(ParseMode.Markdown.name());
    }
}