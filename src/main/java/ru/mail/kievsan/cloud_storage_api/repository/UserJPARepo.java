package ru.mail.kievsan.cloud_storage_api.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;

import java.util.Optional;

@Repository
public interface UserJPARepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(@Email String email);

    boolean existsByEmail(@Email String email);
    boolean existsById(@Positive Long id);

    @Modifying
    @Query("update User u set u.email = :newEmail, u.password = :newPassword where u = :user")
    void updateUserByEmailAndPassword(@Email String newEmail, String newPassword, User user);

}
