package com.abel.videochattingsystem.Config;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

    @Override
    public boolean test(String string) {
        return true;
    }
}
