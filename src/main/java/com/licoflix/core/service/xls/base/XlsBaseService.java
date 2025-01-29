package com.licoflix.core.service.xls.base;

import com.licoflix.core.domain.dto.XlsFilterEnum;
import com.licoflix.util.request.FilterRequest;

import java.io.IOException;

public interface XlsBaseService {
    byte[] generateXls(String fileName, String timezone, FilterRequest<Object> filter) throws IOException;

    XlsFilterEnum getType();
}
