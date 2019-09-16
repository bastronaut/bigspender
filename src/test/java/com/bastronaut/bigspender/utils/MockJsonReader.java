package com.bastronaut.bigspender.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class MockJsonReader {

    public static final String pathPrepend = "src/test/resources/mockjson/";

    public static String readMockJsonAsString(final String fileName) {
        if (StringUtils.isBlank(fileName)) return StringUtils.EMPTY;

        StringBuilder sb = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(pathPrepend + fileName), StandardCharsets.UTF_8)) {
            stream.forEach(s -> sb.append(s));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }


}
