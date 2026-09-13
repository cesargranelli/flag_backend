package br.com.flagplatform.person.dto.response;

import br.com.flagplatform.person.dto.request.CreatePersonBatchItem;

public record PersonBatchLineResult(
        Integer line,
        String status,
        String reason,
        CreatePersonBatchItem item
) {
}
