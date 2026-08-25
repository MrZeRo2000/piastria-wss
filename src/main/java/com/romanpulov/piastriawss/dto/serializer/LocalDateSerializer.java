package com.romanpulov.piastriawss.dto.serializer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends StdSerializer<LocalDate> {
    public LocalDateSerializer(Class<LocalDate> t) {
        super(t);
    }

    public LocalDateSerializer() {
        this(LocalDate.class);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializationContext provider) {
        gen.writeString(value.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
