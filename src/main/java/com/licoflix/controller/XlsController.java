package com.licoflix.controller;

import com.licoflix.core.domain.dto.XlsFilterEnum;
import com.licoflix.core.service.xls.base.XlsBaseService;
import com.licoflix.core.service.xls.base.XlsBaseServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "film/xls")
@CrossOrigin(origins = "${api.access.control.allow.origin}")
@Tag(name = "XLS Controller", description = "Endpoints of XLS Controller")
public class XlsController {
    private static final Logger log = LoggerFactory.getLogger(XlsController.class);

    private static final String STARTED = " - Started";
    private static final String FINISHED = " - Finished";

    private final XlsBaseServiceImp service;

    @GetMapping()
    @Operation(summary = "Get XLS", description = "Get XLS by Type")
    public void xls(
            @Parameter(description = "Type") @RequestParam(name = "type") String type,
            @Parameter(description = "Timezone") @RequestHeader(name = "Timezone") String timezone,
            HttpServletResponse response
    ) throws IOException {
        String methodName = "generateXlsFile";
        String zipFileName = getFileName(type, timezone) + ".zip";
        String xlsFileName = getFileName(type, timezone) + ".xlsx";

        log.info(methodName + STARTED);

        XlsBaseService baseService = service.getService(XlsFilterEnum.findByDescription(type));
        byte[] xlsBytes = baseService.generateXls(xlsFileName, timezone, null);

        ByteArrayOutputStream zipOutputStream = getByteArrayOutputStream(xlsFileName, xlsBytes);

        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
        response.setContentType("application/zip");

        response.getOutputStream().write(zipOutputStream.toByteArray());
        response.flushBuffer();

        log.info(methodName + FINISHED);
    }

    private static @NotNull ByteArrayOutputStream getByteArrayOutputStream(String xlsFileName, byte[] xlsBytes) throws IOException {
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(zipOutputStream)) {
            ZipArchiveEntry zipEntry = new ZipArchiveEntry(xlsFileName);
            zipOut.putArchiveEntry(zipEntry);
            zipOut.write(xlsBytes);
            zipOut.closeArchiveEntry();
        }
        return zipOutputStream;
    }

    private static @NotNull String getFileName(String type, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        String timestamp;
        if (timezone.equals("America/Sao_Paulo"))
            timestamp = LocalDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("dd-MM-yyyy_H'h'mm"));
        else
            timestamp = LocalDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_H'h'mm"));
        return type.replace(" ", "") + "List_" + timestamp;
    }
}
