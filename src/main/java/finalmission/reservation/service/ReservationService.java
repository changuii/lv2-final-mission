package finalmission.reservation.service;

import finalmission.member.domain.Member;
import finalmission.member.exception.MemberNotExistsException;
import finalmission.member.exception.MemberNotFoundException;
import finalmission.member.infrastructure.MemberJpaRepository;
import finalmission.reservation.domain.DateGenerator;
import finalmission.reservation.domain.Reservation;
import finalmission.reservation.domain.ReservationState;
import finalmission.reservation.dto.ReservationDetailResponse;
import finalmission.reservation.dto.ReservationRequest;
import finalmission.reservation.dto.ReservationResponse;
import finalmission.reservation.exception.ReservationNotExistsException;
import finalmission.reservation.exception.ReservationNotFoundException;
import finalmission.reservation.exception.ReservationNotOwnerException;
import finalmission.reservation.exception.ReservationPastException;
import finalmission.reservation.infrastructure.ReservationJpaRepository;
import finalmission.reservationtime.domain.ReservationTime;
import finalmission.reservationtime.exception.ReservationTimeNotExistsException;
import finalmission.reservationtime.infrastructure.ReservationTimeJpaRepository;
import finalmission.restaurant.domain.Restaurant;
import finalmission.restaurant.exception.RestaurantNotExistsException;
import finalmission.restaurant.infrastructure.RestaurantJpaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationTimeJpaRepository reservationTimeJpaRepository;
    private final RestaurantJpaRepository restaurantJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final DateGenerator dateGenerator;

    public ReservationDetailResponse create(final ReservationRequest reservationRequest, final String email) {
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(MemberNotExistsException::new);
        final Restaurant restaurant = restaurantJpaRepository.findById(reservationRequest.restaurantId())
                .orElseThrow(RestaurantNotExistsException::new);
        final ReservationTime reservationTime = reservationTimeJpaRepository.findById(
                        reservationRequest.reservationTimeId())
                .orElseThrow(ReservationTimeNotExistsException::new);

        final LocalDate today = dateGenerator.today();
        final LocalDate reservationDate = reservationRequest.date();
        if(reservationDate.isBefore(today) || reservationDate.equals(today)){
            throw new ReservationPastException();
        }
        if(!reservationTime.isEqualRestaurant(restaurant)){
            throw new ReservationTimeNotExistsException();
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

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByMember(final String email) {
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        return reservationJpaRepository.findAllByMember(member)
                .stream()
                .map(reservation -> ReservationResponse.from(reservation, email))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse findById(final Long id, final String email) {
        final Reservation reservation = reservationJpaRepository.findById(id)
                .orElseThrow(ReservationNotFoundException::new);
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(MemberNotExistsException::new);

        if(!reservation.isOwnMember(member)){
            throw new ReservationNotOwnerException();
        }

        return ReservationDetailResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDetailResponse> findAllByRestaurantAndDate(final Long restaurantId, final LocalDate date) {
        final Restaurant restaurant = restaurantJpaRepository.findById(restaurantId)
                .orElseThrow(RestaurantNotExistsException::new);

        return reservationJpaRepository.findAllByRestaurantAndDate(restaurant, date)
                .stream()
                .map(ReservationDetailResponse::from)
                .toList();
    }

    public void accept(final Long reservationId, final String email) {
        final Reservation reservation = reservationJpaRepository.findById(reservationId)
                .orElseThrow(ReservationNotExistsException::new);
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(MemberNotExistsException::new);

        reservation.accept(member);
    }

    public void reject(final Long reservationId, final String email) {
        final Reservation reservation = reservationJpaRepository.findById(reservationId)
                .orElseThrow(ReservationNotExistsException::new);
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(MemberNotExistsException::new);

        reservation.reject(member);
    }

    public void deleteById(final Long id, final String email){
        final Reservation reservation = reservationJpaRepository.findById(id)
                .orElseThrow(ReservationNotFoundException::new);
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(MemberNotExistsException::new);

        if(!reservation.isOwnMember(member)){
            throw new ReservationNotOwnerException();
        }


        reservationJpaRepository.delete(reservation);
    }
}
