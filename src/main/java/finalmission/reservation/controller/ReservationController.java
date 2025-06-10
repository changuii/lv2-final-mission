package finalmission.reservation.controller;

import finalmission.reservation.dto.ReservationRequest;
import finalmission.reservation.dto.ReservationResponse;
import finalmission.reservation.service.ReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody final ReservationRequest request
    ) {
        final ReservationResponse response = reservationService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAllByEmail(
            @RequestParam("email") final String email
    ) {
        final List<ReservationResponse> response = reservationService.findAllByMember(email);
        return ResponseEntity.ok(response);
    }
}
