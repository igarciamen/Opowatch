package com.igarciamen.notifications.service;

import com.igarciamen.notifications.payloads.request.PostingSummary;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendNewPostingsNotification(String toEmail, String watcherName, List<PostingSummary> postings) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("OpoWatch: new IT postings detected (" + watcherName + ")");
            helper.setText(buildBody(watcherName, postings), true);

            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Could not send notification email to " + toEmail, ex);
        }
    }

    private String buildBody(String watcherName, List<PostingSummary> postings) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>New IT public job postings detected</h2>");
        html.append("<p>Source: ").append(watcherName).append("</p>");
        html.append("<ul>");
        for (PostingSummary posting : postings) {
            html.append("<li><a href=\"").append(posting.getUrl()).append("\">")
                    .append(posting.getTitle()).append("</a>");
            if (posting.getOrganization() != null && !posting.getOrganization().isBlank()) {
                html.append(" - ").append(posting.getOrganization());
            }
            html.append("</li>");
        }
        html.append("</ul>");
        html.append("<p>You are receiving this email because you subscribed to OpoWatch notifications.</p>");
        return html.toString();
    }
}