package com.licoflix.core.domain.repository;

import com.licoflix.core.domain.model.film.FilmWatchingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmWatchingRepository extends JpaRepository<FilmWatchingList, Long> {

    @Query(value = "select f from FilmWatchingList f where f.film.id = :film and f.user = :user")
    FilmWatchingList getByFilmAndUser(Long user, Long film);

    @Query(value = "select f from FilmWatchingList f where f.user = :id")
    List<FilmWatchingList> findByUser(Long id);

    @Query("select f from FilmWatchingList f where f.film.id = :filmId and f.user = :userId order by f.id desc")
    List<FilmWatchingList> findByFilmIdAndUserIdOrderByIdDesc(Long filmId, Long userId);
}
