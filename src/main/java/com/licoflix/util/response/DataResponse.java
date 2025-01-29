package com.licoflix.util.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse<T> extends Response {

    @Serial
    private static final long serialVersionUID = 5605829304634L;

    private T data;
}