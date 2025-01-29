package com.licoflix.core.service.xls.base;

import com.licoflix.core.domain.dto.XlsFilterEnum;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class XlsBaseServiceImp {

    private final Map<XlsFilterEnum, XlsBaseService> services;

    public XlsBaseServiceImp(List<XlsBaseService> facades) {
        services = new LinkedHashMap<>();
        facades.forEach(base -> services.put(base.getType(), base));
    }

    public XlsBaseService getService(XlsFilterEnum filterEnum) {
        return services.get(filterEnum);
    }
}
