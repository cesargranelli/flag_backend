package br.com.flagplatform.institution.dto.response;

import br.com.flagplatform.common.enums.InstitutionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InstitutionResponse(
        UUID id,
        String name,
        InstitutionType type,
        List<String> colors,
        List<UUID> organizations,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
