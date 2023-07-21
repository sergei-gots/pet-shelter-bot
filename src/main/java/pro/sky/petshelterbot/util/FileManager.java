package pro.sky.petshelterbot.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.configuration.TelegramBotConfiguration;
import pro.sky.petshelterbot.entity.Pet;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class FileManager {
    public static String REPORT_IMG_DIR = "reports";
    private final TelegramBotConfiguration telegramBotConfiguration;

    final protected Logger logger = LoggerFactory.getLogger(getClass());

    public FileManager(TelegramBotConfiguration telegramBotConfiguration) {
        this.telegramBotConfiguration = telegramBotConfiguration;
    }

    public Path getReportImgPath(Pet pet) throws IOException {
        Long petId = pet.getId();
        logger.trace("getReportImgPath(pet.Id={}", petId);

        Path path = validatePath(Path.of(telegramBotConfiguration.getImgPath()) );

        path = getPath(path, pet.getShelter().getId());
        path = getPath(path, REPORT_IMG_DIR);
        path = getPath(path, petId);
        return path;
    }

    private Path validatePath(Path path) throws IOException {

        if(Files.isDirectory(path)) {
            return path;
        }
        return Files.createDirectories(path);
    }

    @NotNull
    private Path getPath(Path path, Long childDirId) throws IOException {
        return getPath(path, childDirId.toString());
    }

    /**
     *
     * @param path parent directory path
     * @param dir child directory to be created if it does not exist.
     *            If it is not a directory then IOException will be thrown.
     * @return path build from path and dir
     * @throws IOException
     */
    @NotNull
    private Path getPath(Path path, String dir) throws IOException {

        logger.trace("getPath(path={}, dir={})", path, dir);

        path = Path.of(path + "/" + dir);

        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }

        if (!Files.isDirectory(path)) {
            logger.error("getPath(): filepath=\"{}\" is not a directory",
                    path, new IOException());
        }

        return path;
    }

    public void saveTelegramFile(String telegramFileId, Path localFilename) throws IOException {

        String telegramFileLink = String.format(
                "https://api.telegram.org/bot%s/getFile?file_id=%s",
                telegramBotConfiguration.telegramBot().getToken(),
                telegramFileId);
        URL urlTelegramFile = new URL(telegramFileLink);

        logger.trace("saveTelegramFile(): telegramFileLink={}", telegramFileLink);

        String urlTelegramFileLink = getJsonNode(urlTelegramFile).get("result").get("file_path").asText();
        logger.trace("saveTelegramFile(): urlTelegramFileLink file specific part ={}", urlTelegramFileLink);

        urlTelegramFileLink = String.format("https://api.telegram.org/file/bot%s/%s",
                telegramBotConfiguration.telegramBot().getToken(),
                urlTelegramFileLink);

        logger.trace("saveTelegramFile(): start copying file from Telegram to the local file system");
        Files.copy(
                new URL(urlTelegramFileLink).openStream(),
                localFilename,
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    public static JsonNode getJsonNode(URL url) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(url);
    }

}