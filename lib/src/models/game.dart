import 'package:cloud_firestore/cloud_firestore.dart';

import 'enums.dart';

/// Jogo de uma competição.
class Game {
  final String id;
  final String competitionId;
  final String? roundId;
  final String? venueId;
  final String homeTeamId;
  final String awayTeamId;
  final DateTime? scheduledAt;
  final DateTime? actualStartTime;
  final DateTime? actualEndTime;
  final int? homeScore;
  final int? awayScore;
  final GameStatus status;
  final String? notes;
  final DateTime createdAt;
  final DateTime? updatedAt;

  // Dados desnormalizados.
  final String? competitionName;
  final int? roundNumber;
  final String? roundName;
  final String? homeTeamName;
  final String? homeTeamLogoUrl;
  final String? awayTeamName;
  final String? awayTeamLogoUrl;
  final String? venueName;
  final String? venueAddress;

  const Game({
    required this.id,
    required this.competitionId,
    this.roundId,
    this.venueId,
    required this.homeTeamId,
    required this.awayTeamId,
    this.scheduledAt,
    this.actualStartTime,
    this.actualEndTime,
    this.homeScore,
    this.awayScore,
    this.status = GameStatus.scheduled,
    this.notes,
    required this.createdAt,
    this.updatedAt,
    this.competitionName,
    this.roundNumber,
    this.roundName,
    this.homeTeamName,
    this.homeTeamLogoUrl,
    this.awayTeamName,
    this.awayTeamLogoUrl,
    this.venueName,
    this.venueAddress,
  });

  factory Game.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Game(
      id: doc.id,
      competitionId: data['competitionId'] as String? ?? '',
      roundId: data['roundId'] as String?,
      venueId: data['venueId'] as String?,
      homeTeamId: data['homeTeamId'] as String? ?? '',
      awayTeamId: data['awayTeamId'] as String? ?? '',
      scheduledAt: (data['scheduledAt'] as Timestamp?)?.toDate(),
      actualStartTime: (data['actualStartTime'] as Timestamp?)?.toDate(),
      actualEndTime: (data['actualEndTime'] as Timestamp?)?.toDate(),
      homeScore: data['homeScore'] as int?,
      awayScore: data['awayScore'] as int?,
      status: gameStatusFromString(data['status'] as String?) ?? GameStatus.scheduled,
      notes: data['notes'] as String?,
      createdAt: (data['createdAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      updatedAt: (data['updatedAt'] as Timestamp?)?.toDate(),
      competitionName: data['competitionName'] as String?,
      roundNumber: data['roundNumber'] as int?,
      roundName: data['roundName'] as String?,
      homeTeamName: data['homeTeamName'] as String?,
      homeTeamLogoUrl: data['homeTeamLogoUrl'] as String?,
      awayTeamName: data['awayTeamName'] as String?,
      awayTeamLogoUrl: data['awayTeamLogoUrl'] as String?,
      venueName: data['venueName'] as String?,
      venueAddress: data['venueAddress'] as String?,
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'competitionId': competitionId,
      if (roundId != null) 'roundId': roundId,
      if (venueId != null) 'venueId': venueId,
      'homeTeamId': homeTeamId,
      'awayTeamId': awayTeamId,
      if (scheduledAt != null) 'scheduledAt': Timestamp.fromDate(scheduledAt!),
      if (actualStartTime != null)
        'actualStartTime': Timestamp.fromDate(actualStartTime!),
      if (actualEndTime != null)
        'actualEndTime': Timestamp.fromDate(actualEndTime!),
      if (homeScore != null) 'homeScore': homeScore,
      if (awayScore != null) 'awayScore': awayScore,
      'status': status.name,
      if (notes != null) 'notes': notes,
      'createdAt': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
      // Dados desnormalizados.
      if (competitionName != null) 'competitionName': competitionName,
      if (roundNumber != null) 'roundNumber': roundNumber,
      if (roundName != null) 'roundName': roundName,
      if (homeTeamName != null) 'homeTeamName': homeTeamName,
      if (homeTeamLogoUrl != null) 'homeTeamLogoUrl': homeTeamLogoUrl,
      if (awayTeamName != null) 'awayTeamName': awayTeamName,
      if (awayTeamLogoUrl != null) 'awayTeamLogoUrl': awayTeamLogoUrl,
      if (venueName != null) 'venueName': venueName,
      if (venueAddress != null) 'venueAddress': venueAddress,
    };
  }

  Game copyWith({
    String? competitionId,
    String? roundId,
    String? venueId,
    String? homeTeamId,
    String? awayTeamId,
    DateTime? scheduledAt,
    DateTime? actualStartTime,
    DateTime? actualEndTime,
    int? homeScore,
    int? awayScore,
    GameStatus? status,
    String? notes,
    String? competitionName,
    int? roundNumber,
    String? roundName,
    String? homeTeamName,
    String? homeTeamLogoUrl,
    String? awayTeamName,
    String? awayTeamLogoUrl,
    String? venueName,
    String? venueAddress,
  }) {
    return Game(
      id: id,
      competitionId: competitionId ?? this.competitionId,
      roundId: roundId ?? this.roundId,
      venueId: venueId ?? this.venueId,
      homeTeamId: homeTeamId ?? this.homeTeamId,
      awayTeamId: awayTeamId ?? this.awayTeamId,
      scheduledAt: scheduledAt ?? this.scheduledAt,
      actualStartTime: actualStartTime ?? this.actualStartTime,
      actualEndTime: actualEndTime ?? this.actualEndTime,
      homeScore: homeScore ?? this.homeScore,
      awayScore: awayScore ?? this.awayScore,
      status: status ?? this.status,
      notes: notes ?? this.notes,
      createdAt: createdAt,
      updatedAt: updatedAt,
      competitionName: competitionName ?? this.competitionName,
      roundNumber: roundNumber ?? this.roundNumber,
      roundName: roundName ?? this.roundName,
      homeTeamName: homeTeamName ?? this.homeTeamName,
      homeTeamLogoUrl: homeTeamLogoUrl ?? this.homeTeamLogoUrl,
      awayTeamName: awayTeamName ?? this.awayTeamName,
      awayTeamLogoUrl: awayTeamLogoUrl ?? this.awayTeamLogoUrl,
      venueName: venueName ?? this.venueName,
      venueAddress: venueAddress ?? this.venueAddress,
    );
  }
}

/// Check-in de atleta em um jogo.
class CheckIn {
  final String id;
  final String personId;
  final String teamId;
  final DateTime checkedInAt;
  final CheckInStatus status;
  final int? matchNumber;

