package org.baylist.util.convert;

import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class InputStreamConverter {

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        return (inputStream == null || inputStream.available() == 0)
                ? ""
                : StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    }

}
