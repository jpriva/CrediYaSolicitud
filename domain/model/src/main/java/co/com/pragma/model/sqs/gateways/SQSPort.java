package co.com.pragma.model.sqs.gateways;

public interface SQSPort {
    void sendEmail(String email, String title, String message);
}
