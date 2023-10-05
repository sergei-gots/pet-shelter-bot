package pro.sky.petshelterbot.exceptions;

import org.jetbrains.annotations.NotNull;

public class ShelterException extends RuntimeException {

    public ShelterException(String message) {
        super(message);
    }

    public static ShelterException of(Class type,
                                      @NotNull Long id) {
        return new ShelterException(
                "The " + type.getName() +
                    " with id=" + id +
                    " is not listed in the db.");

    }
}
