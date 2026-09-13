package br.com.flagplatform.person.dto.response;

import java.util.List;

public record PersonBatchResponse(
        int total,
        int imported,
        int skipped,
        List<PersonBatchLineResult> lines
) {
}
