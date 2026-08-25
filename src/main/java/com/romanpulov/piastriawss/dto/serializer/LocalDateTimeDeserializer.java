package com.romanpulov.piastriawss.dto.serializer;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    public LocalDateTimeDeserializer() {
        this(null);
    }

    public LocalDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) {
        return LocalDateTime.parse(p.readValueAs(String.class), DateTimeFormatter.ISO_DATE_TIME);
    }
}
