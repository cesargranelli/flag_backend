package br.com.flagplatform.common.converter;

import br.com.flagplatform.common.converter.abstration.PersistableEnumConverter;
import br.com.flagplatform.common.enums.CompetitionTeamStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CompetitionTeamStatusConverter
        extends PersistableEnumConverter<CompetitionTeamStatus> {

    public CompetitionTeamStatusConverter() {
        super(CompetitionTeamStatus.class);
    }
}
