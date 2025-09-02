package com.licoflix.core.domain.repository;

import com.licoflix.core.domain.model.film.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT c.name, f FROM Film f JOIN f.categories c")
    Page<Object[]> findFilmsWithCategories(Pageable pageable);

    @Query("SELECT c.name, f FROM Film f JOIN f.categories c WHERE c.name = :category")
    Page<Object[]> findFilmsWithCategoriesByCategory(String category, Pageable pageable);

    @Query("SELECT f FROM Film f where f.title = :title")
    Optional<Film> findByTitle(String title);

    @Query("SELECT f.saga, f FROM Film f WHERE f.saga IS NOT NULL ORDER BY f.year DESC, f.id DESC")
    List<Object[]> findFilmsWithSaga();
}