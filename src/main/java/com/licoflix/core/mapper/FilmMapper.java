package com.licoflix.core.mapper;

import com.licoflix.core.domain.dto.FilmRequest;
import com.licoflix.core.domain.dto.FilmResponse;
import com.licoflix.core.domain.dto.FilmWatchingListResponse;
import com.licoflix.core.domain.model.film.Category;
import com.licoflix.core.domain.model.film.Film;
import com.licoflix.core.domain.model.film.FilmWatchingList;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
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
                .saga(film.getSaga())
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
                .goldenGlobes(film.getGoldenGlobes())
                .originalTitle(film.getOriginalTitle())
                .image(film.getImage() != null ? Base64.getEncoder().encodeToString(film.getImage()) : "")
                .background(film.getBackground() != null ? Base64.getEncoder().encodeToString(film.getBackground()) : "")
                .categories(new ArrayList<>(film.getCategories().stream().map(Category::getName).sorted()
                        .collect(Collectors.toCollection(LinkedHashSet::new))))
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
                .saga(filmRequest.getSaga())
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
                .goldenGlobes(filmRequest.getGoldenGlobes())
                .originalTitle(filmRequest.getOriginalTitle())
                .build();

        film.setCreatedBy(id);
        return film;
    }

    public static void setChangedInfo(Long id, Film film, Long user) {
        film.setId(id);
        film.setCreatedBy(user);
        film.setChangedIn(LocalDateTime.now());
    }

    public static List<FilmWatchingListResponse> watchingListToDTO(List<FilmWatchingList> watching) {
        List<FilmWatchingListResponse> responses = new ArrayList<>();

        watching.forEach(entity -> responses
                .add(FilmWatchingListResponse
                        .builder()
                        .user(entity.getUser())
                        .current(entity.getCurrent())
                        .duration(entity.getDuration())
                        .film(entityToDTO(entity.getFilm()))
                        .build()));

        return responses;
    }
}
