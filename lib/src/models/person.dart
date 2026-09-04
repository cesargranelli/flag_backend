import 'package:cloud_firestore/cloud_firestore.dart';

import 'enums.dart';

/// Pessoa (atleta, técnico, árbitro, organizador).
///
/// Entidade central que pode ter múltiplos papéis.
class Person {
  final String id;
  final String name;
  final String? email;
  final String? phone;
  final String? photoUrl;
  final Gender? gender;
  final DateTime? birthDate;
  final String? computedAgeGroup;
  final List<PersonRole> roles;
  final String status;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const Person({
    required this.id,
    required this.name,
    this.email,
    this.phone,
    this.photoUrl,
    this.gender,
    this.birthDate,
    this.computedAgeGroup,
    this.roles = const [],
    this.status = 'ACTIVE',
    required this.createdAt,
    this.updatedAt,
  });

  /// Cria Person a partir de documento Firestore.
  factory Person.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Person(
      id: doc.id,
      name: data['name'] as String? ?? '',
      email: data['email'] as String?,
      phone: data['phone'] as String?,
      photoUrl: data['photoUrl'] as String?,
      gender: genderFromString(data['gender'] as String?),
      birthDate: data['birthDate'] != null
          ? (data['birthDate'] as Timestamp).toDate()
          : null,
      computedAgeGroup: data['computedAgeGroup'] as String?,
      roles: (data['roles'] as List<dynamic>?)
              ?.map((r) => PersonRole.values.firstWhere(
                    (e) => e.name == r,
                    orElse: () => PersonRole.athlete,
                  ))
              .toList() ??
          [],
      status: data['status'] as String? ?? 'ACTIVE',
      createdAt: (data['createdAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      updatedAt: (data['updatedAt'] as Timestamp?)?.toDate(),
    );
  }

  /// Converte para Map (Firestore).
  Map<String, dynamic> toFirestore() {
    return {
      'name': name,
      if (email != null) 'email': email,
      if (phone != null) 'phone': phone,
      if (photoUrl != null) 'photoUrl': photoUrl,
      if (gender != null) 'gender': gender!.name,
      if (birthDate != null) 'birthDate': Timestamp.fromDate(birthDate!),
      if (computedAgeGroup != null) 'computedAgeGroup': computedAgeGroup,
      'roles': roles.map((r) => r.name).toList(),
      'status': status,
      'createdAt': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
    };
  }

  /// Calcula ageGroup a partir de birthDate.
  String? calculateAgeGroup() {
    if (birthDate == null) return null;
    final now = DateTime.now();
    final age = now.year - birthDate!.year -
        (now.month < birthDate!.month ||
                (now.month == birthDate!.month && now.day < birthDate!.day)
            ? 1
            : 0);
    if (age <= 6) return 'U6';
    if (age <= 8) return 'U8';
    if (age <= 10) return 'U10';
    if (age <= 12) return 'U12';
    if (age <= 14) return 'U14';
    if (age <= 16) return 'U16';
    if (age <= 18) return 'U18';
    if (age <= 20) return 'U20';
    return 'OPEN';
  }

  Person copyWith({
    String? name,
    String? email,
    String? phone,
    String? photoUrl,
    Gender? gender,
    DateTime? birthDate,
    String? computedAgeGroup,
    List<PersonRole>? roles,
    String? status,
  }) {
    return Person(
      id: id,
      name: name ?? this.name,
      email: email ?? this.email,
      phone: phone ?? this.phone,
      photoUrl: photoUrl ?? this.photoUrl,
      gender: gender ?? this.gender,
      birthDate: birthDate ?? this.birthDate,
      computedAgeGroup: computedAgeGroup ?? this.computedAgeGroup,
      roles: roles ?? this.roles,
      status: status ?? this.status,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
