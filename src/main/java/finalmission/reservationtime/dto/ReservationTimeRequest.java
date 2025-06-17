package finalmission.reservationtime.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public record ReservationTimeRequest(
        Long restaurantId,
        @JsonFormat(pattern = "HH:mm") LocalTime time
) {
}
