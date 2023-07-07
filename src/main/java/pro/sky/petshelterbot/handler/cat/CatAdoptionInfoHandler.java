package pro.sky.petshelterbot.handler.cat;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.UserMessage;
import pro.sky.petshelterbot.repository.UserMessageRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Handles user's pressing a button and sends information about how to adopt a pet
 */
@Component
public class CatAdoptionInfoHandler {
    final private TelegramBot telegramBot;

    final private UserMessageRepository userMessageRepository;

    public CatAdoptionInfoHandler(TelegramBot telegramBot, UserMessageRepository userMessageRepository) {
        this.telegramBot = telegramBot;
        this.userMessageRepository = userMessageRepository;
    }

    public void sendAdoptionInfo(Long chatId) {
        // Create buttons
        InlineKeyboardButton button1 = new InlineKeyboardButton("Правила знакомства до того, как забрать животное из приюта").callbackData("cat_adoption_steps");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Требования к потенциальным владельцам").callbackData("cat_requirements_info");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Список документов для оформления").callbackData("cat_documents_info");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Рекомендации по транспортировке").callbackData("cat_transfer_info");
        InlineKeyboardButton button5 = new InlineKeyboardButton("Рекомендации по обустройству дома для котенка").callbackData("cat_house_info");
        InlineKeyboardButton button6 = new InlineKeyboardButton("Рекомендации по обустройству дома для взрослого животного").callbackData("cat_house_adult_info");
        InlineKeyboardButton button7 = new InlineKeyboardButton("Рекомендации по обустройству дома для животного с ограниченными возможностями").callbackData("cat_house_disabled_info");
        InlineKeyboardButton button8 = new InlineKeyboardButton("Оставить контактные данные").callbackData("cat_contact_info");
        InlineKeyboardButton button9 = new InlineKeyboardButton("Позвать волонтера").callbackData("cat_volunteer_info");
        // Create InlineKeyboardMarkup and give it buttons
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                new InlineKeyboardButton[] {button1},
                new InlineKeyboardButton[] {button2},
                new InlineKeyboardButton[] {button3},
                new InlineKeyboardButton[] {button4},
                new InlineKeyboardButton[] {button5},
                new InlineKeyboardButton[] {button6},
                new InlineKeyboardButton[] {button7},
                new InlineKeyboardButton[] {button8},
                new InlineKeyboardButton[] {button9}
        );

        // Send buttons to user
        telegramBot.execute(new SendMessage(chatId, "Выберите интересующую вас информацию:")
                .replyMarkup(markup));
    }


    public void sendAdoptionSteps(Long chatId) {
        // Send schedule information to user
        telegramBot.execute(new SendMessage(chatId, "Правила знакомства следующие:\n" +
                "   - Познакомьтесь с историей животного и его особенностями.\n" +
                "   - Узнайте о привычках и потребностях животного.\n" +
                "   - Проведите время с животным в приюте, чтобы установить контакт"));
    }

    public void sendRequirements(Long chatId) {
        telegramBot.execute(new SendMessage(chatId, "Требования к потенциальным владельцам животных:\n" +
                "   - Вы должны любить животных\n" +
                "   - У вас должны быть деньги и свободное время"));
    }

    public void sendDocuments(Long chatId) {
        telegramBot.execute(new SendMessage(chatId, "Список документов:\n" +
                "   - Паспорт или удостоверение личности.\n" +
                "   - Документы, подтверждающие ваше место жительства.\n" +
                "   - Договор о передаче животного."));
    }

    public void sendTransfer(Long chatId) {
        telegramBot.execute(new SendMessage(chatId, "Рекомендации по транспортировке:\n" +
                "   - Используйте специальную переноску или клетку для безопасности животного.\n" +
                "   - Обеспечьте комфортные условия во время перевозки.\n" +
                "   - Предоставьте доступ к воде и пище во время длительной поездки"));
    }

    public void sendHouseInfo(Long chatId) {
                 UserMessage userMessage = userMessageRepository.
                findById(UserMessage.Key.CAT_SHELTER_INFO_KITTY_AT_HOME)
                         .orElseThrow();
                telegramBot.execute(new SendMessage(
                    chatId, userMessage.getText()
                ));
        /*telegramBot.execute(new SendMessage(chatId, " Рекомендации по обустройству дома для котенка:\n" +
                "   - Создайте безопасную зону для игр и отдыха.\n" +
                "   - Подготовьте место для кормления и сна.\n" +
                "   - Уберите опасные предметы и растения из доступа животного."));

         */
    }

    public void sendHouseInfoAdult(Long chatId) {
        telegramBot.execute(new SendMessage(chatId, "Рекомендации по обустройству дома для взрослого животного:\n" +
                "   - Предоставьте уютное место для отдыха и сна.\n" +
                "   - Обеспечьте доступ к чистой воде и корму.\n" +
                "   - Создайте зону для игр и физической активности."));
    }

    public void sendHouseInfoDisabled(Long chatId) {
        telegramBot.execute(new SendMessage(chatId, "Рекомендации по обустройству дома для животного с ограниченными возможностями (зрение, передвижение):\n" +
                "   - Уберите препятствия на пути передвижения.\n" +
                "   - Обозначьте опасные места или ступеньки.\n" +
                "   - Используйте специальные приспособления для помощи животному."));
    }
}