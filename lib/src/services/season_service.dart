import '../models/models.dart';
import 'base_service.dart';

/// Serviço de temporadas.
class SeasonService extends BaseService<Season> {
  @override
  String get collectionName => 'seasons';

  @override
  Season fromFirestore(dynamic doc) => Season.fromFirestore(doc);

  @override
  Map<String, dynamic> toFirestore(Season item) => item.toFirestore();

  /// Lista temporadas por organização.
  Future<List<Season>> listByOrganization(String organizationId) {
    return listWhere('organizationId', organizationId);
  }

  /// Lista temporadas por esporte.
  Future<List<Season>> listBySport(String sport) {
    return listWhere('sport', sport);
  }

  /// Escuta temporadas por organização.
  Stream<List<Season>> streamByOrganization(String organizationId) {
    return streamWhere('organizationId', organizationId);
  }
}
