package com.example.register_app.Util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setTo(email);
        messageHelper.setSubject("Verify OTP");

        // Fixing the multiline string formatting
        String content = """
                <div><a href="http://localhost:8080/verify-account?email=%s&otp=%s" target="_blank">Click To Verify</a></div>
                """.formatted(email, otp);

        messageHelper.setText(content, true);  // Setting true for HTML content
        javaMailSender.send(mimeMessage); // Send the email after setting it up
    }

    public void sendSetPasswordEmail(String email) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setTo(email);
        messageHelper.setSubject("Set Password");

        // Fixing the multiline string formatting
        String content = """
            <div><a href="http://localhost:8080/set-password?email=%s" target="_blank">Click To Set New Password</a></div>
            """.formatted(email);

        // Setting the HTML content before sending
        messageHelper.setText(content, true);

        // Now send the email after setting it up
        javaMailSender.send(mimeMessage);
    }


}
