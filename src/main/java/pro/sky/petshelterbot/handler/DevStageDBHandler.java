package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.service.PetService;
import pro.sky.petshelterbot.repository.ShelterRepository;

/**
 * Development stage class for debugging/testing
 * functionality being implementing within Cats/CatAdopters workflow
 * handles commands starting with '/dev-:
 *     - '/dev-dev-create-cat' adds to the DB a new Cat;
 *     - '/dev-create-shelters' creates shelters in the DB if they have not been created before.
 */
@Component
public class DevStageDBHandler extends AbstractHandler{
    private final PetService petService;
    private final ShelterRepository shelterRepository;


    public DevStageDBHandler(TelegramBot telegramBot, PetService petRepository, ShelterRepository shelterRepository) {
        super(telegramBot);
        this.petService = petRepository;
        this.shelterRepository = shelterRepository;
    }

    @Override
    public boolean handle(Message message) {

        String text = message.text();
        if(!text.startsWith("/dev-")) {
            return false;
        }

        Chat chat = message.chat();
        logger.info("- Received {} command from user {}", text, chat.firstName());

        if(text.equals("/dev-create-cat")) {
            return createCat(chat);
        }
        if(text.equals("/dev-create-shelters")) {
            createSheltersIfNotExist(chat);
            return true;
        }

        return true;
    }

    private boolean createCat(Chat chat) {
        String catInfo = petService.createCat("Муська")
                .toString();
        logger.info("- Test-cat={} was added to db", catInfo);
        sendMessage(chat.id(), "Added " + catInfo);
        return true;
    }

    private void createSheltersIfNotExist(Chat chat) {
        if(shelterRepository.findAll().size()>=2) {
            logger.info("- Shelters already exist in db");
            sendMessage(chat.id(),  "Shelters are already listed in DB");
            return;
        }
        sendMessage(chat.id(),"Shelters successfully created in DB; see log-INFO for details");
    }


}
