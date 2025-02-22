package my.notes.notesApp.biz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Note {

    @Id
    @GeneratedValue
    private Long id;

    private String content;

    private String title;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Customer creator;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

