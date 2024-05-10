package ru.mail.kievsan.cloud_storage_api.util;

import java.util.List;
import java.util.function.Function;

public class LogUtils {

    public static final String prefix = ">" + "-".repeat(15);
    public static final Function<Class<?>, String> className = clazz-> List.of(clazz.getName().split("\\.")).getLast();
    public static final Function<Class<?>, String> className_ = clazz-> String.format("'%s'", className.apply(clazz));

}
