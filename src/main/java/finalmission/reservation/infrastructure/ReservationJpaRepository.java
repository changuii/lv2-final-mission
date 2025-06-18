package finalmission.reservation.infrastructure;

import finalmission.member.domain.Member;
import finalmission.reservation.domain.Reservation;
import finalmission.restaurant.domain.Restaurant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByMember(final Member member);
    List<Reservation> findAllByRestaurantAndDate(final Restaurant restaurant, final LocalDate date);
}
