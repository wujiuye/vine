package com.wujiuye.vine.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

/**
 * 序列化工具
 *
 * @author wujiuye 2020/08/26
 */
public class SerializeUtils {

    private final static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * 序列化
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String serialize(T obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 反序列化
     *
     * @param value
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T deserialization(String value, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(value, tClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 反序列化
     *
     * @param in
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T deserialization(InputStream in, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(in, tClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
