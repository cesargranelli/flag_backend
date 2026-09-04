import 'package:cloud_firestore/cloud_firestore.dart';

import '../models/models.dart';
import 'base_service.dart';

/// Serviço de pessoas (atletas, técnicos, árbitros).
class PersonService extends BaseService<Person> {
  @override
  String get collectionName => 'persons';

  @override
  Person fromFirestore(DocumentSnapshot doc) => Person.fromFirestore(doc);

  @override
  Map<String, dynamic> toFirestore(Person item) => item.toFirestore();

  /// Lista pessoas por role.
  Future<List<Person>> listByRole(PersonRole role) async {
    final snapshot = await collection
        .where('roles', arrayContains: role.name)
        .orderBy('name')
        .get();
    return snapshot.docs.map((doc) => fromFirestore(doc)).toList();
  }

  /// Lista pessoas por status.
  Future<List<Person>> listByStatus(String status) {
    return listWhere('status', status);
  }

  /// Busca pessoa por email.
  Future<Person?> getByEmail(String email) async {
    final snapshot = await collection.where('email', isEqualTo: email).limit(1).get();
    if (snapshot.docs.isEmpty) return null;
    return fromFirestore(snapshot.docs.first);
  }

  /// Escuta pessoas por role.
  Stream<List<Person>> streamByRole(PersonRole role) {
    return collection
        .where('roles', arrayContains: role.name)
        .orderBy('name')
        .snapshots()
        .map((snapshot) =>
            snapshot.docs.map((doc) => fromFirestore(doc)).toList());
  }
}
