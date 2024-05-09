package ru.mail.kievsan.cloud_storage_api.security;

public interface SecuritySettings {
    String JWT_HEADER_NAME = "auth-token";
    String FREE_ENTRY_POINTS = "/api/v1/user/reg, /api/v1/login, /api/v1/logout";
}
