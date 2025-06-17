package finalmission.reservation.service;

import finalmission.exception.CustomException;
import finalmission.member.domain.Member;
import finalmission.member.infrastructure.MemberJpaRepository;
import finalmission.reservation.domain.DateGenerator;
import finalmission.reservation.domain.Reservation;
import finalmission.reservation.domain.ReservationState;
import finalmission.reservation.dto.ReservationDetailResponse;
import finalmission.reservation.dto.ReservationRequest;
import finalmission.reservation.dto.ReservationResponse;
import finalmission.reservation.infrastructure.ReservationJpaRepository;
import finalmission.reservationtime.domain.ReservationTime;
import finalmission.reservationtime.infrastructure.ReservationTimeJpaRepository;
import finalmission.restaurant.domain.Restaurant;
import finalmission.restaurant.infrastructure.RestaurantJpaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationTimeJpaRepository reservationTimeJpaRepository;
    private final RestaurantJpaRepository restaurantJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final DateGenerator dateGenerator;

    public ReservationDetailResponse create(final ReservationRequest reservationRequest, final String email) {
        final Member member = memberJpaRepository.findByEmail(email)
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
        if(!reservationTime.isEqualRestaurant(restaurant)){
            throw new CustomException("식당의 예약 시간이 잘못되었습니다.");
        }

        final Reservation notSavedReservation = Reservation.builder()
                .date(reservationRequest.date())
                .reservationState(ReservationState.PENDING)
                .member(member)
                .reservationTime(reservationTime)
                .restaurant(restaurant)
                .build();

        final Reservation savedReservation = reservationJpaRepository.save(notSavedReservation);
        return ReservationDetailResponse.from(savedReservation);
    }

    public List<ReservationResponse> findAllByMember(final String email) {
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버입니다."));

        return reservationJpaRepository.findAllByMember(member)
                .stream()
                .map(reservation -> ReservationResponse.from(reservation, email))
                .toList();
    }

    public ReservationDetailResponse findById(final Long id, final String email) {
        final Reservation reservation = reservationJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException("존재하지 않는 예약 id 입니다."));
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버입니다."));

        if(!reservation.isOwnMember(member)){
            throw new CustomException("자신 소유의 예약만 조회할 수 있습니다.");
        }

        return ReservationDetailResponse.from(reservation);
    }

    @Transactional
    public void accept(final Long reservationId, final String email) {
        final Reservation reservation = reservationJpaRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException("존재하지 않는 예약 id 입니다."));
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버입니다."));

        reservation.accept(member);
    }

    @Transactional
    public void reject(final Long reservationId, final String email) {
        final Reservation reservation = reservationJpaRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException("존재하지 않는 예약 id 입니다."));
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버입니다."));

        reservation.reject(member);
    }

    public void deleteById(final Long id, final String email){
        final Reservation reservation = reservationJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException("존재하지 않는 예약 id 입니다."));
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버입니다."));

        if(!reservation.isOwnMember(member)){
            throw new CustomException("자신 소유의 예약만 삭제할 수 있습니다.");
        }


        reservationJpaRepository.delete(reservation);
    }
}
