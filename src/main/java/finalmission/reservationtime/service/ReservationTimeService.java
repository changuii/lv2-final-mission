package finalmission.reservationtime.service;

import finalmission.exception.CustomException;
import finalmission.member.domain.Member;
import finalmission.member.infrastructure.MemberJpaRepository;
import finalmission.reservationtime.domain.ReservationTime;
import finalmission.reservationtime.dto.ReservationTimeRequest;
import finalmission.reservationtime.dto.ReservationTimeResponse;
import finalmission.reservationtime.infrastructure.ReservationTimeJpaRepository;
import finalmission.restaurant.domain.Restaurant;
import finalmission.restaurant.infrastructure.RestaurantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationTimeService {

    private final ReservationTimeJpaRepository reservationTimeJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final RestaurantJpaRepository restaurantJpaRepository;

    public ReservationTimeResponse create(final ReservationTimeRequest request, final String email){
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버입니다."));
        final Restaurant restaurant = restaurantJpaRepository.findById(request.restaurantId())
                .orElseThrow(() -> new CustomException("존재하지 않는 식당입니다."));

        validateRestaurantOwner(restaurant, member);
        validateDuplicationTime(request, restaurant);

        final ReservationTime reservationTime = ReservationTime.builder()
                .time(request.time())
                .restaurant(restaurant)
                .build();

        final ReservationTime savedReservationTime = reservationTimeJpaRepository.save(reservationTime);
        return ReservationTimeResponse.from(savedReservationTime);
    }

    private void validateRestaurantOwner(final Restaurant restaurant, final Member member) {
        if(!restaurant.isOwner(member)){
            throw new CustomException("식당의 주인만 시간을 생성할 수 있습니다.");
        }
    }

    private void validateDuplicationTime(final ReservationTimeRequest request, final Restaurant restaurant) {
        if(reservationTimeJpaRepository.existsByTimeAndRestaurant(request.time(), restaurant)){
            throw new CustomException("이미 존재하는 예약 시간입니다.");
        }
    }

}
