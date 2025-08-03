package com.licoflix.core.service.film;

import com.licoflix.core.domain.dto.*;
import com.licoflix.util.response.DataListResponse;
import com.licoflix.util.response.DataResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface IFilmService {
    DataListResponse<FilmResponse> list(String search, String orderBy, String direction, Integer page, Integer pageSize);

    DataResponse<FilmResponse> save(FilmRequest filmRequestDTO, String authorization, String timezone) throws IOException, GeneralSecurityException;

    DataResponse<FilmResponse> get(Long id);

    DataListResponse<FilmGroupedByCategoryResponse> listByCategories(Integer page, Integer pageSize, String category);

    @Transactional
    DataListResponse<FilmGroupedByCategoryResponse> listBySaga();

    DataListResponse<CategoryResponse> listCategories();

    @Transactional
    void delete(Long id) throws Exception;

    DataResponse<FilmResponse> addFilmInList(Long id, String authorization, String timezone) throws Exception;

    DataListResponse<FilmResponse> geFilmUserList(String authorization, String timezone);

    @Transactional
    void removeFilmFromList(Long id, String authorization, String timezone) throws Exception;

    @Transactional
    void addToContinueWatchList(String title, String current, String duration, String authorization, String timezone) throws Exception;

    @Transactional
    void removeFromContinueWatchList(String title, String authorization, String timezone) throws Exception;

    @Transactional
    DataListResponse<FilmWatchingListResponse> listWatchingFilmList(String authorization, String timezone) throws Exception;
}
