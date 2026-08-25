package com.romanpulov.piastriawss.dto.serializer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
    public LocalDateTimeSerializer(Class<LocalDateTime> t) {
        super(t);
    }

    public LocalDateTimeSerializer() {
        this(null);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext provider) {
        gen.writeString(value.format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
