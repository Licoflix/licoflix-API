package com.licoflix.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
