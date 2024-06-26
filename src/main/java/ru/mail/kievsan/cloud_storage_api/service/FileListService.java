package ru.mail.kievsan.cloud_storage_api.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.exception.UserNotFoundException;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListResponse;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.FileJPARepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileListService {

    private final FileJPARepo fileRepo;

    public List<FileListResponse> getFileList(Integer limit, User user) throws RuntimeException {
        log.info("  Start File list service, get file list with limit = {},  user:  {} ({}), {}",
                limit, user.getUsername(), user.getNickname(), user.getAuthorities());
        try {
            var fileStream = fileRepo.findAllByUserOrderByFilename(user).stream();
            var limitStream = limit > 0 ? fileStream.limit(limit) : fileStream;

            log.info("Success: got file list. User {} ({})", user.getUsername(), user.getNickname());

            return limitStream.map(file -> new FileListResponse(file.getFilename(), file.getSize()))
                    .collect(Collectors.toList());
        } catch (RuntimeException ex) {
            String msg = String.format("Get file list error:  %s", ex);
            throw new UserNotFoundException(msg, HttpStatus.NOT_FOUND, "FILE LIST", "'/list'", "'getFileList service'");
        }
    }
}
