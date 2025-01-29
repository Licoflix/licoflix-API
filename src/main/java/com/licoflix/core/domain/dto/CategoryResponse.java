package com.licoflix.core.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;

    @Override
    public String toString() {
        return " FilmResponse = {" +
                " id: " + id +
                ", name: " + name +
                '}';
    }
}
