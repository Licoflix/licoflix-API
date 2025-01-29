package com.licoflix.core.domain.model.audit;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class AuditFields implements Serializable {
    @Column(name = "created_by")
    private Long createdBy = -1L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_in")
    private LocalDateTime createdIn = LocalDateTime.now();

    @Column(name = "changed_by")
    private Long changedBy;

    @Column(name = "changed_in")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime changedIn;
}