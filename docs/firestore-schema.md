# Firestore Schema - Flag Platform

## Visão Geral

Este documento define a estrutura do banco de dados Firestore para o Flag Platform.

## Collections

### organizations/{orgId}
```
├── name: string
├── tradeName: string (opcional)
├── parentId: string (opcional - referência a outra organização)
├── type: "FEDERATION" | "LEAGUE" | "ASSOCIATION" | "UNIVERSITY" | "CLUB" | "OTHER"
├── document: string (CNPJ/CPF, opcional)
├── logoUrl: string (opcional)
├── primaryColor: string (opcional)
├── secondaryColor: string (opcional)
├── tertiaryColor: string (opcional)
├── quaternaryColor: string (opcional)
├── email: string (opcional)
├── phone: string (opcional)
├── website: string (opcional)
├── instagram: string (opcional)
├── country: string
├── state: string (opcional)
├── city: string (opcional)
├── timezone: string
├── locale: string
├── status: "ACTIVE" | "INACTIVE"
├── createdAt: timestamp
└── updatedAt: timestamp
```

### persons/{personId}
```
├── name: string
├── email: string (opcional)
├── phone: string (opcional)
├── photoUrl: string (opcional)
├── gender: "MALE" | "FEMALE" | "MIXED" (opcional)
├── birthDate: date (opcional)
├── computedAgeGroup: string (opcional - U6, U8, U10, U12, U14, U16, U18, U20, OPEN)
├── roles: array<string> (ATHLETE, COACH, MANAGER, ORGANIZER, REFEREE, ADMIN)
├── status: "ACTIVE" | "INACTIVE"
├── createdAt: timestamp
└── updatedAt: timestamp
```

### users/{userId}
```
├── personId: string (referência a persons)
├── email: string
├── passwordHash: string
├── role: "SUPER_ADMIN" | "ADMIN" | "ORGANIZER" | "REFEREE" | "USER"
├── status: "PENDING" | "ACTIVE" | "REJECTED"
├── createdAt: timestamp
└── updatedAt: timestamp
```

### teams/{teamId}
```
├── organizationId: string (referência a organizations)
├── name: string
├── shortName: string (opcional)
├── logoUrl: string (opcional)
├── sport: string (opcional)
├── divisionId: string (opcional)
├── status: "ACTIVE" | "INACTIVE" | "DISABLED"
├── createdAt: timestamp
├── updatedAt: timestamp
├── organizationName: string (desnormalizado)
└── organizationLogoUrl: string (desnormalizado, opcional)
```

### venues/{venueId}
```
├── name: string
├── logoUrl: string (opcional)
├── address: map {
│   ├── street: string
│   ├── number: string
│   ├── complement: string
│   ├── neighborhood: string
│   ├── city: string
│   ├── state: string
│   ├── country: string
│   ├── zipCode: string
│   ├── lat: number
│   └── lng: number
│   }
├── mapsUrl: string (opcional)
├── createdAt: timestamp
└── updatedAt: timestamp
```

### seasons/{seasonId}
```
├── organizationId: string (referência a organizations)
├── name: string
├── sport: string
├── startDate: date
├── endDate: date (opcional)
├── status: "DRAFT" | "IN_PROGRESS" | "CLOSED"
├── createdAt: timestamp
└── updatedAt: timestamp
```

### competitions/{competitionId}
```
├── seasonId: string (referência a seasons, opcional)
├── organizationId: string (referência a organizations)
├── name: string
├── sport: string
├── modality: string (opcional)
├── gender: "MALE" | "FEMALE" | "MIXED" (opcional)
├── ageGroup: string (opcional)
├── groupingType: "SINGLE_ELIMINATION" | "ROUND_ROBIN" | "SWISS" | "LEAGUE" | "CUSTOM" (opcional)
├── venueId: string (referência a venues, opcional)
├── startDate: date (opcional)
├── endDate: date (opcional)
├── status: "DRAFT" | "REGISTRATION_OPEN" | "IN_PROGRESS" | "FINISHED" | "CANCELLED"
├── eligibilityRules: map {
│   ├── allowedGenders: array<string>
│   ├── minAge: number (opcional)
│   └── maxAge: number (opcional)
│   }
├── createdAt: timestamp
├── updatedAt: timestamp
├── organizationName: string (desnormalizado)
├── seasonName: string (desnormalizado, opcional)
└── venueName: string (desnormalizado, opcional)
```

### games/{gameId}
```
├── competitionId: string (referência a competitions)
├── roundId: string (referência a rounds, opcional)
├── venueId: string (referência a venues, opcional)
├── homeTeamId: string (referência a teams)
├── awayTeamId: string (referência a teams)
├── scheduledAt: timestamp (opcional)
├── actualStartTime: timestamp (opcional)
├── actualEndTime: timestamp (opcional)
├── homeScore: number (opcional)
├── awayScore: number (opcional)
├── status: "SCHEDULED" | "OPENING" | "IN_PROGRESS" | "CONFERENCE" | "FINISHED" | "CANCELLED"
├── notes: string (opcional)
├── createdAt: timestamp
├── updatedAt: timestamp
├── competitionName: string (desnormalizado)
├── roundNumber: number (desnormalizado, opcional)
├── roundName: string (desnormalizado, opcional)
├── homeTeamName: string (desnormalizado)
├── homeTeamLogoUrl: string (desnormalizado, opcional)
├── awayTeamName: string (desnormalizado)
├── awayTeamLogoUrl: string (desnormalizado, opcional)
├── venueName: string (desnormalizado, opcional)
└── venueAddress: string (desnormalizado, opcional)
```

