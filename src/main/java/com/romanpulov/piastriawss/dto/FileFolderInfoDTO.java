package com.romanpulov.piastriawss.dto;

import java.util.List;

public record FileFolderInfoDTO(String folderName, List<FileInfoDTO> fileList) {
}
