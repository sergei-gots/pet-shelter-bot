package pro.sky.petshelterbot.util;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.configuration.TelegramBotConfiguration;
import pro.sky.petshelterbot.entity.Pet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileManager {
    private final TelegramBotConfiguration telegramBotConfiguration;

    final protected Logger logger = LoggerFactory.getLogger(getClass());

    public FileManager(TelegramBotConfiguration telegramBotConfigureation) {
        this.telegramBotConfiguration = telegramBotConfigureation;
    }

    public Path getReportPhotosPath(Pet pet) throws IOException {
        Path path = Path.of(telegramBotConfiguration.getPhotosDir());
        if (!Files.isDirectory(path)) {
            logger.error("getReportPhostosDir(): db.photos.dir=\"{}\" does not exist", path, new IOException());
            throw (new IOException("db.photos.dir does not exist"));
        }

        path = getPath(path, pet.getShelter().getId());
        path = getPath(path, pet.getId());
        return path;
    }

    @NotNull
    private Path getPath(Path path, Long childDirId) throws IOException {
        return getPath(path, childDirId.toString());
    }
    @NotNull
    private Path getPath(Path path, String dir) throws IOException {

        logger.trace("getPath(path={}, dir={})", path, dir);

        path = Path.of(path + "/" + dir);

        if (!Files.exists(path)) {
            return Files.createDirectory(path);
        }

        if (!Files.isDirectory(path)) {
            logger.error("getPath(): filepath=\"{}\" is not a directory", path, new IOException());
        }
        return path;
    }
}