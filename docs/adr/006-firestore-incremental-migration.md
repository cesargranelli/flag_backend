# ADR-006: Persistência Dual Incremental — PostgreSQL + Firestore por Domínio

## Status

**Aceito** — 2026-09-04

## Contexto

O ADR-001 definiu Java + Firebase Firestore como arquitetura alvo. Na prática, o backend
hoje é **100% PostgreSQL/JPA/Flyway** (construção estável). Migrar todas as entidades para
Firestore de uma vez (big bang) já foi tentado nas issues #1–#5 e **revertido** (#5) por
quebrar o build e a regra de negócio.

É necessária uma estratégia incremental: manter o PostgreSQL como fonte de verdade padrão e
adotar o Firestore **domínio a domínio**, com infraestrutura preparada, sem mudar regras de
negócio, controllers, JPA ou Flyway.

## Decisão

1. **Persistência dual incremental**: PostgreSQL/JPA continua sendo o default. O Firestore é
   habilitado por domínio através de flags `app.firestore.<domínio>=true` (default `false`).
   Cada issue futura liga UM domínio, implementa `XxxFirestoreRepository` e valida com o
   emulador antes de tocar em produção.
2. **Escrita única via Java REST**: o **cliente web NÃO escreve direto no Firestore**. Toda
   escrita passa pelo backend (Admin SDK). O Firestore é persistência/leitura (realtime) para
   os apps (referee_app, public_app), nunca porta de escrita do cliente.
3. **Admin Web lê somente via REST**: o Admin Web (flag_admin_web) lê listagens/detalhes
   **exclusivamente** pela API REST do backend — exceto **Firebase Auth** para login. Ele NÃO
   consome o Firestore diretamente (nem leitura). O Firestore é espelho de leitura realtime
   **apenas** para os apps (public_app/referee_app); o backend continua sendo a única porta de
   escrita (Admin SDK). Nenhuma etapa futura troca datasource do Admin Web para Firestore.
4. **Padrão porta de repositório**: interface-base genérica
   `br.com.flagplatform.common.firebase.FirestoreRepository<T>` (`findById`, `findAll`,
   `save`, `delete`). Domínios migrados implementam:
   `XxxFirestoreRepository implements XxxRepository, FirestoreRepository<XxxEntity>` com
   `@ConditionalOnProperty(name = "app.firestore.<domínio>", havingValue = "true")`.
   A interface `XxxRepository` (JPA) não muda; a troca de implementação é transparente para o service.
5. **Emulador no perfil dev**: `FirestoreFactory` cria o bean `Firestore` apenas quando
   `app.firestore.enabled=true` (`@ConditionalOnProperty`). Com host de emulador configurado,
   usa `FirestoreOptions.setEmulatorHost(...)` + `EmulatorCredentials` (sem credencial real).
   Sem host, usa a API real com service account (`FIREBASE_SERVICE_ACCOUNT` /
   `GOOGLE_APPLICATION_CREDENTIALS` / ADC).
6. **Regras de segurança Firestore**: leitura pública/autenticada por regras; escrita SOMENTE
   via Admin SDK (Java). Regras a publicar junto com o primeiro domínio migrado.

## Modelagem no Firestore: flat vs aninhada (Issue #10)

