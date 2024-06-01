package roomescape.admin.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AdminOnly;
import roomescape.time.dto.ReservationTimeAddRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

@RestController
public class AdminReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public AdminReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping("/admin/times")
    @AdminOnly
    public ResponseEntity<ReservationTimeResponse> saveReservationTime(
            @Valid @RequestBody ReservationTimeAddRequest reservationTimeAddRequest) {
        ReservationTimeResponse saveResponse = reservationTimeService.saveReservationTime(reservationTimeAddRequest);
        URI createdUri = URI.create("/times/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @DeleteMapping("/admin/times/{id}")
    @AdminOnly
    public ResponseEntity<Void> removeReservationTime(@PathVariable("id") Long id) {
        reservationTimeService.removeReservationTime(id);
        return ResponseEntity.noContent().build();
    }
}