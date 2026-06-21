package dev.jorge.projects.email.model;

import com.github.f4b6a3.uuid.UuidCreator;
import dev.jorge.projects.email.enums.StatusEmail;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_emails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Email implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private UUID emailId;
    private UUID userId;
    private String emailFrom;
    private String emailTo;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String text;
    private LocalDateTime sendDateEmail;
    private StatusEmail statusEmail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.emailId == null) {
            this.emailId = UuidCreator.getTimeOrderedEpoch();
        }
        this.sendDateEmail = LocalDateTime.now();
    }

}
