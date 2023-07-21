package pro.sky.petshelterbot.exceptions;

public class HandlerException extends RuntimeException{
    public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
