/// Enums para o Flag Platform.

enum Gender { male, female, mixed }

enum PersonRole { athlete, coach, manager, organizer, referee, admin }

enum UserStatus { pending, active, rejected }

enum UserRole { admin, organizer, mesa }

enum OrganizationType { federation, league, association, university, club, other }

enum OrganizationStatus { active, inactive }

enum TeamStatus { active, inactive, disabled }

enum SeasonStatus { draft, inProgress, closed }

enum CompetitionStatus { draft, registrationOpen, inProgress, finished, cancelled }

enum GameStatus { scheduled, opening, inProgress, conference, finished, cancelled }

enum CheckInStatus { pending, checkedIn, absent }

enum RosterStatus { pending, approved, rejected, active, inactive }

enum EligibilityStatus { pending, approved, rejected }

enum GroupingType { singleElimination, roundRobin, swiss, league, custom }

enum GroupType { conference, division, pool, bracket }

enum ScoreEventType { touchdown, fieldGoal, safety, extraPoint, conversion }

/// Converte string do Firestore para enum.
Gender? genderFromString(String? value) {
  if (value == null) return null;
  return Gender.values.firstWhere(
    (e) => e.name == value,
    orElse: () => Gender.mixed,
  );
}

/// Converte string do Firestore para enum.
GameStatus? gameStatusFromString(String? value) {
  if (value == null) return null;
  return GameStatus.values.firstWhere(
    (e) => e.name == value,
    orElse: () => GameStatus.scheduled,
  );
}

/// Converte string do Firestore para enum.
CompetitionStatus? competitionStatusFromString(String? value) {
  if (value == null) return null;
  return CompetitionStatus.values.firstWhere(
    (e) => e.name == value,
    orElse: () => CompetitionStatus.draft,
  );
}
