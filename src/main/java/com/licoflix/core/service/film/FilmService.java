package com.licoflix.core.service.film;

import com.licoflix.core.domain.dto.*;
import com.licoflix.core.domain.model.film.Category;
import com.licoflix.core.domain.model.film.Film;
import com.licoflix.core.domain.model.film.FilmWatchingList;
import com.licoflix.core.domain.model.film.UserFilmList;
import com.licoflix.core.domain.repository.*;
import com.licoflix.core.mapper.FilmMapper;
import com.licoflix.core.service.rest.RestService;
import com.licoflix.util.response.DataListResponse;
import com.licoflix.util.response.DataResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService implements IFilmService {
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);

    private final RestService restService;
    private final FilmRepository filmRepository;
    private final CategoryRepository categoryRepository;
    private final FilmWatchingRepository filmWatchingRepository;
    private final FilmCategoryRepository filmCategoryRepository;
    private final UserFilmListRepository userFilmListRepository;

    @Override
    @Transactional
    public DataListResponse<FilmResponse> list(String search, String orderBy, String direction, Integer page, Integer pageSize) {
        String validOrderBy = StringUtils.isBlank(orderBy) ? "id" : orderBy;
        Sort.Direction sortDirection = Sort.Direction.fromString(StringUtils.isBlank(direction) ? "desc" : direction.toLowerCase());
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(new Sort.Order(sortDirection, validOrderBy)));

        Page<Film> pages = filmRepository.findAll(FilmSpecification.containsTextInAttributes(search), pageable);
        List<FilmResponse> films = pages.map(FilmMapper::entityToDTO).toList();

        return new DataListResponse<>(films, pages.getTotalPages(), pages.getTotalElements());
    }

    @Override
    @Transactional
    public DataListResponse<FilmGroupedByCategoryResponse> listByCategories(Integer page, Integer pageSize, String category) {
        page = page != null && page > 0 ? page : 1;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : 10;

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Object[]> filmsPage = (category != null && !category.trim().isEmpty())
                ? filmRepository.findFilmsWithCategoriesByCategory(category.trim(), pageable)
                : filmRepository.findFilmsWithCategories(pageable);

        Map<String, List<FilmResponse>> groupedFilmsMap = filmsPage.getContent()
                .parallelStream().collect(Collectors.groupingByConcurrent(row -> (String) row[0], Collectors.mapping(
                        row -> FilmMapper.entityToDTO((Film) row[1]), Collectors.toList())));

        List<FilmGroupedByCategoryResponse> groupedFilms = new ArrayList<>(groupedFilmsMap.size());
        groupedFilmsMap.forEach((key, value) ->
                groupedFilms.add(new FilmGroupedByCategoryResponse(key, value))
        );

        return new DataListResponse<>(groupedFilms, filmsPage.getTotalPages(), filmsPage.getTotalElements());
    }

    @Override
    public DataListResponse<CategoryResponse> listCategories() {
        DataListResponse<CategoryResponse> response = new DataListResponse<>();

        List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Order.asc("name")));
        response.setData(categories.stream().map(category -> CategoryResponse.builder().id(category.getId())
                .name(category.getName()).build()).toList());

        response.setTotalElements((long) categories.size());
        return response;
    }

    @Override
    @Transactional
    public DataResponse<FilmResponse> save(FilmRequest filmRequest, String authorization, String timezone) throws IOException {
        List<Category> categories;

        UserDetailsResponse userDetails = restService.getUserDetails(authorization, timezone);

        List<String> categoryNames = filmRequest.getCategories();
        Film film = FilmMapper.requestToEntity(filmRequest, filmRequest.getImage(), filmRequest.getBackground(), userDetails.getId());

        Optional<Film> filmFromDB = filmRepository.getByTitleAndYear(filmRequest.getTitle(), filmRequest.getYear());
        filmFromDB.ifPresent(db -> FilmMapper.setChangedInfo(db.getId(), film, userDetails.getId()));

        categories = categoryNames.stream().map(this::getCategory).toList();
        film.setCategories(categories);

        saveFilmFiles(filmRequest);

        filmRepository.save(film);

        return new DataResponse<>(FilmMapper.entityToDTO(film));
    }

    @Override
    @Transactional
    public DataResponse<FilmResponse> get(Long id) {
        DataResponse<FilmResponse> response = new DataResponse<>();

        Optional<Film> film = Optional.of(filmRepository.getReferenceById(id));
        film.ifPresent(value -> response.setData(FilmMapper.entityToDTO(value)));

        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) throws Exception {
        Film film = filmRepository.findById(id).orElseThrow(() -> new Exception("Film with ID " + id + " not found"));
        deleteFilmFiles(film.getTitle());

        filmCategoryRepository.deleteByFilmId(film.getId());
        userFilmListRepository.deleteByFilmId(film.getId());
        filmRepository.delete(film);
    }

    @Override
    @Transactional
    public DataResponse<FilmResponse> addFilmInList(Long id, String authorization, String timezone) throws Exception {
        UserDetailsResponse userDetails = restService.getUserDetails(authorization, timezone);
        Film film = filmRepository.findById(id).orElseThrow(() -> new Exception("Film with ID " + id + " not found"));
        List<UserFilmList> list = userFilmListRepository.findByUserId(userDetails.getId());

        if (list.stream().noneMatch(x -> x.getFilm().equals(film)))
            userFilmListRepository.save(UserFilmList.builder().film(film).user(userDetails.getId()).build());

        return new DataResponse<>(FilmMapper.entityToDTO(film));
    }

    @Override
    @Transactional
    public DataListResponse<FilmResponse> geFilmUserList(String authorization, String timezone) {

        UserDetailsResponse userDetails = restService.getUserDetails(authorization, timezone);
        List<UserFilmList> list = userFilmListRepository.findByUserId(userDetails.getId());

        List<FilmResponse> response = new ArrayList<>(FilmMapper.entityToDTOList(list.stream().map(UserFilmList::getFilm).toList()));

        return new DataListResponse<>(response, 1, response.size());
    }

    @Override
    @Transactional
    public void removeFilmFromList(Long id, String authorization, String timezone) throws Exception {
        UserDetailsResponse userDetails = restService.getUserDetails(authorization, timezone);
        Film film = filmRepository.findById(id).orElseThrow(() -> new Exception("Film with ID " + id + " not found"));

        userFilmListRepository.removeByUserIdAndFilmId(userDetails.getId(), film.getId());
    }

    @Override
    @Transactional
    public void addToContinueWatchList(String title, String current, String duration,
                                       String authorization, String timezone) {
        UserDetailsResponse user = restService.getUserDetails(authorization, timezone);
        Film film = filmRepository.findByTitle(title).orElseThrow(() -> new EntityNotFoundException("Film not found: " + title));
        List<FilmWatchingList> records = filmWatchingRepository.findByFilmIdAndUserIdOrderByIdDesc(film.getId(), user.getId());

        if (!records.isEmpty()) {
            filmWatchingRepository.deleteAll(records);
        }

        float currentTime = Float.parseFloat(current);
        float totalDuration = Float.parseFloat(duration);

        if (!(totalDuration - currentTime <= 300 || currentTime <= 8)) {
            FilmWatchingList newRecord = FilmWatchingList.builder().film(film).user(user.getId()).current(current).duration(duration).build();
            filmWatchingRepository.save(newRecord);
        }
    }

    @Override
    @Transactional
    public void removeFromContinueWatchList(String title, String authorization, String timezone) throws Exception {
        UserDetailsResponse user = restService.getUserDetails(authorization, timezone);
        Film film = filmRepository.findByTitle(title).orElseThrow(() -> new Exception("Film with title " + title + " not found"));
        FilmWatchingList watching = filmWatchingRepository.getByFilmAndUser(user.getId(), film.getId());

        filmWatchingRepository.delete(watching);
    }

    @Override
    @Transactional
    public DataListResponse<FilmWatchingListResponse> listWatchingFilmList(String authorization, String timezone) throws Exception {
        UserDetailsResponse user = restService.getUserDetails(authorization, timezone);
        List<FilmWatchingList> watching = filmWatchingRepository.findByUser(user.getId());

        return new DataListResponse<>(FilmMapper.watchingListToDTO(watching), 1, watching.size());
    }

    private Category getCategory(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    return categoryRepository.save(newCategory);
                });
    }

    private void saveFilmFiles(FilmRequest request) throws IOException {
        if (request.getFilm() != null || request.getSubtitle() != null || request.getSubtitleEn() != null) {
            String baseDir = "film/";

            Path basePath = Path.of(baseDir);
            if (Files.notExists(basePath)) {
                Files.createDirectories(basePath);
                logger.debug("Base directory created: {}", basePath.toAbsolutePath());
            }

            Path filmTitleDir = basePath.resolve(request.getTitle());
            if (Files.notExists(filmTitleDir)) {
                Files.createDirectories(filmTitleDir);
                logger.debug("Directory for movie '{}' created: {}", request.getTitle(), filmTitleDir.toAbsolutePath());
            }

            Path filmPath = filmTitleDir.resolve(request.getTitle() + ".mp4");
            if (request.getFilm() != null) {
                try (var inputStream = request.getFilm().getInputStream()) {
                    Files.copy(inputStream, filmPath, StandardCopyOption.REPLACE_EXISTING);
                    logger.debug("Film '{}' saved at {}", request.getTitle(), filmPath.toAbsolutePath());
                } catch (IOException e) {
                    logger.error("Failed to save film '{}' at {}: {}", request.getTitle(), filmPath.toAbsolutePath(), e.getMessage());
                    throw new IOException("Error saving film file", e);
                }
            }

            if (request.getSubtitle() != null) {
                Path subtitlePath = filmTitleDir.resolve(request.getTitle() + " ptbr.vtt");
                try (var subtitleInputStream = request.getSubtitle().getInputStream()) {
                    Files.copy(subtitleInputStream, subtitlePath, StandardCopyOption.REPLACE_EXISTING);
                    logger.debug("Subtitle-ptbr for '{}' saved at {}", request.getTitle(), subtitlePath.toAbsolutePath());
                } catch (IOException e) {
                    logger.error("Failed to save subtitle-ptbr for film '{}' at {}: {}", request.getTitle(), subtitlePath.toAbsolutePath(), e.getMessage());
                    throw new IOException("Error saving subtitle-ptbr file", e);
                }
            }

            if (request.getSubtitleEn() != null) {
                Path subtitlePath = filmTitleDir.resolve(request.getTitle() + " en.vtt");
                try (var subtitleInputStream = request.getSubtitleEn().getInputStream()) {
                    Files.copy(subtitleInputStream, subtitlePath, StandardCopyOption.REPLACE_EXISTING);
                    logger.debug("Subtitle-en for '{}' saved at {}", request.getTitle(), subtitlePath.toAbsolutePath());
                } catch (IOException e) {
                    logger.error("Failed to save subtitle-en for film '{}' at {}: {}", request.getTitle(), subtitlePath.toAbsolutePath(), e.getMessage());
                    throw new IOException("Error saving subtitle-en file", e);
                }
            }
        }
    }

    private void deleteFilmFiles(String title) {
        String baseDir = "film/";
        Path basePath = Path.of(baseDir);

        if (!Files.exists(basePath)) {
            logger.warn("Base directory '{}' does not exist", baseDir);
            return;
        }

        Path filmTitleDir = basePath.resolve(title);

        if (!Files.exists(filmTitleDir)) {
            logger.warn("No directory found for film '{}'", title);
            return;
        }

        Path filmPath = filmTitleDir.resolve(title + ".mp4");
        if (Files.exists(filmPath)) {
            try {
                Files.delete(filmPath);
                logger.debug("Film '{}' deleted at {}", title, filmPath.toAbsolutePath());
            } catch (IOException e) {
                logger.error("Failed to delete film '{}' at {}: {}", title, filmPath.toAbsolutePath(), e.getMessage());
            }
        } else {
            logger.debug("No film file found for '{}'", title);
        }

        Path subtitlePath = filmTitleDir.resolve(title + " ptbr.vtt");
        if (Files.exists(subtitlePath)) {
            try {
                Files.delete(subtitlePath);
                logger.debug("Subtitle for '{}' deleted at {}", title, subtitlePath.toAbsolutePath());
            } catch (IOException e) {
                logger.error("Failed to delete subtitle for film '{}' at {}: {}", title, subtitlePath.toAbsolutePath(), e.getMessage());
            }
        } else {
            logger.debug("No subtitle file found for '{}'", title);
        }

        try {
            Files.delete(filmTitleDir);
            logger.debug("Directory for film '{}' deleted: {}", title, filmTitleDir.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to delete directory for film '{}' at {}: {}", title, filmTitleDir.toAbsolutePath(), e.getMessage());
        }
    }
}