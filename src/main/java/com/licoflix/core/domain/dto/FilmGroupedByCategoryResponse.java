package com.licoflix.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmGroupedByCategoryResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 916485750632560658L;

    private String category;
    private List<FilmResponse> films;

    @Override
    public String toString() {
        return "FilmGroupedByCategoryResponse = {" +
                " category: " + category +
                ", films: " + (films != null ? films.toString() : "[]") +
                '}';
    }
}
