package com.mock2.shopping_app.exception;

public class UsernameExistedException extends RuntimeException {

    public UsernameExistedException() {
    }

    public UsernameExistedException(String message) {
        super(message);
    }
}
