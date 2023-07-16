package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.AdopterRepository;
import pro.sky.petshelterbot.repository.ButtonRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest
public class ShelterInfoHandlerTest {

    private ShelterInfoHandler shelterInfoHandler;
    @Mock
    private TelegramBot telegramBot;
    @Mock
    private AdopterRepository adopterRepository;
    @Mock
    private ButtonRepository buttonsRepository;
    @Mock
    private UserMessageRepository userMessageRepository;
    @Mock
    private ShelterRepository shelterRepository;

    @Mock
    private AdopterDialogHandler adopterDialogHandler;

    @Mock
    private Message message;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        shelterInfoHandler = new ShelterInfoHandler(telegramBot, adopterRepository, shelterRepository,
                userMessageRepository, buttonsRepository, adopterDialogHandler);
    }

    @Test
    public void testSendOpeningHours() {
        Long chatId = 123456789L;
        Long shelterId = 1L;
        Shelter shelter = new Shelter();
        shelter.setWorkTime("9:00 - 18:00");
        shelter.setAddress("123 Main Street");

        when(shelterRepository.findById(shelterId)).thenReturn(java.util.Optional.of(shelter));

        shelterInfoHandler.sendOpeningHours(chatId, shelterId);

        verify(telegramBot).execute(new SendMessage(chatId, "Расписание работы и адрес приюта:\n" +
                "9:00 - 18:00\n" +
                "Адрес: 123 Main Street"));
    }

    @Test
    public void testSendSecurityInfo() {
        Long chatId = 123456789L;
        Long shelterId = 1L;
        Shelter shelter = new Shelter();
        shelter.setTel("1234567890");
        shelter.setEmail("shelter@example.com");

        when(shelterRepository.findById(shelterId)).thenReturn(java.util.Optional.of(shelter));

        shelterInfoHandler.sendSecurityInfo(chatId, shelterId);

        verify(telegramBot).execute(new SendMessage(chatId, "Контактные данные охраны приюта:\n" +
                "Телефон: 1234567890\n" +
                "Email: shelter@example.com"));
    }
}