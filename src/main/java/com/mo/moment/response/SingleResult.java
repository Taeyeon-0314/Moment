package com.mo.moment.response;

import com.mo.moment.jwt.exception.CommonResult;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleResult<T> extends CommonResult {
    private T data;
}
