package com.github.alexthe666.citadel.server.block;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LecternBooks {
    public static final Map<Object, BookData> BOOKS = new LinkedHashMap<>();

    private LecternBooks() {
    }

    public record BookData(int coverColor, int pageColor) {
    }
}
