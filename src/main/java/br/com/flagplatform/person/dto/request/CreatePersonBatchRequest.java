package br.com.flagplatform.person.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreatePersonBatchRequest(
        @NotEmpty
        List<CreatePersonBatchItem> persons
) {
}
