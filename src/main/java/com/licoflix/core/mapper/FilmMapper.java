package com.licoflix.core.mapper;

import com.licoflix.core.domain.dto.FilmGroupedByCategoryResponse;
import com.licoflix.core.domain.dto.FilmRequest;
import com.licoflix.core.domain.dto.FilmResponse;
import com.licoflix.core.domain.model.film.Category;
import com.licoflix.core.domain.model.film.Film;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FilmMapper {
    private FilmMapper() {
    }

    public static FilmResponse entityToDTO(Film film) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String createdInFormatted = film.getCreatedIn().format(formatter);
        String changedInFormatted = film.getChangedIn() != null ? film.getChangedIn().format(formatter) : "";

        return FilmResponse
                .builder()
                .id(film.getId())
                .age(film.getAge())
                .cast(film.getCast())
                .imdb(film.getImdb())
                .year(film.getYear())
                .title(film.getTitle())
                .oscars(film.getOscars())
                .language(film.getLanguage())
                .duration(film.getDuration())
                .createdIn(createdInFormatted)
                .changedIn(changedInFormatted)
                .producers(film.getProducers())
                .directors(film.getDirectors())
                .description(film.getDescription())
                .baftaAwards(film.getBaftaAwards())
                .image(film.getImage() != null ? Base64.getEncoder().encodeToString(film.getImage()) : "")
                .background(film.getBackground() != null ? Base64.getEncoder().encodeToString(film.getBackground()) : "")
                .categories(new ArrayList<>(film.getCategories().stream().map(Category::getName).collect(Collectors.toSet())))
                .build();
    }

    public static List<FilmResponse> entityToDTOList(List<Film> films) {
        List<FilmResponse> list = new ArrayList<>();
        films.forEach(film -> list.add(entityToDTO(film)));
        return list;
    }

    public static Film requestToEntity(FilmRequest filmRequest, MultipartFile imageFile, MultipartFile backgroundFile, Long id) throws IOException {
        byte[] imageBytes = imageFile != null ? imageFile.getBytes() : null;
        byte[] backgroundBytes = backgroundFile != null ? backgroundFile.getBytes() : null;

        Film film = Film.builder()
                .image(imageBytes)
                .age(filmRequest.getAge())
                .cast(filmRequest.getCast())
                .imdb(filmRequest.getImdb())
                .year(filmRequest.getYear())
                .background(backgroundBytes)
                .categories(new ArrayList<>())
                .title(filmRequest.getTitle())
                .oscars(filmRequest.getOscars())
                .language(filmRequest.getLanguage())
                .duration(filmRequest.getDuration())
                .directors(filmRequest.getDirectors())
                .producers(filmRequest.getProducers())
                .baftaAwards(filmRequest.getBaftaAwards())
                .description(filmRequest.getDescription())
                .build();

        film.setCreatedBy(id);
        return film;
    }

    public static void setChangedInfo(Long id, Film film, Long user) {
        film.setId(id);
        film.setCreatedBy(user);
        film.setChangedIn(LocalDateTime.now());
    }

    public static List<FilmGroupedByCategoryResponse> filmGroupedByCategoryListFromObjectList(List<Object[]> filmsGroupedData) {
        Map<String, List<FilmResponse>> groupedFilmsMap = new HashMap<>();
        for (Object[] row : filmsGroupedData) {
            String categoryName = (String) row[0];
            Film film = (Film) row[1];

            groupedFilmsMap
                    .computeIfAbsent(categoryName, k -> new ArrayList<>())
                    .add(FilmMapper.entityToDTO(film));
        }

        return groupedFilmsMap.entrySet().stream()
                .map(entry -> {
                    FilmGroupedByCategoryResponse groupedResponse = new FilmGroupedByCategoryResponse();
                    groupedResponse.setCategory(entry.getKey());
                    groupedResponse.setFilms(entry.getValue());
                    return groupedResponse;
                })
                .toList();
    }
}
