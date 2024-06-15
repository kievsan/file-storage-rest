package ru.mail.kievsan.cloud_storage_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListResponse;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.FileJPARepo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileListService {

    private final FileJPARepo fileRepo;

    public List<FileListResponse> getFileList(Integer limit, User user) throws RuntimeException {
        log.info("Start File list service, get file list with limit = {},  user:  {} ({}), {}",
                limit, user.getUsername(), user.getNickname(), user.getAuthorities());
        var fileStream = fileRepo.findAllByUserOrderByFilename(user).stream();
        var limitStream = limit > 0 ? fileStream.limit(limit) : fileStream;

        log.info("  Success: got file list. User {} ({})", user.getUsername(), user.getNickname());

        return limitStream.map(file -> new FileListResponse(file.getFilename(), file.getSize())).toList();  // .collect(Collectors.toList());
    }
}
