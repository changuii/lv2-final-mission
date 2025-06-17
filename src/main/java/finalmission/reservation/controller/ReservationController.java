package finalmission.reservation.controller;

import finalmission.config.AuthenticationPrincipal;
import finalmission.reservation.dto.ReservationDetailResponse;
import finalmission.reservation.dto.ReservationRequest;
import finalmission.reservation.dto.ReservationResponse;
import finalmission.reservation.service.ReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDetailResponse> createReservation(
            @RequestBody final ReservationRequest request,
            @AuthenticationPrincipal final String email
    ) {
        final ReservationDetailResponse response = reservationService.create(request, email);
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(response);
    }

    @GetMapping("/member")
    public ResponseEntity<List<ReservationResponse>> findAllByEmail(
            @AuthenticationPrincipal final String email
    ) {
        final List<ReservationResponse> response = reservationService.findAllByMember(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDetailResponse> findById(
            @PathVariable("id") final Long id,
            @AuthenticationPrincipal final String email
    ){
        final ReservationDetailResponse response = reservationService.findById(id, email);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/accept/{id}")
    public ResponseEntity<Void> accept(
            @PathVariable("id") final Long id,
            @AuthenticationPrincipal final String email
    ){
        reservationService.accept(id, email);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") final Long id,
            @AuthenticationPrincipal final String email
    ) {
        reservationService.deleteById(id, email);
        return ResponseEntity.noContent().build();
    }
}
