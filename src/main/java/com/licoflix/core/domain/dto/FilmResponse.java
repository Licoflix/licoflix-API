package com.licoflix.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class FilmResponse {
    private Long id;
    private int age;
    private int year;
    private Double imdb;
    private String cast;
    private String title;
    private String image;
    private Integer oscars;
    private String language;
    private String duration;
    private String producers;
    private String directors;
    private String createdIn;
    private String changedIn;
    private String background;
    private String description;
    private Integer baftaAwards;
    private List<String> categories;

    @Override
    public String toString() {
        return " FilmResponse = {" + " title: " + title + '}';
    }
}
