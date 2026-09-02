package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.dto.ScanResultDTO;
import com.romanpulov.tursocore.TursoClient;
import com.romanpulov.tursocore.TursoException;
import jakarta.annotation.Nonnull;
import org.json.JSONObject;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScanService {
    private static final Pattern REGEX_AMOUNT = Pattern.compile("\\{'amount':\\s*([0-9.,]+)\\s*\\}");

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
  st.scan_desc,
  sr.scan_result
FROM scan_results sr
INNER JOIN scan_types st ON sr.scan_type_id = st.scan_type_id
INNER JOIN os ON os.max_scan_id = sr.scan_id
WHERE os.object_desc = '%s'
""";

    private final TursoClient tursoClient;

    public ScanService(TursoClient tursoClient) {
        this.tursoClient = tursoClient;
    }

    @Retryable(retryFor = TursoException.class, backoff = @Backoff(delay = 10))
    public List<ScanResultDTO> findLatestScanResults(@Nonnull String objectName) throws TursoException {
        String sql = String.format(ScanService.SQL_TEXT, objectName);
        List<JSONObject> rawResult = this.tursoClient.executeQuery(sql);

        return rawResult
                .stream()
                .map(v -> {
                    String productName = v.getString("scan_desc");
                    Matcher matcher = REGEX_AMOUNT.matcher(v.getString("scan_result"));

                    BigDecimal amount = BigDecimal.ZERO;
                    if (matcher.find()) {
                        String amountString = matcher.group(1);
                        try {
                            amount = new BigDecimal(amountString);
                        }  catch (NumberFormatException ignored) {}
                    }

                    return new ScanResultDTO(productName, amount);
                })
                .toList();
    }
}
