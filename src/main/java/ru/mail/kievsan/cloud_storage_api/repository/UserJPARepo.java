package ru.mail.kievsan.cloud_storage_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;

import java.util.Optional;

@Repository
public interface UserJPARepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
