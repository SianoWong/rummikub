package com.rummikub.util;

import com.rummikub.exception.CustomException;
import com.rummikub.enums.ExceptionEnum;
import org.springframework.util.StringUtils;

public class CheckUtil {
    public static String formatNonNull(String text, ExceptionEnum exception) {
        if (StringUtils.hasText(text)) {
            return text.trim();
        }
        throw new CustomException(exception);
    }
}
