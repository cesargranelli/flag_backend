import 'package:cloud_firestore/cloud_firestore.dart';

import '../models/models.dart';
import 'base_service.dart';

/// Serviço de competições (inclui Season, Group, Round, CompetitionTeam, Roster).
class CompetitionService extends BaseService<Competition> {
  @override
  String get collectionName => 'competitions';

  @override
  Competition fromFirestore(DocumentSnapshot doc) =>
      Competition.fromFirestore(doc);

  @override
  Map<String, dynamic> toFirestore(Competition item) => item.toFirestore();

  /// Lista competições por organização.
  Future<List<Competition>> listByOrganization(String organizationId) {
    return listWhere('organizationId', organizationId);
  }

  /// Lista competições por season.
  Future<List<Competition>> listBySeason(String seasonId) {
    return listWhere('seasonId', seasonId);
  }

  /// Lista competições por status.
  Future<List<Competition>> listByStatus(String status) {
    return listWhere('status', status);
  }

  // ── Groups ────────────────────────────────────────────────────────

  /// Lista grupos de uma competição.
  Future<List<CompetitionGroup>> listGroups(String competitionId) async {
    final snapshot = await collection
        .doc(competitionId)
        .collection('groups')
        .orderBy('sortOrder')
        .get();
    return snapshot.docs
        .map((doc) => CompetitionGroup.fromFirestore(doc))
        .toList();
  }

  /// Cria grupo em uma competição.
  Future<CompetitionGroup> createGroup(
    String competitionId,
    CompetitionGroup group,
  ) async {
    final docRef =
        collection.doc(competitionId).collection('groups').doc();
    await docRef.set(group.toFirestore());
    return CompetitionGroup.fromFirestore(await docRef.get());
  }

  // ── Rounds ────────────────────────────────────────────────────────

  /// Lista rodadas de uma competição.
  Future<List<Round>> listRounds(String competitionId) async {
    final snapshot = await collection
        .doc(competitionId)
        .collection('rounds')
        .orderBy('number')
        .get();
    return snapshot.docs.map((doc) => Round.fromFirestore(doc)).toList();
  }

  /// Cria rodada em uma competição.
  Future<Round> createRound(String competitionId, Round round) async {
    final docRef =
        collection.doc(competitionId).collection('rounds').doc();
    await docRef.set(round.toFirestore());
    return Round.fromFirestore(await docRef.get());
  }

  // ── Competition Teams ─────────────────────────────────────────────

  /// Inscreve time em uma competição.
  Future<CompetitionTeam> enrollTeam(
    String competitionId,
    CompetitionTeam compTeam,
  ) async {
    final docRef = collection
        .doc(competitionId)
        .collection('competitionTeams')
        .doc(compTeam.teamId);
    await docRef.set(compTeam.toFirestore());
    return CompetitionTeam.fromFirestore(await docRef.get());
  }

  /// Lista times inscritos em uma competição.
  Future<List<CompetitionTeam>> listEnrolledTeams(String competitionId) async {
    final snapshot = await collection
        .doc(competitionId)
        .collection('competitionTeams')
        .get();
    return snapshot.docs
        .map((doc) => CompetitionTeam.fromFirestore(doc))
        .toList();
  }

  /// Atualiza status de inscrição.
  Future<void> updateEnrollmentStatus(
    String competitionId,
    String teamId,
    String status,
  ) async {
    await collection
        .doc(competitionId)
        .collection('competitionTeams')
        .doc(teamId)
        .update({'status': status});
  }

  // ── Roster ────────────────────────────────────────────────────────

  /// Adiciona atleta ao elenco.
  Future<RosterEntry> addRosterEntry(
    String competitionId,
    RosterEntry entry,
  ) async {
    final docRef = collection
        .doc(competitionId)
        .collection('roster')
        .doc();
    await docRef.set(entry.toFirestore());
    return RosterEntry.fromFirestore(await docRef.get());
  }

  /// Lista elenco de uma competição.
  Future<List<RosterEntry>> listRoster(String competitionId) async {
    final snapshot = await collection
        .doc(competitionId)
        .collection('roster')
        .get();
    return snapshot.docs
        .map((doc) => RosterEntry.fromFirestore(doc))
        .toList();
  }

  /// Lista elenco por CompetitionTeam.
  Future<List<RosterEntry>> listRosterByTeam(
    String competitionId,
    String competitionTeamId,
  ) async {
    final snapshot = await collection
        .doc(competitionId)
        .collection('roster')
        .where('competitionTeamId', isEqualTo: competitionTeamId)
        .get();
    return snapshot.docs
        .map((doc) => RosterEntry.fromFirestore(doc))
        .toList();
  }

  /// Valida elegibilidade de um atleta para uma competição.
  bool validateEligibility(Person person, Competition competition) {
    final rules = competition.eligibilityRules;

    // Verificar gênero.
    if (rules.allowedGenders.isNotEmpty && person.gender != null) {
      if (!rules.allowedGenders.contains(person.gender!.name)) {
        return false;
      }
    }

    // Verificar idade.
    if (person.birthDate != null) {
      final now = DateTime.now();
      final age = now.year - person.birthDate!.year -
          (now.month < person.birthDate!.month ||
                  (now.month == person.birthDate!.month &&
                      now.day < person.birthDate!.day)
              ? 1
              : 0);

      if (rules.minAge != null && age < rules.minAge!) {
        return false;
      }
      if (rules.maxAge != null && age > rules.maxAge!) {
        return false;
      }
    }

    return true;
  }
}
