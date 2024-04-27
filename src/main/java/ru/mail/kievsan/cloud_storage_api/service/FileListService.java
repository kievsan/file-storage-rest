package ru.mail.kievsan.cloud_storage_api.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.exception.FileListUserNotFoundException;
import ru.mail.kievsan.cloud_storage_api.exception.HttpStatusException;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListResponse;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.FileJPARepo;
import ru.mail.kievsan.cloud_storage_api.security.JWTUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FileListService {

    private FileJPARepo fileRepo;
    private JWTUserDetails jwtUserDetails;

    public List<FileListResponse> getFileList(String authToken, Integer limit) throws RuntimeException {
        log.info("  Start File list service:  limit = {},  token:  {}", limit, authToken);
        try {
            final User user = jwtUserDetails.loadUserByJWT(authToken);
            log.info("Success get all files. User {}", user.getUsername());

            var fileStream = fileRepo.findAllByUserOrderByFilename(user).stream();
            var limitStream = limit > 0 ? fileStream.limit(limit) : fileStream;

            return limitStream.map(file -> new FileListResponse(file.getFilename(), file.getSize()))
                    .collect(Collectors.toList());
        } catch (HttpStatusException ex) {
            String msg = String.format("Get file list error:  %s", ex);
//            log.info(msg);
            throw new FileListUserNotFoundException(msg);
        }
    }

}
