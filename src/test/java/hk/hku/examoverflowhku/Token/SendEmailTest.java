package hk.hku.examoverflowhku.Token;

import org.junit.Test;

public class SendEmailTest {

    @Test
    public void sendMail_ShouldSendTokenSuccessfully() {
        String code = "1111";
        String destination = "ivanrz29@connect.hku.hk";
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendMail(destination, code);
    }
}
