package service;
/**
 * Indicates that the username requested during registration
 * has already been taken, which caused registration to fail
 */
public class AlreadyTakenException extends Exception {
    public AlreadyTakenException(String message){super(message); }
    public AlreadyTakenException(String message, Throwable ex) {super(message, ex); }

}
