# ADR-001: Arquitetura Backend - Java + Firebase (Firestore)

## Status

**Aceito** — Implementado em 2026-09-04

## Contexto

O Flag Platform precisa de uma arquitetura backend que suporte:
- Regras de negócio complexas (eligibility, standings, game flow)
- Persistência de dados (banco de dados relacional ou NoSQL)
- Autenticação de usuários
- Uploads de imagens
- Atualizações em tempo real (referee_app)
- Offline para apps mobile (referee_app, public_app)

### Opções Consideradas

| Opção | Descrição |
|-------|-----------|
| **A** | Java + PostgreSQL (backend completo) |
| **B** | Firebase Firestore + Cloud Functions (serverless) |
| **C** | Java + Firebase Firestore (híbrido) |

## Decisão

**Opção C: Java + Firebase Firestore**

## Justificativa

### Por que não Java + PostgreSQL (Opção A)?

| Problema | Impacto |
|----------|---------|
| Gerenciamento de infraestrutura | Alta complexidade operacional |
| Não suporta offline nativamente | Impossível para referee_app e public_app |
| Não suporta real-time nativamente | Requer WebSocket manual |
| Autenticação manual | Mais código para manter |

### Por que não Firebase puro (Opção B)?

| Problema | Impacto |
|----------|---------|
| Cloud Functions limitado para lógica complexa | Dificuldade para eligibility, standings |
| Vendor lock-in total | Depende 100% do Google |
| Debug mais difícil | Sem IDE Java completo |
| Menos flexibilidade | Limitações de Cloud Functions |

### Por que Java + Firebase (Opção C)?

| Vantagem | Benefício |
|----------|-----------|
| Java cuida da lógica complexa | Eligibility, standings, game flow |
| Firebase cuida da infraestrutura | Firestore, Storage, Auth |
| Flutter apenas consome | Chama APIs Java, usa Firebase Auth |
| Real-time grátis | Firestore snapshots para referee_app |
| Offline grátis | Firestore persistence para mobile |
| Autenticação simples | Firebase Auth resolve tudo |
| Menos infraestrutura | Não precisa gerenciar PostgreSQL |

## Fluxo de Dados

```
Flag Admin Web (flag_admin_web)
    │
    ├──→ Firebase Auth (login/logout — ÚNICO acesso direto ao Firebase)
    │
    └──→ Java API REST (todas as demais operações — listagens, detalhes e escrita)
              │
              ├──→ Regras de negócio (eligibility, standings)
              │
              └──→ Firebase Firestore (leitura/escrita via backend/Admin SDK)
                        │
                        └──→ Firebase Storage (imagens)

App do torcedor / árbitro (public_app, referee_app)
    │
    ├──→ Firebase Auth (login)
    ├──→ Firebase Firestore (leitura realtime — via espelho do backend)
    │
    └──→ Java API REST (operações de escrita)
```

> **Regra (2026-09-05):** o Admin Web acessa o Firebase **somente para autenticação**
> (Firebase Auth). **Todos os demais cenários passam pelo backend (REST) sempre** — o Admin
> Web nunca lê nem escreve no Firestore diretamente. O Firestore é espelho de leitura realtime
> **apenas** para os apps (public_app/referee_app); o backend segue como única porta de escrita.

## Consequências

### Positivas

1. **Separação clara**: Java = lógica, Firebase = infraestrutura
2. **Menos infraestrutura**: Não precisa gerenciar PostgreSQL
3. **Real-time grátis**: Firestore snapshots para o referee_app
4. **Offline grátis**: Firestore persistence para mobile
5. **Autenticação simples**: Firebase Auth resolve tudo
6. **Java flexível**: Regras de negócio complexas em Java (não em Cloud Functions)

### Negativas

1. **Duas plataformas**: Java + Firebase para manter
2. **Vendor lock-in parcial**: Firestore é Google-specific
3. **Complexidade inicial**: Configurar ambos os sistemas

## Estrutura de Pastas

```
flag_admin_web/          ← Flutter (frontend)
├── lib/src/
│   ├── core/            ← compartilhado (widgets, theme, utils)
│   ├── features/        ← por domínio (MVVM)
│   │   ├── organizations/
│   │   ├── teams/
│   │   ├── athletes/
│   │   └── ...
│   ├── config/
│   ├── providers/
│   ├── router/
│   └── utils/

flag_backend/            ← Java/Spring Boot (backend)
├── src/main/java/       ← código Java
├── src/main/resources/  ← migrations, configs
├── pom.xml              ← dependências Maven
└── mvnw                 ← Maven wrapper
```

## Referências

- [Flutter Architecture Guide](https://docs.flutter.dev/app-architecture/guide)
- [Firebase for Flutter](https://firebase.google.com/docs/flutter/setup)
- [Spring Boot](https://spring.io/projects/spring-boot)

## Revisado por

- Tech Lead: [Nome]
- Data: 2026-09-04
