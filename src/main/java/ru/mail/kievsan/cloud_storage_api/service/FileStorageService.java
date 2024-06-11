package ru.mail.kievsan.cloud_storage_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.mail.kievsan.cloud_storage_api.exception.AdviceException;
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

    static final String logErrTitle = "[FILE service error]";

    private final FileJPARepo fileRepo;

    public void uploadFile(String filename, MultipartFile file, User user) throws InputDataException {
        String errMsg = "User %s: error input data, upload file '%s'.".formatted(user.getUsername(), filename);
        fileRepo.save(new File(
                filename,
                LocalDateTime.now(),
                file == null ? 0 : file.getSize(),
                //file == null ? "Example".getBytes() : file.getBytes(), // SUCCESS!
                getFileContent(file, errMsg),
                user)
        );
        log.info("[User {}] Success upload file '{}'. ", user.getUsername(), filename);
    }

    public File downloadFile(String filename, User user) throws InputDataException {
        String errMsg = String.format("User %s: download file error, file not found '%s'", user.getUsername(), filename);
        File file = throwExceptionWhenFileFound(false, filename, user,
                new InputDataException(errMsg, null, "FILE", "'/file'", "'downloadFile service'")
        );
        log.info("[User {}] Success download file '{}'", user.getUsername(), filename);
        return file;
    }

    @Transactional
    public void deleteFile(String filename, User user) throws InputDataException, InternalServerException {
        String errMsg = String.format("User %s: delete file error, file not found '%s'", user.getUsername(), filename);
        throwExceptionWhenFileFound(false, filename, user,
                new InputDataException(errMsg, null, "FILE", "'/file'", "'deleteFile service'")
        );

        fileRepo.deleteByUserAndFilename(user, filename);

        errMsg = String.format("User %s: server error delete file '%s'", user.getUsername(), filename);
        throwExceptionWhenFileFound(true, filename, user,
                new InternalServerException(errMsg, null, "FILE", "'/file'", "'deleteFile service'")
        );
        log.info("[User {}] Success delete file '{}'", user.getUsername(), filename);
    }

    @Transactional
    public void editFileName(String filename, String newFileName, User user) throws InputDataException, InternalServerException {
        if (newFileName == null || newFileName.isBlank()) {
            String errMsg = String.format("User %s: new file name is null or blank, storage file '%s'",
                    user.getUsername(), filename);
            log.error("{} {}", logErrTitle, errMsg);
            throw new InputDataException(errMsg, null, "FILE", "'/file'", "'editFileName service'");
        }
        newFileName = newFileName.trim();
        if (!Objects.equals(filename, newFileName)) {
            String errMsg = String.format("User %s: file name not found, then has not been edited '%s' -> '%s'",
                    user.getUsername(), filename, newFileName);
            throwExceptionWhenFileFound(false, filename, user,
                    new InputDataException(errMsg, null, "FILE", "'/file'", "'editFileName service'")
            );

            fileRepo.editFileNameByUser(user, filename, newFileName);

            errMsg = String.format("User %s: server error edit file name '%s' -> '%s', the file name remains the same...",
                    user.getUsername(), filename, newFileName);
            throwExceptionWhenFileFound(true, filename, user,
                    new InternalServerException(errMsg, null, "FILE", "'/file'", "'editFileName service'"));
        }
        log.info("[User {}] Success edit file name '{}' -> '{}'", user.getUsername(), filename, newFileName);
    }

//    private File throwExceptionIfNotFoundFile(boolean exists, String filename, User user, AdviceException e) throws AdviceException {
//        File file = fileRepo.findByUserAndFilename(user, filename);
//        if (Objects.equals(file != null, exists)) {
//            log.error("{} {}", logErrTitle, e.getMessage());
//            throw e;
//        }
//        return file;
//    }

    private File throwExceptionWhenFileFound(boolean hasToThrow, String filename, User user, AdviceException e) throws AdviceException {
        File file = fileRepo.findByUserAndFilename(user, filename);
        boolean found = file != null;
        if (Objects.equals(found, hasToThrow)) {
            log.error("{} {}", logErrTitle, e.getMessage());
            throw e;
        }
        return file;
    }

    private byte[] getFileContent(MultipartFile file, String errMsg) throws InputDataException {
        try {
            return Objects.requireNonNull(file).getBytes();
        } catch (NullPointerException | IOException e) {
            log.error("{} {}  Cause: {}. {}", logErrTitle, errMsg, e.getClass(), e.getMessage() == null ? "" : e.getMessage());
            throw new InputDataException(errMsg, null, "FILE", "'/file'", "'uploadFile service'");
        }
    }
}