## Subcollections

### games/{gameId}/checkins/{checkinId}
```
├── personId: string (referência a persons)
├── teamId: string (referência a teams)
├── checkedInAt: timestamp
├── status: "PENDING" | "CHECKED_IN" | "ABSENT"
├── matchNumber: number (opcional)
├── personName: string (desnormalizado)
├── personPhotoUrl: string (desnormalizado, opcional)
└── teamName: string (desnormalizado)
```

### games/{gameId}/scoreEvents/{eventId}
```
├── personId: string (referência a persons)
├── type: "TOUCHDOWN" | "FIELD_GOAL" | "SAFETY" | "EXTRA_POINT" | "CONVERSION"
├── teamId: string (referência a teams)
├── quarter: number (opcional)
├── timestamp: timestamp (opcional)
├── personName: string (desnormalizado)
└── teamName: string (desnormalizado)
```

### competitions/{competitionId}/groups/{groupId}
```
├── name: string
├── type: "CONFERENCE" | "DIVISION" | "POOL" | "BRACKET"
└── sortOrder: number
```

### competitions/{competitionId}/rounds/{roundId}
```
├── number: number
├── name: string (opcional)
├── startDate: date (opcional)
├── endDate: date (opcional)
└── createdAt: timestamp
```

### competitions/{competitionId}/competitionTeams/{compTeamId}
```
├── teamId: string (referência a teams)
├── groupId: string (referência a groups, opcional)
├── status: "PENDING" | "APPROVED" | "REJECTED" | "ACTIVE"
├── enrolledAt: timestamp
├── teamName: string (desnormalizado)
└── teamLogoUrl: string (desnormalizado, opcional)
```

### competitions/{competitionId}/roster/{rosterEntryId}
```
├── competitionTeamId: string (referência a competitionTeams)
├── personId: string (referência a persons)
├── role: "PLAYER" | "COACH" | "MANAGER"
├── jerseyNumber: string (opcional)
├── nickname: string (opcional)
├── eligibilityStatus: "PENDING" | "APPROVED" | "REJECTED"
├── eligibilityReason: string (opcional)
├── status: "PENDING" | "APPROVED" | "ACTIVE" | "INACTIVE"
├── enrolledAt: timestamp
├── personName: string (desnormalizado)
├── personPhotoUrl: string (desnormalizado, opcional)
└── teamName: string (desnormalizado)
```

## Índices Compostos Necessários

| Collection | Campos | Direção | Finalidade |
|------------|--------|---------|------------|
| games | competitionId, scheduledAt | ASC, ASC | Lista jogos por competição ordenados por data |
| games | competitionId, roundId | ASC, ASC | Lista jogos por competição + rodada |
| games | homeTeamId, scheduledAt | ASC, ASC | Histórico de jogos do time (mandante) |
| games | awayTeamId, scheduledAt | ASC, ASC | Histórico de jogos do time (visitante) |
| games | venueId, scheduledAt | ASC, ASC | Agenda do venue |
| games | status, scheduledAt | ASC, ASC | Jogos ativos (referee app) |
| competitions | organizationId, status | ASC, ASC | Competições da organização por status |
| competitions | seasonId, status | ASC, ASC | Competições da season |
| teams | organizationId, status | ASC, ASC | Times da organização por status |
| persons | roles (array), status | ARRAY_CONTAINS, ASC | Buscar atletas por role |

## Regras de Segurança (Firestore Rules)

- **Leitura pública**: organizations, persons, games (dados públicos)
- **Escrita autenticada**: apenas ADMIN/ORGANIZER podem criar/editar
- **Check-ins**: apenas REFEREE (MESA) podem escrever
- **Score events**: apenas REFEREE criam, ADMIN corrigem

## Denormalização

Para evitar JOINs no Firestore, os seguintes dados são desnormalizados via Cloud Functions:

| Campo Desnormalizado | Fonte | Target | Trigger |
|---------------------|-------|--------|---------|
| organizationName | organizations.name | teams, venues, competitions | onWrite organization |
| teamName/teamLogoUrl | teams.name/logoUrl | games, competitionTeams, roster | onWrite team |
| personName/personPhotoUrl | persons.name/photoUrl | roster, checkins | onWrite person |
| competitionName | competitions.name | games | onWrite competition |
| venueName | venues.name | games, competitions | onWrite venue |
| roundNumber/roundName | rounds.number/name | games | onWrite round |

## Estimativa de Custos

- ~500 teams, ~5000 athletes, ~100 competições, ~10k jogos
- Custo estimado: ~$1-2/mês (bem abaixo do tier gratuito)
