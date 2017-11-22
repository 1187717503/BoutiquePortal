package com.intramirror.web.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2017/11/22.
 *
 * @author YouFeng.Zhu
 */
public final class JsonTransformUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonTransformUtil.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static <T> T readValue(String jsonStr, Class<T> valueType) {

        try {
            return objectMapper.readValue(jsonStr, valueType);
        } catch (IOException e) {
            LOGGER.warn("Failed to transform String [{}] -> [{}].", jsonStr, valueType.getClass());
        }
        return null;
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.warn("Failed to transform Object [{}] -> String.", object.getClass());
        }

        return null;
    }

}
