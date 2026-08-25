package com.romanpulov.piastriawss.dto.serializer;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer extends StdDeserializer<LocalDate> {
    public LocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    public LocalDateDeserializer() {
        this(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) {
        return LocalDate.parse(p.readValueAs(String.class), DateTimeFormatter.ISO_DATE_TIME);
    }
}
