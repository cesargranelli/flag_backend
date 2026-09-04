import 'package:cloud_firestore/cloud_firestore.dart';

import '../models/models.dart';
import 'base_service.dart';

/// Serviço de campos de jogo (venues).
class VenueService extends BaseService<Venue> {
  @override
  String get collectionName => 'venues';

  @override
  Venue fromFirestore(DocumentSnapshot doc) => Venue.fromFirestore(doc);

  @override
  Map<String, dynamic> toFirestore(Venue item) => item.toFirestore();

  /// Lista venues por cidade.
  Future<List<Venue>> listByCity(String city) async {
    final snapshot = await collection
        .where('address.city', isEqualTo: city)
        .orderBy('name')
        .get();
    return snapshot.docs.map((doc) => fromFirestore(doc)).toList();
  }
}
