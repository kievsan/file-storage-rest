package ru.mail.kievsan.cloud_storage_api.util;

import java.util.List;
import java.util.function.Function;

public interface ILogUtils {
    String prefix = ">" + "-".repeat(15);
    Function<Class<?>, String> className = clazz-> List.of(clazz.getName().split("\\.")).getLast();
    Function<Class<?>, String> className_ = clazz-> String.format("'%s'", className.apply(clazz));
}
