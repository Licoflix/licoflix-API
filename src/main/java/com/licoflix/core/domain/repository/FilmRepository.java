package com.licoflix.core.domain.repository;

import com.licoflix.core.domain.model.film.Category;
import com.licoflix.core.domain.model.film.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    Page<Film> findAll(Specification<Film> filmSpecification, Pageable pageable);

    @Query(value = "SELECT f.title, STRING_AGG(c.name, ', ') AS categories, f.year, f.duration, f.directors, f.producers, f.film_cast " +
            "FROM tb_film f " +
            "JOIN tb_film_category fc ON f.id = fc.film_id " +
            "JOIN tb_category c ON fc.category_id = c.id " +
            "GROUP BY f.id " +
            "ORDER BY f.title", nativeQuery = true)
    List<String[]> findAllForXLS();

    Optional<Film> getByTitleAndYear(String title, int year);

    @Query("SELECT c.name, f FROM Film f JOIN f.categories c")
    List<Object[]> findFilmsWithCategories();

    @Query("SELECT f FROM Film f where f.title = :title")
    Optional<Film> findByTitle(String title);
}