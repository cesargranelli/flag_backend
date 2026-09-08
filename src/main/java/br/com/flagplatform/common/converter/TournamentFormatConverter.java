package br.com.flagplatform.common.converter;

import br.com.flagplatform.common.converter.abstration.PersistableEnumConverter;
import br.com.flagplatform.common.enums.TournamentFormat;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TournamentFormatConverter extends PersistableEnumConverter<TournamentFormat> {

    public TournamentFormatConverter() {
        super(TournamentFormat.class);
    }

}
