package com.licoflix.core.domain.dto;

import lombok.Getter;

@Getter
public enum XlsFilterEnum {

    CUSTOMER("Film", Object.class);

    private final String description;
    private final Class<?> filterClass;

    XlsFilterEnum(String description, Class<?> filterClass) {
        this.description = description;
        this.filterClass = filterClass;
    }

    public static XlsFilterEnum findByDescription(String description) {
        for (XlsFilterEnum filter : XlsFilterEnum.values()) {
            if (filter.getDescription().replaceAll("\\s+", "")
                    .equalsIgnoreCase(description.replaceAll("\\s+", ""))) {
                return filter;
            }
        }
        throw new IllegalArgumentException("No enum constant with description " + description);
    }
}