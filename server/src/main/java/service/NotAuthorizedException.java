package service;

public class NotAuthorizedException extends Exception{
    public NotAuthorizedException(String message){super(message); }
    public NotAuthorizedException(String message, Throwable ex) {super(message, ex); }

}
