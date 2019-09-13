package com.newcache.server.exception;

public class BadCommandException extends Exception {

    public BadCommandException(String errorMessage) {
        super(errorMessage);
    }
}
