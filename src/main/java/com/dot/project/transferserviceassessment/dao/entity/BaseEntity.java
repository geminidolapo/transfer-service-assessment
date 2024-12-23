package com.dot.project.transferserviceassessment.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(columnDefinition="tinyint(1) default 0")
    private boolean deleted = Boolean.FALSE;

    @PrePersist
    private void prePersist() {
        if (Objects.isNull(this.getCreatedAt())) {
            this.setCreatedAt(
                    LocalDateTime.now(ZoneId.of("Africa/Lagos"))
            );
        }
    }
}
