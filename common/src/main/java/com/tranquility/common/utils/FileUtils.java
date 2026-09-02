package com.tranquility.common.utils;

import org.apache.tika.Tika;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtils {

    private static final Tika TIKA = new Tika();

    public static String detectContentType(InputStream fileContent) throws IOException {
        return TIKA.detect(fileContent);
    }
}
