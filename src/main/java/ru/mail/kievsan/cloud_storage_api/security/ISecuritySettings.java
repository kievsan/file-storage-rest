package ru.mail.kievsan.cloud_storage_api.security;

public interface ISecuritySettings {
    String JWT_HEADER_NAME = "auth-token";
    String BASE_URI = "/api/v1";
    String USER_URI = BASE_URI + "/user";
    String SIGN_UP_URI = USER_URI;
    String LOGIN_URI = BASE_URI + "/login";
    String LOGOUT_URI = BASE_URI + "/logout";
    String POST_FREE_ENTRY_POINTS = String.format("%s, %s, %s", SIGN_UP_URI, LOGIN_URI, LOGOUT_URI);
    String FILE_URI = BASE_URI + "/file";
    String FILE_LIST_URI = BASE_URI + "/list";
}
