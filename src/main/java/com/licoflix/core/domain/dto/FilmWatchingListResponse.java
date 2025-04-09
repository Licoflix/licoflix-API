package com.licoflix.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FilmWatchingListResponse {
    private Long user;
    private String current;
    private String duration;
    private FilmResponse film;
}
