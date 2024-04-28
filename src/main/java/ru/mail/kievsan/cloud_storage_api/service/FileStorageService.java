package ru.mail.kievsan.cloud_storage_api.service;

import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mail.kievsan.cloud_storage_api.exception.InputDataException;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.FileJPARepo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileJPARepo fileRepo;

    private final String header = "[FILE service error]";

    public void uploadFile(String filename, MultipartFile file, User user) {
        try {
            fileRepo.save(new File(filename, LocalDateTime.now(),
                    file == null ? 0 : file.getSize(),
                    Objects.requireNonNull(file).getBytes(),
                    //file == null ? Decoders.BASE64.decode("FileExample.") : file.getBytes(), // SUCCESS!
                    user));
            log.info("[User {}] Success upload file '{}'. ", user.getUsername(), filename);
        } catch (IOException | NullPointerException e) {
            String msg = String.format("User %s: error input data, upload file '%s'", user.getUsername(), filename);
            log.error("{} {}", header, msg);
            throw new InputDataException(msg);
        }
    }

    public void editFileName(String filename, String newFileName, User user) {
        fileRepo.editFileNameByUser(user, filename, newFileName);
        final File file = fileRepo.findByUserAndFilename(user, filename);
        if (file != null) {
            String msg = String.format("User %s: error input data, filename had no edited '%s' -> '%s'",
                    user.getUsername(), filename, newFileName);
            log.error("{} {}", header, msg);
            throw new InputDataException(msg);
        }
        log.info("[User {}] Success edit file name '{}' -> '{}'", user.getUsername(), filename, newFileName);
    }

}
