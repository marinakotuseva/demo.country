package ru.iteranet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class IncorrectName extends RuntimeException {
    public IncorrectName(){
        super("Имя не заполнено");
    }
}
