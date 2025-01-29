package com.licoflix.core.service.xls.builder;

import java.io.IOException;
import java.util.List;

public interface XlsBuilderService {
    byte[] generateXlsFile(String[] header, List<String[]> lines, String fileName, String timezone) throws IOException;
}