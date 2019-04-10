package ru.iteranet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class RecordAlreadyExistsException extends RuntimeException {
    public RecordAlreadyExistsException(String name){
        super("Запись с именем " + name +" уже существует");
    }
}

