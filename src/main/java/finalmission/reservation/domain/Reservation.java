package finalmission.reservation.domain;

import finalmission.exception.CustomException;
import finalmission.member.domain.Member;
import finalmission.reservationtime.domain.ReservationTime;
import finalmission.restaurant.domain.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationState reservationState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationTime reservationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Restaurant restaurant;

    public boolean isOwnMember(final Member member){
        return this.member.equals(member);
    }

    public void accept(final Member restaurantOwner){
        validateRestaurantOwner(restaurantOwner);
        if(this.reservationState != ReservationState.PENDING){
            throw new CustomException("대기 상태의 예약만 승인할 수 있습니다.");
        }
        reservationState = ReservationState.ACCEPT;
    }

    public void reject(final Member restaurantOwner){
        validateRestaurantOwner(restaurantOwner);
        if(this.reservationState != ReservationState.PENDING){
            throw new CustomException("대기 상태의 예약만 거부할 수 있습니다.");
        }
        reservationState = ReservationState.REJECT;
    }

    private void validateRestaurantOwner(final Member restaurantOwner) {
        if(!this.restaurant.isOwner(restaurantOwner)){
            throw new CustomException("식당 소유주만 예약을 승인할 수 있습니다.");
        }
    }
}
