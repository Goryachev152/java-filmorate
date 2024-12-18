package ru.yandex.practicum.filmorate.adapters;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDate value) throws IOException {
        if (Objects.isNull(value)) {
            jsonWriter.value("null");
        } else {
            jsonWriter.value(value.format(DateTimeFormatter.ISO_DATE));
        }
    }

    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        LocalDate date;
        try {
            date = LocalDate.parse(jsonReader.nextString(), DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException ex) {
            date = null;
        }
        return date;
    }
}