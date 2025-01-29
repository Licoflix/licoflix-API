package com.licoflix.core.domain.repository;

import com.licoflix.core.domain.model.film.FilmCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmCategoryRepository extends JpaRepository<FilmCategory, Long> {
    Page<FilmCategory> findAll(Specification<FilmCategory> filmCategorySpecification, Pageable pageable);


    @Modifying
    @Query(value = "delete from FilmCategory tuf where film.id = :id")
    void deleteByFilmId(Long id);
}
