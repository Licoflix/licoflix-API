package com.licoflix.controller;

import com.licoflix.core.domain.common.DomainReturnCode;
import com.licoflix.core.domain.dto.*;
import com.licoflix.core.service.film.IFilmService;
import com.licoflix.util.response.DataListResponse;
import com.licoflix.util.response.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "film")
@CrossOrigin(origins = "${api.access.control.allow.origin}")
@Tag(name = "Film Controller", description = "Endpoints of Film Controller")
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    private static final String STARTED = " - Started";
    private static final String FINISHED = " - Finished";

    private final IFilmService service;

    @GetMapping()
    @Operation(summary = "List Films", description = "List Films by Filters")
    public DataListResponse<FilmResponse> list(
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "orderBy", required = false) String orderBy,
            @RequestParam(name = "direction", required = false) String direction
    ) {
        DataListResponse<FilmResponse> response = service.list(search, orderBy, direction, page, pageSize);
        response.setMessage(DomainReturnCode.SUCCESSFUL_OPERATION.getDesc());
        return response;
    }

    @GetMapping("/grouped")
    @Operation(summary = "List Films Grouped by Categories", description = "List Films Grouped by Categories")
    public DataListResponse<FilmGroupedByCategoryResponse> listGroupedByCategories(
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "category", required = false) String category
    ) {
        DataListResponse<FilmGroupedByCategoryResponse> response = service.listByCategories(page, pageSize, category);
        response.setMessage(DomainReturnCode.SUCCESSFUL_OPERATION.getDesc());
        return response;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Film", description = "Get Film by Id")
    public DataResponse<FilmResponse> get(@PathVariable(name = "id") Long id) {
        DataResponse<FilmResponse> response = service.get(id);
        response.setMessage(DomainReturnCode.SUCCESSFUL_OPERATION.getDesc());
        return response;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Film", description = "Delete Film by Id")
    public void delete(@PathVariable(name = "id") Long id) throws Exception {
        logger.info("Delete method" + STARTED);
        service.delete(id);
        logger.info("Delete method" + FINISHED);
    }

    @GetMapping("/category")
    @Operation(summary = "List Categories", description = "List Categories")
    public DataListResponse<CategoryResponse> listCategories() {
        DataListResponse<CategoryResponse> response = service.listCategories();
        response.setMessage(DomainReturnCode.SUCCESSFUL_OPERATION.getDesc());
        return response;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Film", description = "Create Film")
    public DataResponse<FilmResponse> save(
            @ModelAttribute FilmRequest filmRequest,
            @RequestHeader(name = "Timezone") String timezone,
            @RequestHeader(name = "Authorization") String authorization
    ) throws IOException, GeneralSecurityException {
        logger.info("Create method" + STARTED);

        DataResponse<FilmResponse> response = service.save(filmRequest, authorization, timezone);
        response.setMessage(DomainReturnCode.SUCCESSFUL_OPERATION.getDesc());

        logger.info("Create method" + FINISHED);
        return response;
    }

    @PostMapping("/user/list/{id}")
    @Operation(summary = "Add Film in User List", description = "Add Film in User List")
    public DataResponse<FilmResponse> add(
            @PathVariable Long id,
            @RequestHeader(name = "Timezone") String timezone,
            @RequestHeader(name = "Authorization") String authorization) throws Exception {
        return service.addFilmInList(id, authorization, timezone);
    }

    @GetMapping("/user/list")
    @Operation(summary = "Get Film User List", description = "Get Film User List")
    public DataListResponse<FilmResponse> get(
            @RequestHeader(name = "Timezone") String timezone,
            @RequestHeader(name = "Authorization") String authorization) throws Exception {
        return service.geFilmUserList(authorization, timezone);
    }

    @DeleteMapping("/user/list/{id}")
    @Operation(summary = "Delete Film from User List", description = "Delete Film from User List")
    public void delete(
            @PathVariable Long id,
            @RequestHeader(name = "Timezone") String timezone,
            @RequestHeader(name = "Authorization") String authorization) throws Exception {
        service.removeFilmFromList(id, authorization, timezone);
    }

    @GetMapping("/{title}/video")
    @Operation(summary = "Get Film and Subtitle", description = "Fetch Film and Subtitle by Title")
    public ResponseEntity<Resource> getFilm(@PathVariable String title) throws IOException {
        title = URLDecoder.decode(title, StandardCharsets.UTF_8);

        String filmDirPath = "film/" + title + "/" + title + ".mp4";
        Path filmPath = Paths.get(filmDirPath);

        Resource filmResource = new UrlResource(filmPath.toUri());
        if (!Files.exists(filmPath) || !Files.isReadable(filmPath)) {
            throw new IOException("Film not found or is not readable: " + filmDirPath);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + title + ".mp4\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(filmResource);
    }

    @GetMapping("/{title}/{language}/subtitle")
    @Operation(summary = "Get Subtitle", description = "Fetch Subtitle file by Title")
    public ResponseEntity<Resource> getSubtitle(@PathVariable String title, @PathVariable String language) throws IOException {
        title = URLDecoder.decode(title, StandardCharsets.UTF_8);

        String subtitleDirPath = "film/" + title + "/" + title + " " + language + ".vtt";
        Path subtitlePath = Paths.get(subtitleDirPath);

        Resource subtitleResource = new UrlResource(subtitlePath.toUri());
        if (!subtitleResource.exists() || !subtitleResource.isReadable()) {
            throw new IOException("Subtitle not found: " + title);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/vtt; charset=UTF-8"));
        headers.setContentDisposition(ContentDisposition.inline().filename(title + ".vtt").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(subtitleResource);
    }

    @PostMapping("/continue-watching")
    @Operation(summary = "Add to Continue Watch List", description = "Add film to continue watching list")
    public void addToContinueWatchList(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "current") String currentTime,
            @RequestParam(name = "duration") String duration,
            @RequestHeader(name = "Authorization") String authorization,
            @RequestHeader(name = "Timezone") String timezone) throws Exception {
        service.addToContinueWatchList(title, currentTime, duration, authorization, timezone);
    }

    @DeleteMapping("/continue-watching")
    @Operation(summary = "Remove from Continue Watch List", description = "Remove film from continue watching list")
    public void removeFromContinueWatchList(
            @RequestParam(name = "title") String title,
            @RequestHeader(name = "Authorization") String authorization,
            @RequestHeader(name = "Timezone") String timezone) throws Exception {
        service.removeFromContinueWatchList(title, authorization, timezone);
    }

    @GetMapping("/continue-watching")
    @Operation(summary = "List Continue Watching Films", description = "List all films in continue watching list")
    public DataListResponse<FilmWatchingListResponse> listContinueWatchList(
            @RequestHeader(name = "Authorization") String authorization,
            @RequestHeader(name = "Timezone") String timezone) throws Exception {
        return service.listWatchingFilmList(authorization, timezone);
    }
}