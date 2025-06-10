package finalmission.reservation.dto;

import finalmission.member.dto.MemberResponse;
import finalmission.reservation.domain.Reservation;
import finalmission.restaurant.dto.ReservationTimeResponse;
import finalmission.restaurant.dto.RestaurantResponse;
import java.time.LocalDate;

public record ReservationResponse (
        Long id,
        LocalDate date,
        String state,
        ReservationTimeResponse time,
        MemberResponse member,
        RestaurantResponse restaurant
){

    public static ReservationResponse from(final Reservation reservation){
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                reservation.getReservationState().getMessage(),
                ReservationTimeResponse.from(reservation.getReservationTime()),
                MemberResponse.from(reservation.getMember()),
                RestaurantResponse.from(reservation.getRestaurant())
        );
    }
}
