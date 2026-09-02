package com.romanpulov.piastriawss.controller;

import com.romanpulov.piastriawss.dto.ScanResultDTO;
import com.romanpulov.piastriawss.service.ScanService;
import com.romanpulov.tursocore.TursoException;
import jakarta.annotation.Nonnull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @GetMapping(value="/scan-latest", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ScanResultDTO>> getLatestScanResults(@Nonnull String objectName) throws TursoException {
        return ResponseEntity.ok(this.scanService.findLatestScanResults(objectName));
    }
}
