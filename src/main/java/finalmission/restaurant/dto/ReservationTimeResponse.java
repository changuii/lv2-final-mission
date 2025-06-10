package finalmission.restaurant.dto;

import finalmission.restaurant.domain.ReservationTime;
import java.time.LocalTime;

public record ReservationTimeResponse(
        Long id,
        LocalTime time
) {

    public static ReservationTimeResponse from(final ReservationTime reservationTime) {
        return new ReservationTimeResponse(
                reservationTime.getId(),
                reservationTime.getTime()
        );
    }
}
