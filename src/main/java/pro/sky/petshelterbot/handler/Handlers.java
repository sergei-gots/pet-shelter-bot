package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.util.Logged;



@Service
public class Handlers
    extends Logged
        implements Handler {
    final private Handler[] handlers;

    /**
     * @param volunteerDialogHandler to developer inside the constructor:
     *                               volunteerHandler must be placed at first place within
     *                               list of handlers: first of all we check
     *                               if the update we have to handle is sent by volunteer.
     */
    public Handlers(VolunteerDialogHandler volunteerDialogHandler,
                    BasicAdopterHandler basicAdopterHandler,
                    AdopterInputHandler adopterInputHandler,
                    AdopterDialogHandler adopterDialogHandler,
                    ShelterInfoHandler shelterInfoHandler,
                    DefaultHandler defaultHandler
                               ) {

        handlers = new Handler[]{
                //Important: volunteerHandler MUST BE at first place
                volunteerDialogHandler,
                //then - all the other,
                basicAdopterHandler,
                adopterInputHandler,
                shelterInfoHandler,
                adopterDialogHandler,
                defaultHandler
        };
    }

    @Override
    public boolean handle(Update update) {
        logger.debug("process(updates): processing update: {}", update);
        try {
            for (Handler handler : handlers) {
                if (handler.handle(update)) {
                    return true;
                }
            }
        } catch (Exception e) {
            handlers[handlers.length - 1].handle(update);
            logger.error("processMessage: exception was thrown in a handler", e);
        }
        return false;
    }
}
