package pro.sky.petshelterbot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.service.ReportService;

/** Notifies adopters who forgot to send pet reports on time
 */
@Component
public class Notifier {

    private final TelegramBot telegramBot;
    private final ReportService reportService;

    public Notifier(TelegramBot telegramBot, ReportService reportService) {
        this.telegramBot = telegramBot;
        this.reportService = reportService;
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void run() {
        reportService.findAllOverdueReports()
                .forEach(pet -> {
                    SendMessage message = new SendMessage(pet.getAdopter().getChatId(), "У вас есть пропуски по отчётам. Пожалуйста, отправьте отчёт по животному за просроченный период.");
                    telegramBot.execute(message);
                });
    }
}
