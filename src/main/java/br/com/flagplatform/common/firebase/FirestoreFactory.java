package br.com.flagplatform.common.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.internal.EmulatorCredentials;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fábrica do Firestore (Admin SDK) para persistência dual — PostgreSQL (default)
 * + Firestore por domínio. Ver ADR-006.
 *
 * <p>O bean {@code Firestore} só é criado quando a flag {@code app.firestore.enabled}
 * é {@code true} ({@link ConditionalOnProperty}). Enquanto nenhum domínio for migrado,
 * a flag fica {@code false} e nenhuma inicialização do Firebase acontece.
 *
 * <h2>Configuração por ambiente</h2>
 * <ul>
 *   <li><b>Emulador local (perfil {@code dev})</b>: ativado quando o host do emulador é
 *       resolvido. Resolução em ordem: propriedade {@code app.firestore.emulator-host}
 *       (application-dev.yml → {@code ${FIRESTORE_EMULATOR_HOST:localhost:9090}}) e depois
 *       a env var {@code FIRESTORE_EMULATOR_HOST}. Neste modo as credenciais são fictícias
 *       ({@link EmulatorCredentials}) e o host é passado via
 *       {@link FirestoreOptions.Builder#setEmulatorHost(String)}.</li>
 *   <li><b>API real do Firebase (prod)</b>: sem host de emulador. Credenciais vindas de
 *       {@code FIREBASE_SERVICE_ACCOUNT} (caminho do JSON), {@code GOOGLE_APPLICATION_CREDENTIALS}
 *       ou Application Default Credentials.</li>
 *   <li><b>Perfil {@code local}</b>: permanece somente PostgreSQL — a flag
 *       {@code app.firestore.enabled} fica {@code false} (application-local.yml).</li>
 * </ul>
 *
 * <p>O projeto id é resolvido de {@code app.firestore.project-id} (env
 * {@code FIREBASE_PROJECT_ID}), do campo {@code project_id} do JSON do service account,
 * da env {@code GCLOUD_PROJECT} ou, como último recurso, do default {@code flag-platform}.
 *
 * <p>Observação: {@code FirestoreOptions} é construído explicitamente (e não via
 * {@code FirestoreOptions.getDefaultInstance()}) para não acoplar a instância ao
 * {@code FirebaseApp} default nem a estado estático entre contextos.
 */
@Slf4j
@Configuration
public class FirestoreFactory {

    public static final String PROP_ENABLED = "app.firestore.enabled";
    public static final String PROP_EMULATOR_HOST = "app.firestore.emulator-host";
    public static final String PROP_PROJECT_ID = "app.firestore.project-id";

    public static final String ENV_FIRESTORE_EMULATOR_HOST = "FIRESTORE_EMULATOR_HOST";
    public static final String ENV_FIREBASE_SERVICE_ACCOUNT = "FIREBASE_SERVICE_ACCOUNT";
    public static final String ENV_GOOGLE_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS";
    public static final String ENV_GCLOUD_PROJECT = "GCLOUD_PROJECT";

    private static final String DEFAULT_PROJECT_ID = "flag-platform";

    /**
     * Expõe o {@link Firestore} do Admin SDK. Bean condicionado à flag
     * {@code app.firestore.enabled=true} — sem a flag, o Firebase não é tocado.
     */
    @Bean
    @ConditionalOnProperty(name = PROP_ENABLED, havingValue = "true")
    public Firestore firestore(Environment environment) throws IOException {
        String emulatorHost = resolveEmulatorHost(environment);
        GoogleCredentials credentials = resolveCredentials(emulatorHost);
        String projectId = resolveProjectId(environment, credentials);

        FirestoreOptions.Builder optionsBuilder = FirestoreOptions.newBuilder()
                .setProjectId(projectId);

        if (emulatorHost != null) {
            // Credenciais do Firebase ficam fictícias; o FirestoreOptions cuida do
            // endpoint (plaintext + EmulatorCredentials) internamente.
            optionsBuilder.setEmulatorHost(emulatorHost);
            log.info("Firestore conectado ao EMULADOR local em {}", emulatorHost);
        } else {
            optionsBuilder.setCredentials(credentials);
            log.info("Firestore conectado ao Firebase Cloud (projectId={})", projectId);
        }

        FirestoreOptions firestoreOptions = optionsBuilder.build();

        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .setFirestoreOptions(firestoreOptions)
                .build();

        FirebaseApp app = FirebaseApp.getApps().stream()
                .filter(candidate -> candidate.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                .findFirst()
                .orElseGet(() -> FirebaseApp.initializeApp(firebaseOptions));

        return firestoreOptions.getService();
    }

    /**
     * Resolve o host do emulador: propriedade {@code app.firestore.emulator-host} primeiro,
     * depois a env var {@code FIRESTORE_EMULATOR_HOST}. Retorna {@code null} quando nenhum
     * host é configurado (modo API real).
     */
    String resolveEmulatorHost(Environment environment) {
        String fromProperty = environment.getProperty(PROP_EMULATOR_HOST);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return normalizeHost(fromProperty);
        }
        String fromEnv = System.getenv(ENV_FIRESTORE_EMULATOR_HOST);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return normalizeHost(fromEnv);
        }
        return null;
    }

    private String normalizeHost(String host) {
        String normalized = host.trim();
        int scheme = normalized.indexOf("://");
        return scheme >= 0 ? normalized.substring(scheme + 3) : normalized;
    }

    /**
     * Em modo emulador usa {@link EmulatorCredentials} (o emulador ignora autenticação).
     * Em produção: {@code FIREBASE_SERVICE_ACCOUNT} primeiro, depois
     * {@code GOOGLE_APPLICATION_CREDENTIALS}, e por fim Application Default Credentials.
     */
    GoogleCredentials resolveCredentials(String emulatorHost) throws IOException {
        if (emulatorHost != null) {
            return new EmulatorCredentials();
        }
        String serviceAccountPath = firstNonBlank(
                System.getenv(ENV_FIREBASE_SERVICE_ACCOUNT),
                System.getenv(ENV_GOOGLE_APPLICATION_CREDENTIALS));
        if (serviceAccountPath != null) {
            Path path = Path.of(serviceAccountPath);
            if (!Files.exists(path)) {
                throw new IllegalStateException(
                        "Service account do Firebase não encontrado em '" + path
                                + "'. Configure FIREBASE_SERVICE_ACCOUNT ou GOOGLE_APPLICATION_CREDENTIALS.");
            }
            try (FileInputStream input = new FileInputStream(path.toFile())) {
                return GoogleCredentials.fromStream(input);
            }
        }
        return GoogleCredentials.getApplicationDefault();
    }

    /**
     * Resolve o project id, em ordem: propriedade {@code app.firestore.project-id}
     * ({@code FIREBASE_PROJECT_ID}), campo {@code project_id} do JSON do service account,
     * env {@code GCLOUD_PROJECT} e default {@code flag-platform}.
     */
    String resolveProjectId(Environment environment, GoogleCredentials credentials) {
        String configured = environment.getProperty(PROP_PROJECT_ID);
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        String projectFromCredentials = projectIdFromServiceAccountFile();
        if (projectFromCredentials != null) {
            return projectFromCredentials;
        }
        String envProject = System.getenv(ENV_GCLOUD_PROJECT);
        if (envProject != null && !envProject.isBlank()) {
            return envProject;
        }
        return DEFAULT_PROJECT_ID;
    }

    private String projectIdFromServiceAccountFile() {
        String serviceAccountPath = firstNonBlank(
                System.getenv(ENV_FIREBASE_SERVICE_ACCOUNT),
                System.getenv(ENV_GOOGLE_APPLICATION_CREDENTIALS));
        if (serviceAccountPath == null) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(Path.of(serviceAccountPath))) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("project_id") && !json.get("project_id").isJsonNull()) {
                return json.get("project_id").getAsString();
            }
        } catch (IOException | RuntimeException e) {
            log.warn("Não foi possível ler project_id do service account '{}': {}", serviceAccountPath, e.getMessage());
        }
        return null;
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