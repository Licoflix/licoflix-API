package com.licoflix.core.domain.model.film;

import com.licoflix.core.domain.model.audit.AuditFields;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_film_watching_list", indexes = {@Index(name = "index_tb_watching_film", columnList = "id, film_id, user_id")})
public class FilmWatchingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @Column(name = "user_id", nullable = false)
    private Long user;

    @Column(name = "duration", nullable = false)
    private String duration;

    @Column(name = "current", nullable = false)
    private String current;
}
