package br.com.flagplatform.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Configuração resiliente do Firebase Admin SDK.
 * <p>
 * Inicializa o {@link FirebaseApp} se houver credenciais disponíveis (via property,
 * variável de ambiente ou Application Default Credentials). Se nenhuma credencial
 * for encontrada (ex: desenvolvimento local offline), não quebra o startup e permite
 * que a aplicação opere com autenticação em modo tolerante/legado.
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${app.firebase.credentials:${FIREBASE_CREDENTIALS:}}")
    private String credentials;

    @Value("${app.firebase.project-id:${FIREBASE_PROJECT_ID:}}")
    private String projectId;

    @Bean
    public FirebaseApp firebaseApp() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        try {
            GoogleCredentials googleCredentials = resolveCredentials();
            if (googleCredentials == null) {
                log.warn("Firebase credentials not configured. FirebaseApp will not be initialized (operating in dev/mock mode).");
                return null;
            }

            FirebaseOptions.Builder builder = FirebaseOptions.builder()
                    .setCredentials(googleCredentials);

            if (projectId != null && !projectId.isBlank()) {
                builder.setProjectId(projectId.trim());
            }

            FirebaseApp app = FirebaseApp.initializeApp(builder.build());
            log.info("FirebaseApp initialized successfully.");
            return app;
        } catch (Exception ex) {
            log.warn("Failed to initialize FirebaseApp: {}. Application will continue without Firebase Admin SDK.", ex.getMessage());
            return null;
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(@Autowired(required = false) FirebaseApp firebaseApp) {
        if (firebaseApp == null) {
            log.debug("FirebaseApp is null, FirebaseAuth bean will not be initialized.");
            return null;
        }
        try {
            return FirebaseAuth.getInstance(firebaseApp);
        } catch (Exception ex) {
            log.warn("Failed to get FirebaseAuth instance: {}", ex.getMessage());
            return null;
        }
    }

    private GoogleCredentials resolveCredentials() {
        log.info("Resolving Firebase credentials...");
        log.info("  app.firebase.credentials = '{}'", credentials != null ? credentials : "(vazio)");
        log.info("  FIREBASE_CREDENTIALS env = '{}'", System.getenv("FIREBASE_CREDENTIALS"));
        log.info("  GOOGLE_APPLICATION_CREDENTIALS env = '{}'",
                System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        if (credentials != null && !credentials.isBlank()) {
            String trimmed = credentials.trim();
            // 1. JSON inline
            if (trimmed.startsWith("{")) {
                try {
                    return GoogleCredentials.fromStream(
                            new ByteArrayInputStream(trimmed.getBytes(StandardCharsets.UTF_8)));
                } catch (Exception e) {
                    log.warn("Failed to parse inline Firebase credentials JSON: {}", e.getMessage());
                }
            }

            // 2. Classpath resource
            if (trimmed.startsWith("classpath:")) {
                String path = trimmed.substring("classpath:".length());
                try {
                    ClassPathResource resource = new ClassPathResource(path);
                    if (resource.exists()) {
                        try (InputStream is = resource.getInputStream()) {
                            return GoogleCredentials.fromStream(is);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to read classpath Firebase credentials {}: {}", path, e.getMessage());
                }
            }

            // 3. File path no filesystem
            try {
                File file = new File(trimmed);
                if (file.exists() && file.isFile()) {
                    try (InputStream is = new FileInputStream(file)) {
                        return GoogleCredentials.fromStream(is);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to read file Firebase credentials {}: {}", trimmed, e.getMessage());
            }
        }

        // 4. Fallback: Application Default Credentials (GOOGLE_APPLICATION_CREDENTIALS)
        try {
            return GoogleCredentials.getApplicationDefault();
        } catch (Exception e) {
            log.debug("Application Default Credentials not available: {}", e.getMessage());
        }

        return null;
    }
}
