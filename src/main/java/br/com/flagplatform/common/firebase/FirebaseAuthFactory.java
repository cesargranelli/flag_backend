package br.com.flagplatform.common.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.internal.EmulatorCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuração do Firebase Auth (Admin SDK) para verificação de ID tokens.
 *
 * <p>Reusa o {@link FirebaseApp} default já inicializado por {@link FirestoreFactory}
 * (via dependência explícita no bean {@code FirebaseAuth}). As credenciais do service
 * account cobrem todos os serviços Firebase, então nenhum ajuste de credenciais é
 * necessário aqui.
 *
 * <p>Bean ativado apenas quando {@code app.auth.firebase-enabled=true}.
 *
 * <h2>Variáveis de ambiente</h2>
 * <ul>
 *   <li>{@code FIREBASE_PROJECT_ID} — project ID do Firebase (default: {@code flag-platform})</li>
 *   <li>{@code FIREBASE_SERVICE_ACCOUNT} — caminho do JSON do service account</li>
 *   <li>{@code GOOGLE_APPLICATION_CREDENTIALS} — idem, ordem alternativa</li>
 * </ul>
 *
 * @see FirebaseAuthFactory#firebaseAuth(Environment)
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = FirebaseAuthFactory.PROP_ENABLED, havingValue = "true")
public class FirebaseAuthFactory {

    public static final String PROP_ENABLED = "app.auth.firebase-enabled";
    public static final String PROP_PROJECT_ID = "app.firestore.project-id";

    public static final String ENV_FIREBASE_PROJECT_ID = "FIREBASE_PROJECT_ID";
    public static final String ENV_FIREBASE_SERVICE_ACCOUNT = "FIREBASE_SERVICE_ACCOUNT";
    public static final String ENV_GOOGLE_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS";

    private static final String DEFAULT_PROJECT_ID = "flag-platform";

    /**
     * Expõe o {@link FirebaseAuth} do Admin SDK. Depende do bean {@code Firestore}
     * para garantir que o {@link FirebaseApp} default já foi inicializado.
     *
     * @param firestore dummy parameter apenas para forçar ordem de inicialização;
     *                  {@link FirebaseApp} é singletons por app name.
     * @return {@code FirebaseAuth} para o app default
     */
    @Bean
    @ConditionalOnMissingBean
    public FirebaseAuth firebaseAuth(
            Environment environment,
            @Qualifier("firestore") Object firestore) {

        String projectId = resolveProjectId(environment);
        GoogleCredentials credentials;
        try {
            credentials = resolveCredentials();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Falha ao carregar credenciais do Firebase Auth: " + e.getMessage(), e);
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build();

        FirebaseApp app = FirebaseApp.getApps().stream()
                .filter(candidate -> candidate.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                .findFirst()
                .orElseGet(() -> {
                    log.info("Inicializando FirebaseApp default (Auth), projectId={}", projectId);
                    return FirebaseApp.initializeApp(options);
                });

        log.info("Firebase Auth configurado (projectId={})", projectId);
        return FirebaseAuth.getInstance(app);
    }

    private String resolveProjectId(Environment environment) {
        String configured = environment.getProperty(PROP_PROJECT_ID);
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        String fromEnv = System.getenv(ENV_FIREBASE_PROJECT_ID);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return DEFAULT_PROJECT_ID;
    }

    private GoogleCredentials resolveCredentials() throws IOException {
        String serviceAccountPath = firstNonBlank(
                System.getenv(ENV_FIREBASE_SERVICE_ACCOUNT),
                System.getenv(ENV_GOOGLE_APPLICATION_CREDENTIALS));

        if (serviceAccountPath != null) {
            Path path = Path.of(serviceAccountPath);
            if (Files.exists(path)) {
                try (FileInputStream input = new FileInputStream(path.toFile())) {
                    return GoogleCredentials.fromStream(input);
                }
            }
        }
        return GoogleCredentials.getApplicationDefault();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
