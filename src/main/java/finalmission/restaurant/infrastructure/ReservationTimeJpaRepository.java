package finalmission.restaurant.infrastructure;

import finalmission.restaurant.domain.ReservationTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationTimeJpaRepository extends JpaRepository<ReservationTime, Long> {
}
