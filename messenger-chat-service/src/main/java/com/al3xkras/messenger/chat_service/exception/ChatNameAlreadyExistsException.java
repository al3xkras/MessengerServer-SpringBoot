package com.al3xkras.messenger.chat_service.exception;

public class ChatNameAlreadyExistsException extends RuntimeException {
    public ChatNameAlreadyExistsException() {
    }

    public ChatNameAlreadyExistsException(String message) {
        super("Chat name \""+message+"\" already exists");
    }

    public ChatNameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatNameAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}