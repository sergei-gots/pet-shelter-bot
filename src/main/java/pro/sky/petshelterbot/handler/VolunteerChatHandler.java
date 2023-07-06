package pro.sky.petshelterbot.handler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VolunteerChatHandler {

    final private TelegramBot telegramBot;
    final private Map<Long, Long> volunteerToUserChatIds;


    public VolunteerChatHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.volunteerToUserChatIds = new HashMap<>();
    }

    public void handleVolunteerCommand(Long chatId) {
        // Отправляем уведомление волонтеру с просьбой зайти в чат
        telegramBot.execute(new SendMessage(chatId, "Вас позвали в чат. Нажмите кнопку 'Присоединиться к чату' для начала общения.")
                .replyMarkup(new InlineKeyboardMarkup(
                        new InlineKeyboardButton("Присоединиться к чату").callbackData("join_chat")
                )));

        // Создаем отдельный канал для общения волонтера и пользователя
        Long userChatId = createChatChannel();

        // Сохраняем связь между идентификаторами чатов волонтера и пользователя
        volunteerToUserChatIds.put(chatId, userChatId);
    }

    public void CallbackQuery(CallbackQuery callbackQuery) {

        String data = callbackQuery.data();
        Long chatId = callbackQuery.message().chat().id();

        if (data.equals("join_chat")) {
            // Получаем идентификатор чата пользователя
            Long userChatId = volunteerToUserChatIds.get(chatId);

            // Отправляем пользователю уведомление о присоединении волонтера к чату
            telegramBot.execute(new SendMessage(userChatId, "Волонтер присоединился к чату. Теперь вы можете общаться прямо внутри бота."));
        }
    }

    private Long createChatChannel() {
        // Создаем отдельный канал для общения волонтера и пользователя
        // Можно использовать методы Telegram API для создания канала

        // Возвращаем идентификатор созданного канала
        return 123456789L;
    }
}
