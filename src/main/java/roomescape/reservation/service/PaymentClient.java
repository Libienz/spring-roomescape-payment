package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.IllegalRequestException;
import roomescape.global.exception.InternalServerException;
import roomescape.reservation.dto.PaymentConfirmRequest;

@Component
public class PaymentClient {

    private static final String BASE_URL = "https://api.tosspayments.com";
    private static final String CONFIRM_URI = "/v1/payments/confirm";
    private static final String AUTH_TYPE = "Basic ";

    @Value("${third-party-api.payment.secret-key}")
    private String secretKey;
    private final RestClient restClient;

    public PaymentClient() {
        this.restClient = RestClient.builder().baseUrl(BASE_URL).build();
    }

    public void requestConfirmPayment(PaymentConfirmRequest paymentConfirmRequest) {

        restClient.post()
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", createAuthorizationValue())
                .body(paymentConfirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new IllegalRequestException("결제 승인 요청 정보가 잘못되었습니다");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new InternalServerException("결제 승인 도중 알 수 없는 예외가 발생했습니다");
                })
                .toBodilessEntity();
    }

    private String createAuthorizationValue() {
        byte[] encodedBytes = Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String encodedSecretKey = new String(encodedBytes);

        return AUTH_TYPE + encodedSecretKey;
    }
}