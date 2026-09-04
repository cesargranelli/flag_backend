import 'package:cloud_firestore/cloud_firestore.dart';

import '../models/models.dart';
import 'base_service.dart';

/// Serviço de jogos.
class GameService extends BaseService<Game> {
  @override
  String get collectionName => 'games';

  @override
  Game fromFirestore(DocumentSnapshot doc) => Game.fromFirestore(doc);

  @override
  Map<String, dynamic> toFirestore(Game item) => item.toFirestore();

  /// Lista jogos por competição.
  Future<List<Game>> listByCompetition(String competitionId) {
    return listWhere('competitionId', competitionId);
  }

  /// Lista jogos por round.
  Future<List<Game>> listByRound(String competitionId, String roundId) async {
    final snapshot = await collection
        .where('competitionId', isEqualTo: competitionId)
        .where('roundId', isEqualTo: roundId)
        .orderBy('scheduledAt')
        .get();
    return snapshot.docs.map((doc) => fromFirestore(doc)).toList();
  }

  /// Lista jogos por time (como mandante ou visitante).
  Future<List<Game>> listByTeam(String teamId) async {
    final homeGames = await collection
        .where('homeTeamId', isEqualTo: teamId)
        .orderBy('scheduledAt', descending: true)
        .limit(20)
        .get();
    final awayGames = await collection
        .where('awayTeamId', isEqualTo: teamId)
        .orderBy('scheduledAt', descending: true)
        .limit(20)
        .get();
    final allGames = [...homeGames.docs, ...awayGames.docs];
    allGames.sort((a, b) {
      final aDate = (a.data()['scheduledAt'] as Timestamp?)?.toDate();
      final bDate = (b.data()['scheduledAt'] as Timestamp?)?.toDate();
      return (bDate ?? DateTime(0)).compareTo(aDate ?? DateTime(0));
    });
    return allGames.map((doc) => fromFirestore(doc)).toList();
  }

  /// Lista jogos ativos (SCHEDULED ou IN_PROGRESS).
  Future<List<Game>> listActive() async {
    final snapshot = await collection
        .where('status', whereIn: ['SCHEDULED', 'IN_PROGRESS'])
        .orderBy('scheduledAt')
        .get();
    return snapshot.docs.map((doc) => fromFirestore(doc)).toList();
  }

  /// Lista jogos por venue.
  Future<List<Game>> listByVenue(String venueId) {
    return listWhere('venueId', venueId);
  }

  /// Atualiza status do jogo.
  Future<void> updateStatus(String gameId, GameStatus status) async {
    await collection.doc(gameId).update({
      'status': status.name,
      'updatedAt': FieldValue.serverTimestamp(),
    });
  }

  /// Atualiza placar do jogo.
  Future<void> updateScore(
    String gameId, {
    required int homeScore,
    required int awayScore,
  }) async {
    await collection.doc(gameId).update({
      'homeScore': homeScore,
      'awayScore': awayScore,
      'updatedAt': FieldValue.serverTimestamp(),
    });
  }

  /// Registra início do jogo.
  Future<void> startGame(String gameId) async {
    await collection.doc(gameId).update({
      'status': GameStatus.inProgress.name,
      'actualStartTime': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
    });
  }

  /// Registra fim do jogo.
  Future<void> finishGame(String gameId) async {
    await collection.doc(gameId).update({
      'status': GameStatus.finished.name,
      'actualEndTime': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
    });
  }

  // ── Check-ins ─────────────────────────────────────────────────────

  /// Faz check-in de atleta no jogo.
  Future<CheckIn> checkIn(String gameId, CheckIn checkIn) async {
    final docRef = collection
        .doc(gameId)
        .collection('checkins')
        .doc(checkIn.personId);
    await docRef.set(checkIn.toFirestore());
    return CheckIn.fromFirestore(await docRef.get());
  }

  /// Lista check-ins de um jogo.
  Future<List<CheckIn>> listCheckIns(String gameId) async {
    final snapshot = await collection
        .doc(gameId)
        .collection('checkins')
        .get();
    return snapshot.docs
        .map((doc) => CheckIn.fromFirestore(doc))
        .toList();
  }

  /// Lista check-ins de um jogo por team.
  Future<List<CheckIn>> listCheckInsByTeam(
    String gameId,
    String teamId,
  ) async {
    final snapshot = await collection
        .doc(gameId)
        .collection('checkins')
        .where('teamId', isEqualTo: teamId)
        .get();
    return snapshot.docs
        .map((doc) => CheckIn.fromFirestore(doc))
        .toList();
  }

  // ── Score Events ──────────────────────────────────────────────────

  /// Registra evento de placar.
  Future<ScoreEvent> addScoreEvent(String gameId, ScoreEvent event) async {
    final docRef = collection
        .doc(gameId)
        .collection('scoreEvents')
        .doc();
    await docRef.set(event.toFirestore());
    return ScoreEvent.fromFirestore(await docRef.get());
  }

  /// Lista eventos de placar de um jogo.
  Future<List<ScoreEvent>> listScoreEvents(String gameId) async {
    final snapshot = await collection
        .doc(gameId)
        .collection('scoreEvents')
        .orderBy('timestamp')
        .get();
    return snapshot.docs
        .map((doc) => ScoreEvent.fromFirestore(doc))
        .toList();
  }

  /// Escuta jogos ativos em tempo real (para referee_app).
  Stream<List<Game>> streamActive() {
    return collection
        .where('status', whereIn: ['SCHEDULED', 'IN_PROGRESS'])
        .orderBy('scheduledAt')
        .snapshots()
        .map((snapshot) =>
            snapshot.docs.map((doc) => fromFirestore(doc)).toList());
  }

  /// Escuta jogos de uma competição em tempo real.
  Stream<List<Game>> streamByCompetition(String competitionId) {
    return collection
        .where('competitionId', isEqualTo: competitionId)
        .orderBy('scheduledAt')
        .snapshots()
        .map((snapshot) =>
            snapshot.docs.map((doc) => fromFirestore(doc)).toList());
  }
}
