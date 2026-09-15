package br.com.flagplatform.storage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static br.com.flagplatform.common.security.SecurityExpressions.ADMIN_OR_ORGANIZER;

@Tag(name = "Upload", description = "Endpoints para upload de arquivos")
public interface FileUploadApi {

    @Operation(
            summary = "Upload de imagem",
            description = "Faz upload de uma imagem (JPEG, PNG ou WebP). Máximo 5MB."
    )
    @PostMapping("/api/v1/upload")
    @PreAuthorize(ADMIN_OR_ORGANIZER)
    ResponseEntity<?> upload(@RequestParam("file") MultipartFile file);
}
