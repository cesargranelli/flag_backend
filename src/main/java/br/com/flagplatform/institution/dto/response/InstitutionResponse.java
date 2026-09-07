package br.com.flagplatform.institution.dto.response;

import br.com.flagplatform.common.enums.DocumentType;
import br.com.flagplatform.common.enums.InstitutionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InstitutionResponse(
        UUID id,
        String name,
        String tradeName,
        String legalName,
        InstitutionType type,
        String abbreviation,
        String document,
        DocumentType documentType,
        String presidentName,
        String presidentCpf,
        String email,
        String phone,
        String website,
        String instagram,
        String country,
        String state,
        String city,
        String logoUrl,
        String primaryColor,
        String secondaryColor,
        String tertiaryColor,
        String quaternaryColor,
        List<String> colors,
        List<UUID> organizations,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
