import 'package:cloud_firestore/cloud_firestore.dart';

import '../models/models.dart';
import 'base_service.dart';

/// Serviço de organizações.
class OrganizationService extends BaseService<Organization> {
  @override
  String get collectionName => 'organizations';

  @override
  Organization fromFirestore(DocumentSnapshot doc) =>
      Organization.fromFirestore(doc);

  @override
  Map<String, dynamic> toFirestore(Organization item) => item.toFirestore();

  /// Lista organizações por tipo.
  Future<List<Organization>> listByType(OrganizationType type) {
    return listWhere('type', type.name);
  }

  /// Lista organizações por status.
  Future<List<Organization>> listByStatus(String status) {
    return listWhere('status', status);
  }

  /// Escuta organizações por tipo.
  Stream<List<Organization>> streamByType(OrganizationType type) {
    return streamWhere('type', type.name);
  }
}
