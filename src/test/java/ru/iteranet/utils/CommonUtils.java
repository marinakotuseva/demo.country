package ru.iteranet.utils;

import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonUtils {

    public static <T> T responseAs(ResponseEntity<String> response, Class<T> entityClass) {
        return new Gson().fromJson(response.getBody(), entityClass);
    }

    public static <T> List<Object> responseAsList(ResponseEntity<String> response, Class<T[]> entityClassArray) {
        Object responseArray = responseAs(response, entityClassArray);
        return new ArrayList<>(Arrays.asList(responseArray));
    }
}
