package ru.mail.kievsan.cloud_storage_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;

import java.util.List;

@Repository
public interface FileJPARepo extends JpaRepository<File, Long> {

    @Modifying
    @Query("update File f set f.filename = :newName where f.filename = :filename and f.user = :user")
    void editFileNameByUser(@Param("user") User user, @Param("filename") String filename, @Param("newName") String newName);

    void deleteByUserAndFilename(User user, String filename);

    File findByUserAndFilename(User user, String filename);

    List<File> findAllByUserOrderByFilename(User user);
}
