package com.lankaid.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // The real email engine

    public void sendApprovalEmail(String toEmail, String nic, String requestType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Lanka-ID Portal <noreply@lankaid.gov>");
            message.setTo(toEmail);
            message.setSubject("✅ Document Ready: " + requestType);
            message.setText("Dear Citizen (NIC: " + nic + "),\n\n" +
                    "Good news! Your request for '" + requestType + "' has been APPROVED.\n" +
                    "You can now download your digital certificate from the portal.\n\n" +
                    "Regards,\nLanka-ID Team");

            mailSender.send(message);
            System.out.println("✅ Email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.out.println("❌ Error sending email: " + e.getMessage());
        }
    }

    public void sendRejectionEmail(String toEmail, String nic) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("⚠️ Update on your Request");
            message.setText("Dear Citizen (NIC: " + nic + "),\n\n" +
                    "We regret to inform you that your request has been REJECTED due to incomplete documentation.\n" +
                    "Please visit the nearest office.\n\n" +
                    "Regards,\nLanka-ID Team");

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("❌ Error sending email: " + e.getMessage());
        }
    }
}