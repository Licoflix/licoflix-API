package com.licoflix.core.domain.repository;

import com.licoflix.core.domain.model.film.UserFilmList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFilmListRepository extends JpaRepository<UserFilmList, Long> {

    @Query(value = "select tuf from UserFilmList tuf where user = :id order by tuf.id desc")
    List<UserFilmList> findByUserId(Long id);

    @Modifying
    @Query(value = "delete from UserFilmList tuf where user = :user and film.id = :film")
    void removeByUserIdAndFilmId(Long user, Long film);

    @Modifying
    @Query(value = "delete from UserFilmList tuf where film.id = :id")
    void deleteByFilmId(Long id);
}