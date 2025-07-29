package com.licoflix.core.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class FilmRequest {
    private Long id;

    @NotNull(message = "Film age is required")
    private int age;

    @NotNull(message = "Film year is required")
    private int year;

    private Double imdb;

    private Integer oscars;

    private Integer baftaAwards;

    private Integer goldenGlobes;

    @NotNull(message = "Film title is required")
    private String title;

    @NotNull(message = "Film orignal title is required")
    private String originalTitle;

    @NotNull(message = "Film language is required")
    private String language;

    @NotNull(message = "Film cast is required")
    private String cast;

    @NotNull(message = "Film duration is required")
    private String duration;

    @NotNull(message = "Film description is required")
    private String description;

    @NotNull(message = "Film directors is required")
    private String directors;

    @NotNull(message = "Film producers is required")
    private String producers;

    @NotNull(message = "Film image is required")
    private MultipartFile image;

    @NotNull(message = "Film categories is required")
    private List<String> categories;

    @NotNull(message = "Film background is required")
    private MultipartFile background;

    private MultipartFile film;

    private MultipartFile subtitle;

    private MultipartFile subtitleEn;
}