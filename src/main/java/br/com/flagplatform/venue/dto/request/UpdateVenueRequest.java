package br.com.flagplatform.venue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateVenueRequest(
        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 500)
        String address,

        @Size(max = 500)
        String mapsUrl
) {
}
