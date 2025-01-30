package com.licoflix.core.domain.model.film;

import com.licoflix.core.domain.model.audit.AuditFields;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "tb_film", indexes = {@Index(name = "index_tb_film", columnList = "id, title")})
public class Film extends AuditFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "oscars", nullable = false)
    private Integer oscars;

    @Column(name = "baftaAwards", nullable = false)
    private Integer baftaAwards;

    @Column(name = "imdb", nullable = false)
    private Double imdb;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "duration", nullable = false)
    private String duration;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;

    @Lob
    @Column(name = "background", nullable = false)
    private byte[] background;

    @Column(name = "directors", nullable = false, columnDefinition= "varchar")
    private String directors;

    @Column(name = "producers", nullable = false, columnDefinition= "varchar")
    private String producers;

    @Column(name = "film_cast", nullable = false, columnDefinition= "varchar")
    private String cast;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_film_category",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}
