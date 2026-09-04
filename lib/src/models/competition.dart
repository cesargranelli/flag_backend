import 'package:cloud_firestore/cloud_firestore.dart';

/// Temporada de uma organização.
class Season {
  final String id;
  final String organizationId;
  final String name;
  final String sport;
  final DateTime startDate;
  final DateTime? endDate;
  final String status;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const Season({
    required this.id,
    required this.organizationId,
    required this.name,
    required this.sport,
    required this.startDate,
    this.endDate,
    this.status = 'DRAFT',
    required this.createdAt,
    this.updatedAt,
  });

  factory Season.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Season(
      id: doc.id,
      organizationId: data['organizationId'] as String? ?? '',
      name: data['name'] as String? ?? '',
      sport: data['sport'] as String? ?? '',
      startDate: (data['startDate'] as Timestamp?)?.toDate() ?? DateTime.now(),
      endDate: (data['endDate'] as Timestamp?)?.toDate(),
      status: data['status'] as String? ?? 'DRAFT',
      createdAt: (data['createdAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      updatedAt: (data['updatedAt'] as Timestamp?)?.toDate(),
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'organizationId': organizationId,
      'name': name,
      'sport': sport,
      'startDate': Timestamp.fromDate(startDate),
      if (endDate != null) 'endDate': Timestamp.fromDate(endDate!),
      'status': status,
      'createdAt': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
    };
  }

  Season copyWith({
    String? organizationId,
    String? name,
    String? sport,
    DateTime? startDate,
    DateTime? endDate,
    String? status,
  }) {
    return Season(
      id: id,
      organizationId: organizationId ?? this.organizationId,
      name: name ?? this.name,
      sport: sport ?? this.sport,
      startDate: startDate ?? this.startDate,
      endDate: endDate ?? this.endDate,
      status: status ?? this.status,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}

/// Regras de elegibilidade de uma competição.
class EligibilityRules {
  final List<String> allowedGenders;
  final int? minAge;
  final int? maxAge;

  const EligibilityRules({
    this.allowedGenders = const [],
    this.minAge,
    this.maxAge,
  });

  factory EligibilityRules.fromMap(Map<String, dynamic> map) {
    return EligibilityRules(
      allowedGenders: List<String>.from(map['allowedGenders'] ?? []),
      minAge: map['minAge'] as int?,
      maxAge: map['maxAge'] as int?,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'allowedGenders': allowedGenders,
      if (minAge != null) 'minAge': minAge,
      if (maxAge != null) 'maxAge': maxAge,
    };
  }
}

/// Campeonato/competição.
class Competition {
  final String id;
  final String? seasonId;
  final String organizationId;
  final String name;
  final String sport;
  final String? modality;
  final String? gender;
  final String? ageGroup;
  final String? groupingType;
  final String? venueId;
  final DateTime? startDate;
  final DateTime? endDate;
  final String status;
  final EligibilityRules eligibilityRules;
  final DateTime createdAt;
  final DateTime? updatedAt;

  // Dados desnormalizados.
  final String? organizationName;
  final String? seasonName;
  final String? venueName;

  const Competition({
    required this.id,
    this.seasonId,
    required this.organizationId,
    required this.name,
    required this.sport,
    this.modality,
    this.gender,
    this.ageGroup,
    this.groupingType,
    this.venueId,
    this.startDate,
    this.endDate,
    this.status = 'DRAFT',
    this.eligibilityRules = const EligibilityRules(),
    required this.createdAt,
    this.updatedAt,
    this.organizationName,
    this.seasonName,
    this.venueName,
  });

  factory Competition.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Competition(
      id: doc.id,
      seasonId: data['seasonId'] as String?,
      organizationId: data['organizationId'] as String? ?? '',
      name: data['name'] as String? ?? '',
      sport: data['sport'] as String? ?? '',
      modality: data['modality'] as String?,
      gender: data['gender'] as String?,
      ageGroup: data['ageGroup'] as String?,
      groupingType: data['groupingType'] as String?,
      venueId: data['venueId'] as String?,
      startDate: (data['startDate'] as Timestamp?)?.toDate(),
      endDate: (data['endDate'] as Timestamp?)?.toDate(),
      status: data['status'] as String? ?? 'DRAFT',
      eligibilityRules: data['eligibilityRules'] != null
          ? EligibilityRules.fromMap(
              data['eligibilityRules'] as Map<String, dynamic>)
          : const EligibilityRules(),
      createdAt: (data['createdAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      updatedAt: (data['updatedAt'] as Timestamp?)?.toDate(),
      organizationName: data['organizationName'] as String?,
      seasonName: data['seasonName'] as String?,
      venueName: data['venueName'] as String?,
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      if (seasonId != null) 'seasonId': seasonId,
      'organizationId': organizationId,
      'name': name,
      'sport': sport,
      if (modality != null) 'modality': modality,
      if (gender != null) 'gender': gender,
      if (ageGroup != null) 'ageGroup': ageGroup,
      if (groupingType != null) 'groupingType': groupingType,
      if (venueId != null) 'venueId': venueId,
      if (startDate != null) 'startDate': Timestamp.fromDate(startDate!),
      if (endDate != null) 'endDate': Timestamp.fromDate(endDate!),
      'status': status,
      'eligibilityRules': eligibilityRules.toMap(),
      'createdAt': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
      if (organizationName != null) 'organizationName': organizationName,
      if (seasonName != null) 'seasonName': seasonName,
      if (venueName != null) 'venueName': venueName,
    };
  }

  Competition copyWith({
    String? seasonId,
    String? organizationId,
    String? name,
    String? sport,
    String? modality,
    String? gender,
    String? ageGroup,
    String? groupingType,
    String? venueId,
    DateTime? startDate,
    DateTime? endDate,
    String? status,
    EligibilityRules? eligibilityRules,
    String? organizationName,
    String? seasonName,
    String? venueName,
  }) {
    return Competition(
      id: id,
      seasonId: seasonId ?? this.seasonId,
      organizationId: organizationId ?? this.organizationId,
      name: name ?? this.name,
      sport: sport ?? this.sport,
      modality: modality ?? this.modality,
      gender: gender ?? this.gender,
      ageGroup: ageGroup ?? this.ageGroup,
      groupingType: groupingType ?? this.groupingType,
      venueId: venueId ?? this.venueId,
      startDate: startDate ?? this.startDate,
      endDate: endDate ?? this.endDate,
      status: status ?? this.status,
      eligibilityRules: eligibilityRules ?? this.eligibilityRules,
      createdAt: createdAt,
      updatedAt: updatedAt,
      organizationName: organizationName ?? this.organizationName,
      seasonName: seasonName ?? this.seasonName,
      venueName: venueName ?? this.venueName,
    );
  }
}

/// Grupo dentro de uma competição (conferência, divisão, pool).
class CompetitionGroup {
  final String id;
  final String name;
  final String type;
  final int sortOrder;

  const CompetitionGroup({
    required this.id,
    required this.name,
    required this.type,
    required this.sortOrder,
  });

  factory CompetitionGroup.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return CompetitionGroup(
      id: doc.id,
      name: data['name'] as String? ?? '',
      type: data['type'] as String? ?? 'DIVISION',
      sortOrder: data['sortOrder'] as int? ?? 0,
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'name': name,
      'type': type,
      'sortOrder': sortOrder,
    };
  }
}

/// Rodada dentro de uma competição.
class Round {
  final String id;
  final int number;
  final String? name;
  final DateTime? startDate;
  final DateTime? endDate;
  final DateTime createdAt;

  const Round({
    required this.id,
    required this.number,
    this.name,
    this.startDate,
    this.endDate,
    required this.createdAt,
  });

  factory Round.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Round(
      id: doc.id,
      number: data['number'] as int? ?? 0,
      name: data['name'] as String?,
      startDate: (data['startDate'] as Timestamp?)?.toDate(),
      endDate: (data['endDate'] as Timestamp?)?.toDate(),
      createdAt: (data['createdAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'number': number,
      if (name != null) 'name': name,
      if (startDate != null) 'startDate': Timestamp.fromDate(startDate!),
      if (endDate != null) 'endDate': Timestamp.fromDate(endDate!),
      'createdAt': FieldValue.serverTimestamp(),
    };
  }
}

/// Time inscrito em uma competição.
class CompetitionTeam {
  final String id;
  final String teamId;
  final String? groupId;
  final String status;
  final DateTime enrolledAt;

  // Dados desnormalizados.
  final String? teamName;
  final String? teamLogoUrl;

  const CompetitionTeam({
    required this.id,
    required this.teamId,
    this.groupId,
    this.status = 'PENDING',
    required this.enrolledAt,
    this.teamName,
    this.teamLogoUrl,
  });

  factory CompetitionTeam.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return CompetitionTeam(
      id: doc.id,
      teamId: data['teamId'] as String? ?? '',
      groupId: data['groupId'] as String?,
      status: data['status'] as String? ?? 'PENDING',
      enrolledAt:
          (data['enrolledAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      teamName: data['teamName'] as String?,
      teamLogoUrl: data['teamLogoUrl'] as String?,
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'teamId': teamId,
      if (groupId != null) 'groupId': groupId,
      'status': status,
      'enrolledAt': FieldValue.serverTimestamp(),
      if (teamName != null) 'teamName': teamName,
      if (teamLogoUrl != null) 'teamLogoUrl': teamLogoUrl,
    };
  }
}

/// Entrada de elenco (atleta em um CompetitionTeam).
class RosterEntry {
  final String id;
  final String competitionTeamId;
  final String personId;
  final String role;
  final String? jerseyNumber;
  final String? nickname;
  final String eligibilityStatus;
  final String? eligibilityReason;
  final String status;
  final DateTime enrolledAt;

  // Dados desnormalizados.
  final String? personName;
  final String? personPhotoUrl;
  final String? teamName;

  const RosterEntry({
    required this.id,
    required this.competitionTeamId,
    required this.personId,
    required this.role,
    this.jerseyNumber,
    this.nickname,
    this.eligibilityStatus = 'PENDING',
    this.eligibilityReason,
    this.status = 'ACTIVE',
    required this.enrolledAt,
    this.personName,
    this.personPhotoUrl,
    this.teamName,
  });

  factory RosterEntry.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return RosterEntry(
      id: doc.id,
      competitionTeamId: data['competitionTeamId'] as String? ?? '',
      personId: data['personId'] as String? ?? '',
      role: data['role'] as String? ?? 'PLAYER',
      jerseyNumber: data['jerseyNumber'] as String?,
      nickname: data['nickname'] as String?,
      eligibilityStatus:
          data['eligibilityStatus'] as String? ?? 'PENDING',
      eligibilityReason: data['eligibilityReason'] as String?,
      status: data['status'] as String? ?? 'ACTIVE',
      enrolledAt:
          (data['enrolledAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      personName: data['personName'] as String?,
      personPhotoUrl: data['personPhotoUrl'] as String?,
      teamName: data['teamName'] as String?,
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'competitionTeamId': competitionTeamId,
      'personId': personId,
      'role': role,
      if (jerseyNumber != null) 'jerseyNumber': jerseyNumber,
      if (nickname != null) 'nickname': nickname,
      'eligibilityStatus': eligibilityStatus,
      if (eligibilityReason != null) 'eligibilityReason': eligibilityReason,
      'status': status,
      'enrolledAt': FieldValue.serverTimestamp(),
      if (personName != null) 'personName': personName,
      if (personPhotoUrl != null) 'personPhotoUrl': personPhotoUrl,
      if (teamName != null) 'teamName': teamName,
    };
  }
}
