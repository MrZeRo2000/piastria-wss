package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.dto.ScanResultDTO;
import com.romanpulov.tursocore.TursoClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScanService {
    private static final String SQL_TEXT = """
WITH os AS (
  SELECT
    o.object_desc,
    MAX(scan_id) AS max_scan_id
  FROM objects o
  INNER JOIN providers p ON o.provider_id = p.provider_id AND p.provider_code = 'komunalka'
  INNER JOIN scans s ON o.object_id = s.scan_id
)
SELECT
  os.object_desc,
  st.scan_desc,
  sr.scan_result
FROM scan_results sr
INNER JOIN scan_types st ON sr.scan_type_id = st.scan_type_id
INNER JOIN os ON os.max_scan_id = sr.scan_id
""";

    private final TursoClient tursoClient;

    public ScanService(TursoClient tursoClient) {
        this.tursoClient = tursoClient;
    }

    List<ScanResultDTO> findLatestScanResults() {
        List<ScanResultDTO> result = new ArrayList<>();

        return result;
    }
}
