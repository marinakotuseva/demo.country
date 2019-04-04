package ru.iteranet.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.ResponseEntity;
import ru.iteranet.entity.Country;
import ru.iteranet.exceptions.NotSupportedClassExceptions;

import java.lang.reflect.Type;

public class CommonUtils {

    public static <T>T responseToObject(Class clazz, ResponseEntity<String> response) throws NotSupportedClassExceptions {
        if (clazz == Country.class){
            Type type = new TypeToken<Country>() {
            }.getType();
            T object = new Gson().fromJson(response.getBody(), type);
            return object;
        } else {
            throw new NotSupportedClassExceptions();
        }
    }
}
