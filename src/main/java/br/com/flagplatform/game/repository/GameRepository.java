package br.com.flagplatform.game.repository;

import br.com.flagplatform.common.model.Game;
import br.com.flagplatform.common.repository.BaseFirestoreRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository para jogos.
 * 
 * Acessa a collection 'games' no Firestore.
 */
@Repository
public class GameRepository extends BaseFirestoreRepository<Game> {

    public GameRepository(Firestore firestore) {
        super(firestore);
    }

    @Override
    public String getCollectionName() {
        return "games";
    }

    @Override
    public Game fromDocument(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return null;
        }

        return Game.builder()
                .id(doc.getId())
                .competitionId((String) data.get("competitionId"))
                .roundId((String) data.get("roundId"))
                .venueId((String) data.get("venueId"))
                .homeTeamId((String) data.get("homeTeamId"))
                .awayTeamId((String) data.get("awayTeamId"))
                .scheduledAt(data.get("scheduledAt") != null 
                    ? ((com.google.cloud.Timestamp) data.get("scheduledAt")).toDate().toInstant() 
                    : null)
                .actualStartTime(data.get("actualStartTime") != null 
                    ? ((com.google.cloud.Timestamp) data.get("actualStartTime")).toDate().toInstant() 
                    : null)
                .actualEndTime(data.get("actualEndTime") != null 
                    ? ((com.google.cloud.Timestamp) data.get("actualEndTime")).toDate().toInstant() 
                    : null)
                .homeScore(data.get("homeScore") != null ? ((Number) data.get("homeScore")).intValue() : null)
                .awayScore(data.get("awayScore") != null ? ((Number) data.get("awayScore")).intValue() : null)
                .status((String) data.get("status"))
                .notes((String) data.get("notes"))
                .competitionName((String) data.get("competitionName"))
                .roundNumber(data.get("roundNumber") != null ? ((Number) data.get("roundNumber")).intValue() : null)
                .roundName((String) data.get("roundName"))
                .homeTeamName((String) data.get("homeTeamName"))
                .homeTeamLogoUrl((String) data.get("homeTeamLogoUrl"))
                .awayTeamName((String) data.get("awayTeamName"))
                .awayTeamLogoUrl((String) data.get("awayTeamLogoUrl"))
                .venueName((String) data.get("venueName"))
                .venueAddress((String) data.get("venueAddress"))
                .build();
    }

    @Override
    public Map<String, Object> toMap(Game item) {
        Map<String, Object> map = new HashMap<>();
        map.put("competitionId", item.getCompetitionId());
        if (item.getRoundId() != null) map.put("roundId", item.getRoundId());
        if (item.getVenueId() != null) map.put("venueId", item.getVenueId());
        map.put("homeTeamId", item.getHomeTeamId());
        map.put("awayTeamId", item.getAwayTeamId());
        if (item.getScheduledAt() != null) map.put("scheduledAt", com.google.cloud.Timestamp.of(item.getScheduledAt()));
        if (item.getActualStartTime() != null) map.put("actualStartTime", com.google.cloud.Timestamp.of(item.getActualStartTime()));
        if (item.getActualEndTime() != null) map.put("actualEndTime", com.google.cloud.Timestamp.of(item.getActualEndTime()));
        if (item.getHomeScore() != null) map.put("homeScore", item.getHomeScore());
        if (item.getAwayScore() != null) map.put("awayScore", item.getAwayScore());
        map.put("status", item.getStatus());
        if (item.getNotes() != null) map.put("notes", item.getNotes());
        map.put("createdAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        map.put("updatedAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        if (item.getCompetitionName() != null) map.put("competitionName", item.getCompetitionName());
        if (item.getRoundNumber() != null) map.put("roundNumber", item.getRoundNumber());
        if (item.getRoundName() != null) map.put("roundName", item.getRoundName());
        if (item.getHomeTeamName() != null) map.put("homeTeamName", item.getHomeTeamName());
        if (item.getHomeTeamLogoUrl() != null) map.put("homeTeamLogoUrl", item.getHomeTeamLogoUrl());
        if (item.getAwayTeamName() != null) map.put("awayTeamName", item.getAwayTeamName());
        if (item.getAwayTeamLogoUrl() != null) map.put("awayTeamLogoUrl", item.getAwayTeamLogoUrl());
        if (item.getVenueName() != null) map.put("venueName", item.getVenueName());
        if (item.getVenueAddress() != null) map.put("venueAddress", item.getVenueAddress());
        return map;
    }
}
