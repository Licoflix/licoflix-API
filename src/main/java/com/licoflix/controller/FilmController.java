package com.licoflix.controller;

import com.licoflix.core.domain.common.DomainReturnCode;
import com.licoflix.core.domain.dto.CategoryResponse;
import com.licoflix.core.domain.dto.FilmGroupedByCategoryResponse;
import com.licoflix.core.domain.dto.FilmRequest;
import com.licoflix.core.domain.dto.FilmResponse;
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
            @RequestParam(name = "category", required = false) String category
    ) {
        logger.info("List method" + STARTED);

        DataListResponse<FilmResponse> response = service.list(search, category, page, pageSize);
        response.setMessage(DomainReturnCode.SUCCESSFUL_OPERATION.getDesc());

        logger.debug("List method, Response: {}", response.getData());
        logger.info("List method" + FINISHED);
        return response;
    }

    @GetMapping("/grouped")
    @Operation(summary = "List Films Grouped by Categories", description = "List Films Grouped by Categories")
    public DataListResponse<FilmGroupedByCategoryResponse> listGroupedByCategories() {
        logger.info("List Films Grouped by Categories method" + STARTED);

        DataListResponse<FilmGroupedByCategoryResponse> response = service.listByCategories();
        response.setMessage(DomainReturnCode.SUCCESSFUL_OPERATION.getDesc());

        logger.debug("List Films Grouped by Categories method, Response: {}", response.getData());
        logger.info("List Films Grouped by Categories method" + FINISHED);
        return response;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Film", description = "Get Film by Id")
    public DataResponse<FilmResponse> get(@PathVariable(name = "id") Long id) {
        logger.info("Get method" + STARTED);

        DataResponse<FilmResponse> response = service.get(id);
        response.setMessage(DomainReturnCode.SUCCESSFUL_OPERATION.getDesc());

        logger.debug("Get method, Response: {}", response.getData());
        logger.info("Get method" + FINISHED);
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
        logger.info("List Categories method" + STARTED);

        DataListResponse<CategoryResponse> response = service.listCategories();
        response.setMessage(DomainReturnCode.SUCCESSFUL_OPERATION.getDesc());

        logger.debug("List Categories method, Response: {}", response.getData());
        logger.info("List Categories method" + FINISHED);
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

        logger.debug("Create method, Response: {}", response.getData());
        logger.info("Create method" + FINISHED);
        return response;
    }

    @PostMapping("/user/list/{id}")
    @Operation(summary = "Add Film in User List", description = "Add Film in User List")
    public DataResponse<FilmResponse> add(
            @PathVariable Long id,
            @RequestHeader(name = "Timezone") String timezone,
            @RequestHeader(name = "Authorization") String authorization) throws Exception {
        logger.info("Add film in list method" + STARTED);
        DataResponse<FilmResponse> response = service.addFilmInList(id, authorization, timezone);

        logger.debug("Add film in list method, Response: {}", response.getData());
        logger.info("Add film in list method" + FINISHED);
        return response;
    }

    @GetMapping("/user/list")
    @Operation(summary = "Get Film User List", description = "Get Film User List")
    public DataListResponse<FilmResponse> get(
            @RequestHeader(name = "Timezone") String timezone,
            @RequestHeader(name = "Authorization") String authorization) throws Exception {
        logger.info("Get Film User List method" + STARTED);
        DataListResponse<FilmResponse> response = service.geFilmUserList(authorization, timezone);

        logger.debug("Get Film User List method, Response: {}", response.getData());
        logger.info("Get Film User List method" + FINISHED);
        return response;
    }

    @DeleteMapping("/user/list/{id}")
    @Operation(summary = "Delete Film from User List", description = "Delete Film from User List")
    public void delete(
            @PathVariable Long id,
            @RequestHeader(name = "Timezone") String timezone,
            @RequestHeader(name = "Authorization") String authorization) throws Exception {
        logger.info("Delete Film from User List method" + STARTED);
        service.removeFilmFromList(id, authorization, timezone);

        logger.info("Delete Film from User List method" + FINISHED);
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
}