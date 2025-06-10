package finalmission.reservation.service;

import finalmission.exception.CustomException;
import finalmission.member.domain.Member;
import finalmission.member.infrastructure.MemberJpaRepository;
import finalmission.reservation.domain.DateGenerator;
import finalmission.reservation.domain.Reservation;
import finalmission.reservation.domain.ReservationState;
import finalmission.reservation.dto.ReservationRequest;
import finalmission.reservation.dto.ReservationResponse;
import finalmission.reservation.infrastructure.ReservationJpaRepository;
import finalmission.restaurant.domain.ReservationTime;
import finalmission.restaurant.domain.Restaurant;
import finalmission.restaurant.infrastructure.ReservationTimeJpaRepository;
import finalmission.restaurant.infrastructure.RestaurantJpaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationTimeJpaRepository reservationTimeJpaRepository;
    private final RestaurantJpaRepository restaurantJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final DateGenerator dateGenerator;

    public ReservationResponse create(final ReservationRequest reservationRequest) {
        final Member member = memberJpaRepository.findByEmail(reservationRequest.email())
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버입니다."));
        final Restaurant restaurant = restaurantJpaRepository.findById(reservationRequest.restaurantId())
                .orElseThrow(() -> new CustomException("존재하지 않는 식당입니다."));
        final ReservationTime reservationTime = reservationTimeJpaRepository.findById(
                        reservationRequest.reservationTimeId())
                .orElseThrow(() -> new CustomException("존재하지 않는 시간입니다."));

        final LocalDate today = dateGenerator.today();
        final LocalDate reservationDate = reservationRequest.date();
        if(reservationDate.isBefore(today) || reservationDate.equals(today)){
            throw new CustomException("당일 예약 혹은 이미 지난 시간으로 예약이 불가능합니다.");
        }

        final Reservation notSavedReservation = Reservation.builder()
                .date(reservationRequest.date())
                .reservationState(ReservationState.PENDING)
                .member(member)
                .reservationTime(reservationTime)
                .restaurant(restaurant)
                .build();

        final Reservation savedReservation = reservationJpaRepository.save(notSavedReservation);
        return ReservationResponse.from(savedReservation);
    }

    public List<ReservationResponse> findAllByMember(final String email){
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버입니다."));

        return reservationJpaRepository.findAllByMember(member)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
