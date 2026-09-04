import 'package:cloud_firestore/cloud_firestore.dart';

import 'enums.dart';

/// Organização (federação, liga, clube, universidade).
class Organization {
  final String id;
  final String name;
  final String? tradeName;
  final String? parentId;
  final OrganizationType? type;
  final String? document;
  final String? logoUrl;
  final String? primaryColor;
  final String? secondaryColor;
  final String? tertiaryColor;
  final String? quaternaryColor;
  final String? email;
  final String? phone;
  final String? website;
  final String? instagram;
  final String? country;
  final String? state;
  final String? city;
  final String? timezone;
  final String? locale;
  final String status;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const Organization({
    required this.id,
    required this.name,
    this.tradeName,
    this.parentId,
    this.type,
    this.document,
    this.logoUrl,
    this.primaryColor,
    this.secondaryColor,
    this.tertiaryColor,
    this.quaternaryColor,
    this.email,
    this.phone,
    this.website,
    this.instagram,
    this.country,
    this.state,
    this.city,
    this.timezone,
    this.locale,
    this.status = 'ACTIVE',
    required this.createdAt,
    this.updatedAt,
  });

  /// Cria Organization a partir de documento Firestore.
  factory Organization.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Organization(
      id: doc.id,
      name: data['name'] as String? ?? '',
      tradeName: data['tradeName'] as String?,
      parentId: data['parentId'] as String?,
      type: _typeFromString(data['type'] as String?),
      document: data['document'] as String?,
      logoUrl: data['logoUrl'] as String?,
      primaryColor: data['primaryColor'] as String?,
      secondaryColor: data['secondaryColor'] as String?,
      tertiaryColor: data['tertiaryColor'] as String?,
      quaternaryColor: data['quaternaryColor'] as String?,
      email: data['email'] as String?,
      phone: data['phone'] as String?,
      website: data['website'] as String?,
      instagram: data['instagram'] as String?,
      country: data['country'] as String?,
      state: data['state'] as String?,
      city: data['city'] as String?,
      timezone: data['timezone'] as String?,
      locale: data['locale'] as String?,
      status: data['status'] as String? ?? 'ACTIVE',
      createdAt: (data['createdAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      updatedAt: (data['updatedAt'] as Timestamp?)?.toDate(),
    );
  }

  /// Converte para Map (Firestore).
  Map<String, dynamic> toFirestore() {
    return {
      'name': name,
      if (tradeName != null) 'tradeName': tradeName,
      if (parentId != null) 'parentId': parentId,
      if (type != null) 'type': type!.name,
      if (document != null) 'document': document,
      if (logoUrl != null) 'logoUrl': logoUrl,
      if (primaryColor != null) 'primaryColor': primaryColor,
      if (secondaryColor != null) 'secondaryColor': secondaryColor,
      if (tertiaryColor != null) 'tertiaryColor': tertiaryColor,
      if (quaternaryColor != null) 'quaternaryColor': quaternaryColor,
      if (email != null) 'email': email,
      if (phone != null) 'phone': phone,
      if (website != null) 'website': website,
      if (instagram != null) 'instagram': instagram,
      if (country != null) 'country': country,
      if (state != null) 'state': state,
      if (city != null) 'city': city,
      if (timezone != null) 'timezone': timezone,
      if (locale != null) 'locale': locale,
      'status': status,
      'createdAt': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
    };
  }

  static OrganizationType? _typeFromString(String? value) {
    if (value == null) return null;
    return OrganizationType.values.firstWhere(
      (e) => e.name == value,
      orElse: () => OrganizationType.other,
    );
  }

  Organization copyWith({
    String? name,
    String? tradeName,
    String? parentId,
    OrganizationType? type,
    String? document,
    String? logoUrl,
    String? primaryColor,
    String? secondaryColor,
    String? tertiaryColor,
    String? quaternaryColor,
    String? email,
    String? phone,
    String? website,
    String? instagram,
    String? country,
    String? state,
    String? city,
    String? timezone,
    String? locale,
    String? status,
  }) {
    return Organization(
      id: id,
      name: name ?? this.name,
      tradeName: tradeName ?? this.tradeName,
      parentId: parentId ?? this.parentId,
      type: type ?? this.type,
      document: document ?? this.document,
      logoUrl: logoUrl ?? this.logoUrl,
      primaryColor: primaryColor ?? this.primaryColor,
      secondaryColor: secondaryColor ?? this.secondaryColor,
      tertiaryColor: tertiaryColor ?? this.tertiaryColor,
      quaternaryColor: quaternaryColor ?? this.quaternaryColor,
      email: email ?? this.email,
      phone: phone ?? this.phone,
      website: website ?? this.website,
      instagram: instagram ?? this.instagram,
      country: country ?? this.country,
      state: state ?? this.state,
      city: city ?? this.city,
      timezone: timezone ?? this.timezone,
      locale: locale ?? this.locale,
      status: status ?? this.status,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
