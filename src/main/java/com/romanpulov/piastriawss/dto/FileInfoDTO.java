package com.romanpulov.piastriawss.dto;

import com.romanpulov.piastriawss.dto.serializer.LocalDateDeserializer;
import com.romanpulov.piastriawss.dto.serializer.LocalDateSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;

public record FileInfoDTO(
        String fileName,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate modifiedDate) {
}