  // Dados desnormalizados.
  final String? personName;
  final String? personPhotoUrl;
  final String? teamName;

  const CheckIn({
    required this.id,
    required this.personId,
    required this.teamId,
    required this.checkedInAt,
    this.status = CheckInStatus.checkedIn,
    this.matchNumber,
    this.personName,
    this.personPhotoUrl,
    this.teamName,
  });

  factory CheckIn.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return CheckIn(
      id: doc.id,
      personId: data['personId'] as String? ?? '',
      teamId: data['teamId'] as String? ?? '',
      checkedInAt:
          (data['checkedInAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      status: CheckInStatus.values.firstWhere(
        (e) => e.name == data['status'],
        orElse: () => CheckInStatus.checkedIn,
      ),
      matchNumber: data['matchNumber'] as int?,
      personName: data['personName'] as String?,
      personPhotoUrl: data['personPhotoUrl'] as String?,
      teamName: data['teamName'] as String?,
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'personId': personId,
      'teamId': teamId,
      'checkedInAt': FieldValue.serverTimestamp(),
      'status': status.name,
      if (matchNumber != null) 'matchNumber': matchNumber,
      if (personName != null) 'personName': personName,
      if (personPhotoUrl != null) 'personPhotoUrl': personPhotoUrl,
      if (teamName != null) 'teamName': teamName,
    };
  }
}

/// Evento de placar em um jogo.
class ScoreEvent {
  final String id;
  final String personId;
  final ScoreEventType type;
  final String teamId;
  final int? quarter;
  final DateTime? timestamp;

  // Dados desnormalizados.
  final String? personName;
  final String? teamName;

  const ScoreEvent({
    required this.id,
    required this.personId,
    required this.type,
    required this.teamId,
    this.quarter,
    this.timestamp,
    this.personName,
    this.teamName,
  });

  factory ScoreEvent.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return ScoreEvent(
      id: doc.id,
      personId: data['personId'] as String? ?? '',
      type: ScoreEventType.values.firstWhere(
        (e) => e.name == data['type'],
        orElse: () => ScoreEventType.touchdown,
      ),
      teamId: data['teamId'] as String? ?? '',
      quarter: data['quarter'] as int?,
      timestamp: (data['timestamp'] as Timestamp?)?.toDate(),
      personName: data['personName'] as String?,
      teamName: data['teamName'] as String?,
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'personId': personId,
      'type': type.name,
      'teamId': teamId,
      if (quarter != null) 'quarter': quarter,
      if (timestamp != null) 'timestamp': Timestamp.fromDate(timestamp!),
      if (personName != null) 'personName': personName,
      if (teamName != null) 'teamName': teamName,
    };
  }
}

/// Usuário do sistema.
class User {
  final String id;
  final String personId;
  final String email;
  final UserRole role;
  final String status;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const User({
    required this.id,
    required this.personId,
    required this.email,
    this.role = UserRole.organizer,
    this.status = 'PENDING',
    required this.createdAt,
    this.updatedAt,
  });

  factory User.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return User(
      id: doc.id,
      personId: data['personId'] as String? ?? '',
      email: data['email'] as String? ?? '',
      role: UserRole.values.firstWhere(
        (e) => e.name == data['role'],
        orElse: () => UserRole.organizer,
      ),
      status: data['status'] as String? ?? 'PENDING',
      createdAt: (data['createdAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      updatedAt: (data['updatedAt'] as Timestamp?)?.toDate(),
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'personId': personId,
      'email': email,
      'role': role.name,
      'status': status,
      'createdAt': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
    };
  }
}
