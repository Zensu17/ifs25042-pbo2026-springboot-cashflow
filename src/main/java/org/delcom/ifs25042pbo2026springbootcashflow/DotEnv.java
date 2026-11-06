package org.delcom.ifs25042pbo2026springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Memuat file .env ke system properties sebelum Spring Boot start. */
public final class DotEnv {

    private DotEnv() {
    }

    public static void load() throws IOException {
        load(Path.of(".env"));
    }

    static void load(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        List<String> lines = Files.readAllLines(file);
        for (String raw : lines) {
            String line = raw.trim();
            int eq = line.indexOf('=');
            if (line.isEmpty() || line.startsWith("#") || eq < 1) {
                continue;
            }
            String key = line.substring(0, eq).trim();
            // Environment OS / -D menang atas .env.
            if (System.getProperty(key) == null && System.getenv(key) == null) {
                System.setProperty(key, line.substring(eq + 1).trim());
            }
        }
    }
}
