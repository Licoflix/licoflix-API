package com.licoflix.core.service.xls.film;

import com.licoflix.core.domain.dto.XlsFilterEnum;
import com.licoflix.core.domain.repository.FilmRepository;
import com.licoflix.util.request.FilterRequest;
import com.licoflix.core.service.xls.base.XlsBaseService;
import com.licoflix.core.service.xls.builder.XlsBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmXlsServiceImpl implements XlsBaseService {

    private final FilmRepository repository;
    private final XlsBuilderService builderService;

    @Override
    public byte[] generateXls(String fileName, String timezone, FilterRequest<Object> filter) throws IOException {
        String[] header = new String[]{"Title", "Categories", "Year", "Duration", "Directors", "Producers", "Cast"};
        List<String[]> lines = repository.findAllForXLS();
        return builderService.generateXlsFile(header, lines, fileName, timezone);
    }

    @Override
    public XlsFilterEnum getType() {
        return XlsFilterEnum.CUSTOMER;
    }
}