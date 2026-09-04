import 'package:cloud_firestore/cloud_firestore.dart';

import '../models/models.dart';
import 'base_service.dart';

/// Serviço de times.
class TeamService extends BaseService<Team> {
  @override
  String get collectionName => 'teams';

  @override
  Team fromFirestore(DocumentSnapshot doc) => Team.fromFirestore(doc);

  @override
  Map<String, dynamic> toFirestore(Team item) => item.toFirestore();

  /// Lista times por organização.
  Future<List<Team>> listByOrganization(String organizationId) {
    return listWhere('organizationId', organizationId);
  }

  /// Lista times por status.
  Future<List<Team>> listByStatus(String status) {
    return listWhere('status', status);
  }

  /// Escuta times por organização.
  Stream<List<Team>> streamByOrganization(String organizationId) {
    return streamWhere('organizationId', organizationId);
  }

  /// Adiciona membro ao time.
  Future<void> addMember(
    String teamId,
    String personId,
    String role,
  ) async {
    await collection
        .doc(teamId)
        .collection('members')
        .doc(personId)
        .set({
      'personId': personId,
      'role': role,
      'startDate': FieldValue.serverTimestamp(),
      'status': 'ACTIVE',
    });
  }

  /// Remove membro do time.
  Future<void> removeMember(String teamId, String personId) async {
    await collection.doc(teamId).collection('members').doc(personId).delete();
  }

  /// Lista membros do time.
  Future<List<Map<String, dynamic>>> listMembers(String teamId) async {
    final snapshot = await collection
        .doc(teamId)
        .collection('members')
        .where('status', isEqualTo: 'ACTIVE')
        .get();
    return snapshot.docs.map((doc) => doc.data()).toList();
  }
}
