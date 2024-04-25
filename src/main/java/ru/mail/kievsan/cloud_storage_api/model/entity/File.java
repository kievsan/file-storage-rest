package ru.mail.kievsan.cloud_storage_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "storage", name = "files")
@Entity
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String filename;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Long size;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    @ManyToOne
    private User user;

    public File(String filename, LocalDateTime date, Long size, byte[] content, User user) {
        this.filename = filename;
        this.date = date;
        this.size = size;
        this.content = content;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(getId(), file.getId()) &&
                Objects.equals(getFilename(), file.getFilename()) &&
                Objects.equals(getDate(), file.getDate()) &&
                Objects.equals(getSize(), file.getSize()) &&
                Objects.deepEquals(getContent(), file.getContent()) &&
                Objects.equals(getUser(), file.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFilename(), getDate(), getSize(), Arrays.hashCode(getContent()), getUser());
    }
}
