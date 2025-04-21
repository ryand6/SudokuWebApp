package com.github.ryand6.sudokuweb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.ryand6.sudokuweb.util.ArrayUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public final class ArrayUtilsTests {

    @Test
    void testJsonStringToNestedIntArray() throws JsonProcessingException {
        String json = "[[1,2],[3,4]]";

        int[][] expected = {
                {1, 2},
                {3, 4}
        };

        int[][] result = ArrayUtils.jsonStringToNestedIntArray(json);

        assertArrayEquals(expected, result);
    }

}
