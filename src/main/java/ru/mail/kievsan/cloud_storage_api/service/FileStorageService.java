package ru.mail.kievsan.cloud_storage_api.service;

import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.mail.kievsan.cloud_storage_api.exception.InputDataException;
import ru.mail.kievsan.cloud_storage_api.exception.InternalServerException;
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
            throw new InputDataException(msg, null, null, null, "'uploadFile service'");
        }
    }

    @Transactional
    public void editFileName(String filename, String newFileName, User user) {
        if (newFileName == null || newFileName.isEmpty()) {
            String msg = String.format("User %s: new file name is null or empty, storage file '%s'",
                    user.getUsername(), filename);
            log.error("{} {}", header, msg);
            throw new InputDataException(msg, null, null, null, "'editFileName service'");
        }
        newFileName = newFileName.trim();
        if (!Objects.equals(filename, newFileName)) {
            String errMsg = String.format("User %s: file not found, filename had no edited '%s' -> '%s'",
                    user.getUsername(), filename, newFileName);
            checkForTheFile(false, filename, user, errMsg,
                    new InputDataException(errMsg, null, null, null, "'editFileName service'")
            );
            fileRepo.editFileNameByUser(user, filename, newFileName);

            errMsg = String.format("User %s: server error edit filename '%s' -> '%s'",
                    user.getUsername(), filename, newFileName);
            checkForTheFile(true, filename, user, errMsg,
                    new InternalServerException(errMsg, null, "FILE", "'/file'", "'editFileName service'"));
        }
        log.info("[User {}] Success edit file name '{}' -> '{}'", user.getUsername(), filename, newFileName);
    }

    @Transactional
    public void deleteFile(String filename, User user) {
        String errMsg = String.format("User %s: delete file error, file not found '%s'", user.getUsername(), filename);
        checkForTheFile(false, filename, user, errMsg,
                new InputDataException(errMsg, null, null, null, "'deleteFile service'")
        );
        fileRepo.deleteByUserAndFilename(user, filename);

        errMsg = String.format("User %s: server error delete file '%s'", user.getUsername(), filename);
        checkForTheFile(true, filename, user, errMsg,
                new InternalServerException(errMsg, null, "FILE", "'/file'", "'deleteFile service'")
        );
        log.info("[User {}] Success delete file '{}'", user.getUsername(), filename);
    }

    public File downloadFile(String filename, User user) {
        String errMsg = String.format("User %s: download file error, file not found '%s'", user.getUsername(), filename);
        File file = checkForTheFile(false, filename, user, errMsg,
                new InputDataException(errMsg, null, null, null, "'downloadFile service'")
        );
        log.info("[User {}] Success download file '{}'", user.getUsername(), filename);
        return file;
    }

    public File checkForTheFile(boolean exists, String filename, User user, String errMsg, RuntimeException exception) {
        File file = fileRepo.findByUserAndFilename(user, filename);
        if (file != null && exists) {
            log.error("{} {}", header, errMsg);
            throw exception;
        }
        return file;
    }

}
