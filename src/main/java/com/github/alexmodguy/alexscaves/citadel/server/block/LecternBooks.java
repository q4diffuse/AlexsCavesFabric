package com.github.alexmodguy.alexscaves.citadel.server.block;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LecternBooks {
    public static final Map<Object, BookData> BOOKS = new LinkedHashMap<>();

    private LecternBooks() {
    }

    public record BookData(int coverColor, int pageColor) {
    }
}
