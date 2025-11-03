package com.userservice.expmbff.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IncorrectDataException extends Exception {

    private String message;

    public IncorrectDataException(String message) {
        this.message = message;
    }
}
