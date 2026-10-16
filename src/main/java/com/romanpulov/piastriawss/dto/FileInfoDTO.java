package com.romanpulov.piastriawss.dto;

import com.romanpulov.piastriawss.dto.serializer.LocalDateTimeDeserializer;
import com.romanpulov.piastriawss.dto.serializer.LocalDateTimeSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public record FileInfoDTO(
        String fileName,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime modifiedDate) {
}
