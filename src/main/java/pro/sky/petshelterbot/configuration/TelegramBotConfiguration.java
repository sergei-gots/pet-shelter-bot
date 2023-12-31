package pro.sky.petshelterbot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${db.img.path}")
    private String imgPath;

    @Value("${img.upload.path}")
    private String imgUploadPath;


    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getImgUploadPath() {
        return imgUploadPath;
    }

}
