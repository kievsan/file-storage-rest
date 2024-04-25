package ru.mail.kievsan.cloud_storage_api.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListOkResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListResponse;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.security.JWTService;
import ru.mail.kievsan.cloud_storage_api.repository.FileJPARepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FileListService {

    private FileJPARepo fileRepo;
    private JWTService jwtService;

    public List<FileListResponse> getFileList(String authToken, Integer limit) {
        List<FileListResponse> fileListResponses = new ArrayList<>();
        try {
            final User user = jwtService.getUserByAuthToken(authToken);
            log.info("Success get all files. User {}", user.getUsername());

            var fileStream = fileRepo.findAllByUserOrderByFilename(user).stream();
            var limitStream = limit > 0 ? fileStream.limit(limit) : fileStream;

            fileListResponses = limitStream.map(file -> new FileListOkResponse(file.getFilename(), file.getSize()))
                    .collect(Collectors.toList());
        } catch (RuntimeException ex) {
            log.info("Get all files:   {}", ex.getMessage(), ex.getCause());
        }
        return fileListResponses;
    }

}
