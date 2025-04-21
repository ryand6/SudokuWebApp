package com.github.ryand6.sudokuweb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ArrayUtils {

    private ArrayUtils() {

    }

    // Deserialise JSON string format of nested integer array
    public static int[][] jsonStringToNestedIntArray(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int[][] nestedArray = mapper.readValue(jsonString, int[][].class);
        return nestedArray;
    }


}
