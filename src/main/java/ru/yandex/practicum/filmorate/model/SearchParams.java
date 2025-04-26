package ru.yandex.practicum.filmorate.model;

public enum SearchParams {
    DIRECTOR,
    TITLE;

    public static boolean isValid(String value) {
        for (SearchParams param : values()) {
            if (value.equalsIgnoreCase(param.name())) {
                return true;
            }
        }
        return false;
    }
}
