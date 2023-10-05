package pro.sky.petshelterbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.exceptions.HandlerException;
import pro.sky.petshelterbot.repository.*;
import pro.sky.petshelterbot.util.FileManager;


import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static pro.sky.petshelterbot.constants.PetShelterBotConstants.MessageKey.*;

/**
 * Operates chat between Adopter and Volunteer on the Adopter's side
 */
@Component
public class AdopterInputHandler extends AbstractHandler {

    private final ReportRepository reportRepository;
    private final PetRepository petRepository;

    private final FileManager fileManager;

    public AdopterInputHandler(TelegramBot telegramBot,
                               AdopterRepository adopterRepository,
                               VolunteerRepository volunteerRepository,
                               ShelterRepository shelterRepository,
                               UserMessageRepository userMessageRepository,
                               ButtonRepository buttonRepository,
                               DialogRepository dialogRepository,
                               ReportRepository reportRepository, PetRepository petRepository, FileManager fileManager) {
        super(telegramBot, adopterRepository, volunteerRepository, shelterRepository, userMessageRepository, buttonRepository, dialogRepository);
        this.reportRepository = reportRepository;
        this.petRepository = petRepository;
        this.fileManager = fileManager;
    }

    @Override
    public boolean handleCallbackQuery(Message message, String key) {

        Adopter adopter = getAdopter(message);
        Adopter.ChatState chatState = adopter.getChatState();

        logger.debug("handleCallBackQuery(): adopter.firstName={}, key={}, chatState={}",
                adopter.getFirstName(), key, chatState);

        switch(chatState) {
            case ADOPTER_IN_SHELTER_INFO_MENU:
                if(ENTER_REPORT.equals(key)) {
                    processEnterReport(message);
                    return true;
                }
                if(CONTINUE.equals(key)) {
                    showCurrentMenu(adopter);
                    return true;
                }
            case ADOPTER_IN_ADOPTION_INFO_MENU:
                if(ENTER_CONTACTS.equals(key)) {
                    processEnterContacts(message);
                    return true;
                }
            case ADOPTER_INPUTS_AND_READING_ADVICE_TO_IMPROVE_REPORTS:
                if(GOT_IT.equals(key)) {
                    deletePreviousMenu(adopter);
                    processEnterReport(message);
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message message) {

        Adopter adopter = getAdopter(message);
        Adopter.ChatState chatState = adopter.getChatState();

        logger.debug("handleMessage(): adopter.firstName={}, .chatState={}",
                adopter.getFirstName(), chatState);

        if (!adopter.isAdopterInInputState(chatState)) {
            return false;
        }

        switch (chatState) {
            case ADOPTER_INPUTS_PHONE_NUMBER:
                processInputPhoneNumber(message);
                return true;
            case ADOPTER_INPUTS_AND_READING_ADVICE_TO_IMPROVE_REPORTS:
                processEnterReport(message);
                return true;
            case ADOPTER_INPUTS_REPORT_DIET:
                processInputReportDiet(message);
                return true;
            case ADOPTER_INPUTS_REPORT_WELL_BEING:
                processInputReportWellBeing(message);
                return true;
            case ADOPTER_INPUTS_REPORT_BEHAVIOUR:
                processInputReportBehaviour(message);
                return true;
            case ADOPTER_INPUTS_REPORT_IMAGE:
                processInputReportImage(message);
                return true;
        }
        return false;
    }

    @Override
    public boolean handleImg(Update update) {

        Message message = update.message();
        Adopter adopter = getAdopter(message);
        ChatState chatState = adopter.getChatState();
        if (chatState != ChatState.ADOPTER_INPUTS_REPORT_IMAGE) {
            return handleByChatState(adopter);
        }

        Pet pet = getPet(adopter);
        if (pet == null) {
            logger.warn("handleImg(): pet == null => No pet on trial found for the adopter = {} ", adopter.getChatId());
            return false;
        }

        PhotoSize[] photos = update.message().photo();
        Document document = message.document();

        logger.debug("handleImg(): chatId={}, photo={}, document={}",
                message.chat().id(),
                Arrays.toString(photos),
                document);

        String extension = ".png";
        String imgFileId = null;
        if (photos != null) {
            imgFileId = photos[photos.length - 1].fileId();
        } else if (document != null) {
            imgFileId = document.fileId();
            String mimeType = document.mimeType();
            switch(mimeType) {
                case "image/png":
                    extension = ".png";
                    break;
                case "image/jpeg":
                    extension = ".jpg";
                    break;
                default:
                    logger.debug("handleImg(): not supported mimeType={}", mimeType);
                    sendUserMessage(adopter, NOT_SUPPORTED_IMAGE_FORMAT);
                    return true;
            }
        }

        if (imgFileId == null) {
            logger.trace("handleImg(): no image found in message={}", message);
            sendMessage(adopter.getChatId(), "На этом этапе мы ожидаем получить фотографию питомца-)" );
            return true;
        }


        DateTimeFormatter dateTimeFormatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
        LocalDateTime sentTime = LocalDateTime.now();
        LocalDate sent = sentTime.toLocalDate();
        String filename = "/" + pet.getId() + "-" + dateTimeFormatter.format(sentTime) + extension;

        logger.trace("handleImg(): local filename={}", filename);

        Report report = getEditedReport(pet);
        try {
            Path savePath = Path.of(fileManager.getReportImgPath(pet) + filename);
            logger.trace("handleImg(): local save path={}", savePath);
            fileManager.saveTelegramFile(imgFileId, savePath);
            report.setImgPath(savePath.toString());
            logger.trace("handleImg(): image saved");

        } catch (IOException e) {
            logger.error("handleImg(): IOException during execution of " +
                            "FileManager.saveTelegramFile(file_id={})",
                    pet.getId(), e);
            throw new HandlerException(getClass() + ".handleImage: IOException was thrown.", e);
        }

        report.setSent(sent);
        reportRepository.save(report);
        sendMessage(adopter.getChatId(), "Спасибо! Мы приняли ваш отчёт. Он будет проверен сотрудником шелтера. " +
                "Вы можете прислать нам ещё отчёт, если посчитаете необходимым.\n" + MENU);
        adopter.setChatState(ChatState.ADOPTER_IN_SHELTER_INFO_MENU);
        sendMenu(adopter, CONTINUE);
        logger.trace("handleImg(): report completed");
        return true;
    }

    private void processEnterContacts(Message message) {
        Adopter adopter = getAdopter(message);

        logger.debug("processEnterContacts(adopter={}, text=\"{}\")", adopter.getFirstName(), message.text());

        adopter.setChatState(ChatState.ADOPTER_INPUTS_PHONE_NUMBER);
        deletePreviousMenu(adopter);
        adopterRepository.save(adopter);

        sendMessage(adopter.getChatId(), "Пожалуйста, введите номер телефона для связи с вами (/menu для отмены)." +
                " Формат номера: 7-7XX-XXX-XX-XX:");

    }

    private void processInputPhoneNumber(Message message) {
        Adopter adopter = getAdopter(message);

        logger.debug("processInputPhoneNumber(adopter={}, text=\"{}\")", adopter.getFirstName(), message.text());
        String phoneNumber = message.text().replaceAll("\\D+", "");

        int length = phoneNumber.length();
        logger.debug("processInputPhoneNumber(): phone_number={}, length={}", phoneNumber, length);
        //Todo - store these constants in the db
        if (!(length == 11 && phoneNumber.startsWith("77"))) {
            sendMessage(message.chat().id(),
                    "Мы не смогли распознать телефонный номер. Пожалуйста, введите номер " +
                            "в формате 7-7XX-XXX-XX-XX:");
        } else {
            adopter.setPhoneNumber(phoneNumber);
            adopter.setChatState(ChatState.ADOPTER_IN_ADOPTION_INFO_MENU);
            adopterRepository.save(adopter);

            sendMessage(adopter.getChatId(),
                    "Ваш телефонный номер: "
                            + phoneNumber.replaceFirst(
                            "(\\d{1})(\\d{3})(\\d{3})(\\d{2})(\\d+)", "+$1-$2-$3-$4-$5")
                            + ". Спасибо:) \nДля продолжения нажмите " + MENU);

        }
    }

    private void processInputReportDiet(Message message) {
        Adopter adopter = getAdopter(message);

        logger.debug("processInputDiet(adopter={}, text=\"{}\")", adopter.getFirstName(), message.text());

        Pet pet = getPet(adopter);
        Report report = getEditedReport(pet);
        report.setDiet(message.text());
        reportRepository.save(report);

        sendMessage(adopter.getChatId(), "Теперь опишите самочувствие питомца:");
        adopter.setChatState(ChatState.ADOPTER_INPUTS_REPORT_WELL_BEING);
        adopterRepository.save(adopter);
    }

    private void processInputReportWellBeing(Message message) {
        Adopter adopter = getAdopter(message);
        logger.debug("processInputWellBeing(adopter={}, text=\"{}\")", adopter.getFirstName(), message.text());

        Pet pet = getPet(adopter);
        Report report = getEditedReport(pet);
        report.setWellBeing(message.text());
        reportRepository.save(report);

        sendMessage(adopter.getChatId(), "Расскажите, что изменилось в поведении животного за последний день:");
        adopter.setChatState(ChatState.ADOPTER_INPUTS_REPORT_BEHAVIOUR);
        adopterRepository.save(adopter);
    }

    private void processInputReportBehaviour(Message message) {
        Adopter adopter = getAdopter(message);

        logger.debug("processInputReportBehaviour(adopter={}, text=\"{}\")", adopter.getFirstName(), message.text());

        Pet pet = getPet(adopter);
        Report report = getEditedReport(pet);
        report.setBehaviour(message.text());
        reportRepository.save(report);

        sendMessage(adopter.getChatId(), "В завершение, пришлите нам, пожалуйста, сегодняшнюю фотографию питомца:");
        adopter.setChatState(ChatState.ADOPTER_INPUTS_REPORT_IMAGE);
        adopterRepository.save(adopter);
    }

    private void processInputReportImage(Message message) {
        Adopter adopter = getAdopter(message);

        logger.debug("processInputContacts(adopter={}, text=\"{}\")", adopter.getFirstName(), message.text());
    }


    @Nullable
    private Pet getPet(Adopter adopter) {
        return petRepository
                .findFirstByAdopterAndShelter(adopter, adopter.getShelter())
                .orElse(null);
    }

    private Report getEditedReport(Pet pet) {
        return reportRepository
                .findFirstByPetAndSentIsNull(pet)
                .orElse(new Report(pet));
    }

    private void processEnterReport(Message message) {
        Adopter adopter = getAdopter(message);

        logger.debug("processEnterReport(adopter={}, text=\"{}\")", adopter.getFirstName(), message.text());

        Pet pet = getPet(adopter);

        if (pet == null) {
            logger.trace("processEnterReport(pet=null)");

            sendUserMessage(adopter, YOU_DONT_HAVE_ANIMAL_ON_TRIAL);
            adopter.setChatState(ChatState.ADOPTER_IN_SHELTER_INFO_MENU);

        } else if (!checkForAdviceToRead(adopter, pet)) {
            logger.trace("processEnterReport(pet.name={})", pet.getName());

            sendMessage(adopter.getChatId(), "Пожалуйста, опишите рацион питания вашего питомца за последний день:");
            adopter.setChatState(ChatState.ADOPTER_INPUTS_REPORT_DIET);
            Report report = getEditedReport(pet);
            reportRepository.save(report);
        }
        adopterRepository.save(adopter);
    }

    private boolean checkForAdviceToRead(Adopter adopter, Pet pet) {
        if (adopter.getChatState() == ChatState.ADOPTER_INPUTS_AND_READING_ADVICE_TO_IMPROVE_REPORTS) {
            return false;
        }
        Report lastReport = reportRepository
                .findLastByPetAndCheckedIsTrueAndSentIsNotNullOrderBySentAsc(pet)
                .orElse(null);
        if (lastReport == null) {
            logger.trace("checkIfThereIsAdviceToRead(): last report is OK if it exists");

            return false;
        }
        logger.trace("checkIfThereIsAdviceToRead(): last report was NOT PERFECT");
        sendMessage(adopter.getChatId(), adopter.getFirstName() +
                getUserMessage(ADVICE_TO_IMPROVE_REPORTS));
        adopter.setChatState(ChatState.ADOPTER_INPUTS_AND_READING_ADVICE_TO_IMPROVE_REPORTS);
        sendMenu(adopter, GOT_IT);

        return true;
    }

}