Para os domínios migrados até aqui (organization/issue #7, venue/issue #8,
competition/issue #9 e agora conference/division na issue #10), a modelagem adotada é
**flat**: cada domínio tem **coleção própria no nível raiz** (`organizations`, `venues`,
`competitions`, `conferences`, `divisions`) e o documento é identificado pelo
`UUID.toString()` da entidade JPA. Relacionamentos são expressos por **campos de
referência** — a conferência carrega `competitionId`; a divisão carrega `competitionId` e,
opcionalmente, `conferenceId` — nunca por subcoleções aninhadas
(ex.: {`competitions/{id}/conferences/...`}).

**Decisão para conference/division: flat (coleções próprias)**, coerente com o padrão já
estabelecido. Justificativa:

1. **Uniformidade com o padrão existente**: org/venue/competition já usam coleções próprias
   flat; manter conference/division no mesmo formato dá um único padrão de código, regras de
   segurança e consultas nos apps.
2. **Document id universal**: o documento usa o mesmo UUID da entidade JPA (fonte de
   verdade). Em modelagem aninhada, o caminho do documento ficaria subordinado ao id da
   competição, complicando referências cruzadas (uma divisão pertence a uma conferência
   opcional e sempre a uma competição).
3. **Escritas independentes e idempotentes**: conferência e divisão são criadas/atualizadas/
   excluídas individualmente; a modelagem flat espelha cada operação da porta de repositório
   sem reescrever subárvores inteiras (evita gravação em cascata e risco de divergência).
4. **Consultas dos apps**: os apps leem conferências por `competitionId` e divisões por
   `competitionId`/`conferenceId` — no Firestore isso é uma query simples por campo
   (`where("competitionId", "==", id)`), sem composite paths.
5. **Regras de segurança simples**: uma coleção por domínio com leitura pública/autenticada é
   mais fácil de auditar do que regras por caminho aninhado.

**Consequência**: o documento Firestore reflete os mesmos campos camelCase da API REST
(UUIDs como string, timestamps ISO-8601), mantendo o espelho consumível pelos apps
(referee_app/public_app) sem transformação adicional.

## Configuração (env vars)

| Variável | Default | Uso |
|----------|---------|-----|
| `FIRESTORE_ENABLED` | `false` | Liga o Firestore no backend (`app.firestore.enabled`) |
| `FIRESTORE_EMULATOR_HOST` | `localhost:9090` (perfil dev) | Host do emulador (`host:porta`, sem esquema) |
| `FIREBASE_SERVICE_ACCOUNT` | — | Caminho do JSON do service account (prod) |
| `GOOGLE_APPLICATION_CREDENTIALS` | — | Alternativa padrão Google (prod) |
| `FIREBASE_PROJECT_ID` | `flag-platform` | Project ID (emulador/prod sem service account) |
| `GCLOUD_PROJECT` | — | Fallback de project id |

**Porta do emulador**: o default do Firebase Emulator Suite para Firestore é `8080`, mas o
backend roda em `8080` — default em `dev` é **`localhost:9090`** para não conflitar, sempre
sobrescrevível via `FIRESTORE_EMULATOR_HOST`. Subir o emulador:

```bash
firebase emulators:start --only firestore --project flag-platform   # porta 9090 via firebase.json
# ou
firebase emulators:start --only firestore --project flag-platform --port 9090
```

## Não escopo desta issue

- Nenhum domínio foi migrado (flags por domínio permanecem `false`).
- JPA, Flyway, PostgreSQL, controllers, services e regras de negócio inalterados.
- Nenhum segredo no repositório: `firebase-service-account*.json` / `service-account*.json`
  estão no `.gitignore`.

## Estratégia de testes

A Flag Platform **não adota testes unitários/integração dentro das aplicações** (premissa de
produtividade — aceleração da codificação). A validação é feita por **testes e2e fora das
aplicações** (camada de QA dedicada). A garantia técnica desta fase é o build verde:
`./mvnw compile` (CI) e a validação manual com o emulador quando o domínio migrado estiver
ativo (`firebase emulators:start --only firestore --project flag-platform`).

## Consequências

### Positivas

1. Incremental e reversível: cada domínio migra e valida isoladamente.
2. Zero impacto no que está estável (JPA/Postgres).
3. Cliente web nunca escreve direto no Firestore — regras de segurança simples e auditáveis.
4. Real-time/offline possíveis por domínio (referee_app/public_app) quando a flag ligar.

### Negativas

1. Duas persistências para manter durante a transição (escrita dupla por domínio migrado).
2. Firestore por domínio pode criar divergência temporária de dados até a migração completa.
3. Dependência nova (`firebase-admin`) e superfície de configuração por ambiente.

## Referências

- ADR-001: Arquitetura Backend - Java + Firebase (Firestore)
- Issue #6 — feat: infraestrutura Firebase no backend (Admin SDK + emulador + ADR-006)
- [Firestore emulator + Admin SDKs](https://firebase.google.com/docs/emulator-suite/connect_firestore)

## Revisado por

- Tech Lead: Flag Platform
- Data: 2026-09-04