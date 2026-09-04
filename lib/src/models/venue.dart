import 'package:cloud_firestore/cloud_firestore.dart';

/// Endereço (objeto nested dentro de Venue).
class Address {
  final String? street;
  final String? number;
  final String? complement;
  final String? neighborhood;
  final String? city;
  final String? state;
  final String? country;
  final String? zipCode;
  final double? lat;
  final double? lng;

  const Address({
    this.street,
    this.number,
    this.complement,
    this.neighborhood,
    this.city,
    this.state,
    this.country,
    this.zipCode,
    this.lat,
    this.lng,
  });

  /// Cria Address a partir de Map do Firestore.
  factory Address.fromMap(Map<String, dynamic> map) {
    return Address(
      street: map['street'] as String?,
      number: map['number'] as String?,
      complement: map['complement'] as String?,
      neighborhood: map['neighborhood'] as String?,
      city: map['city'] as String?,
      state: map['state'] as String?,
      country: map['country'] as String?,
      zipCode: map['zipCode'] as String?,
      lat: (map['lat'] as num?)?.toDouble(),
      lng: (map['lng'] as num?)?.toDouble(),
    );
  }

  /// Converte para Map (Firestore).
  Map<String, dynamic> toMap() {
    return {
      if (street != null) 'street': street,
      if (number != null) 'number': number,
      if (complement != null) 'complement': complement,
      if (neighborhood != null) 'neighborhood': neighborhood,
      if (city != null) 'city': city,
      if (state != null) 'state': state,
      if (country != null) 'country': country,
      if (zipCode != null) 'zipCode': zipCode,
      if (lat != null) 'lat': lat,
      if (lng != null) 'lng': lng,
    };
  }

  Address copyWith({
    String? street,
    String? number,
    String? complement,
    String? neighborhood,
    String? city,
    String? state,
    String? country,
    String? zipCode,
    double? lat,
    double? lng,
  }) {
    return Address(
      street: street ?? this.street,
      number: number ?? this.number,
      complement: complement ?? this.complement,
      neighborhood: neighborhood ?? this.neighborhood,
      city: city ?? this.city,
      state: state ?? this.state,
      country: country ?? this.country,
      zipCode: zipCode ?? this.zipCode,
      lat: lat ?? this.lat,
      lng: lng ?? this.lng,
    );
  }
}

/// Campo de jogo (venue).
class Venue {
  final String id;
  final String name;
  final String? logoUrl;
  final Address? address;
  final String? mapsUrl;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const Venue({
    required this.id,
    required this.name,
    this.logoUrl,
    this.address,
    this.mapsUrl,
    required this.createdAt,
    this.updatedAt,
  });

  /// Cria Venue a partir de documento Firestore.
  factory Venue.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Venue(
      id: doc.id,
      name: data['name'] as String? ?? '',
      logoUrl: data['logoUrl'] as String?,
      address: data['address'] != null
          ? Address.fromMap(data['address'] as Map<String, dynamic>)
          : null,
      mapsUrl: data['mapsUrl'] as String?,
      createdAt: (data['createdAt'] as Timestamp?)?.toDate() ?? DateTime.now(),
      updatedAt: (data['updatedAt'] as Timestamp?)?.toDate(),
    );
  }

  /// Converte para Map (Firestore).
  Map<String, dynamic> toFirestore() {
    return {
      'name': name,
      if (logoUrl != null) 'logoUrl': logoUrl,
      if (address != null) 'address': address!.toMap(),
      if (mapsUrl != null) 'mapsUrl': mapsUrl,
      'createdAt': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
    };
  }

  Venue copyWith({
    String? name,
    String? logoUrl,
    Address? address,
    String? mapsUrl,
  }) {
    return Venue(
      id: id,
      name: name ?? this.name,
      logoUrl: logoUrl ?? this.logoUrl,
      address: address ?? this.address,
      mapsUrl: mapsUrl ?? this.mapsUrl,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
