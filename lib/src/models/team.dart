import 'package:cloud_firestore/cloud_firestore.dart';

import 'enums.dart';

/// Time de uma organização.
class Team {
  final String id;
  final String organizationId;
  final String name;
  final String? shortName;
  final String? logoUrl;
  final String? sport;
  final String? divisionId;
  final String status;
  final DateTime createdAt;
  final DateTime? updatedAt;

  // Dados desnormalizados (para leitura).
  final String? organizationName;
  final String? organizationLogoUrl;

  const Team({
    required this.id,
    required this.organizationId,
    required this.name,
    this.shortName,
    this.logoUrl,
    this.sport,
    this.divisionId,
    this.status = 'ACTIVE',
    required this.createdAt,
    this.updatedAt,
    this.organizationName,
    this.organizationLogoUrl,
  });

  /// Cria Team a partir de documento Firestore.
  factory Team.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Team(
      id: doc.id,
      organizationId: data['organizationId'] as String? ?? '',
      name: data['name'] as String? ?? '',
      shortName: data['shortName'] as String?,
      logoUrl: data['logoUrl'] as String?,
      sport: data['sport'] as String?,
      divisionId: data['divisionId'] as String?,
      status: data['status'] as String? ?? 'ACTIVE',
      createdAt: (data['createdAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      updatedAt: (data['updatedAt'] as Timestamp?)?.toDate(),
      organizationName: data['organizationName'] as String?,
      organizationLogoUrl: data['organizationLogoUrl'] as String?,
    );
  }

  /// Converte para Map (Firestore).
  Map<String, dynamic> toFirestore() {
    return {
      'organizationId': organizationId,
      'name': name,
      if (shortName != null) 'shortName': shortName,
      if (logoUrl != null) 'logoUrl': logoUrl,
      if (sport != null) 'sport': sport,
      if (divisionId != null) 'divisionId': divisionId,
      'status': status,
      'createdAt': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
      // Dados desnormalizados.
      if (organizationName != null) 'organizationName': organizationName,
      if (organizationLogoUrl != null)
        'organizationLogoUrl': organizationLogoUrl,
    };
  }

  Team copyWith({
    String? organizationId,
    String? name,
    String? shortName,
    String? logoUrl,
    String? sport,
    String? divisionId,
    String? status,
    String? organizationName,
    String? organizationLogoUrl,
  }) {
    return Team(
      id: id,
      organizationId: organizationId ?? this.organizationId,
      name: name ?? this.name,
      shortName: shortName ?? this.shortName,
      logoUrl: logoUrl ?? this.logoUrl,
      sport: sport ?? this.sport,
      divisionId: divisionId ?? this.divisionId,
      status: status ?? this.status,
      createdAt: createdAt,
      updatedAt: updatedAt,
      organizationName: organizationName ?? this.organizationName,
      organizationLogoUrl: organizationLogoUrl ?? this.organizationLogoUrl,
    );
  }
}
