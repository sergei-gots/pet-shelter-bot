package pro.sky.petshelterbot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//ToDo : remove the next row    
//@PropertySource("classpath:/application-dev.properties") //мы задаём кофигурацию либо через Running Configuration -> Edit, либо через параметр командной строки java
public class TelegramBotConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

}
