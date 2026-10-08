package com.tutorial.mailService.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private static final String SENDER_NAME = "Wigothehacker <bugiriwilson651@gmail.com>";

    public void sendSimpleEmail(String to, String subject, String messageContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(SENDER_NAME);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlTemplate = """
                    <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#ffffff;border-radius:10px;border:1px solid #e8e8e8;overflow:hidden">
                      <div style="background:#0F6E56;padding:28px 32px;text-align:center">
                        <div style="width:40px;height:40px;border-radius:50%;background:rgba(255,255,255,0.15);margin:0 auto 12px;display:flex;align-items:center;justify-content:center">
                          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#ffffff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                            <polyline points="22,6 12,13 2,6"/>
                          </svg>
                        </div>
                        <p style="color:#ffffff;font-size:18px;font-weight:600;margin:0;letter-spacing:0.3px">Wigothehacker</p>
                        <p style="color:rgba(255,255,255,0.65);font-size:12px;margin:4px 0 0">bugiriwilson651@gmail.com</p>
                      </div>
                      <div style="padding:32px">
                        <p style="font-size:12px;color:#0F6E56;font-weight:600;margin:0 0 16px;text-transform:uppercase;letter-spacing:0.5px">Message</p>
                        <p style="font-size:15px;color:#2c2c2c;line-height:1.7;margin:0 0 24px">%s</p>
                      </div>
                      <div style="border-top:1px solid #eeeeee;padding:16px 32px;text-align:center;background:#fafafa">
                        <p style="font-size:12px;color:#999999;margin:0 0 4px">This is an automated message — please do not reply.</p>
                        <p style="font-size:12px;color:#999999;margin:0">&copy; Wigothehacker. All rights reserved.</p>
                      </div>
                    </div>
                    """;

            String finalHtmlBody = String.format(htmlTemplate, messageContent);

            helper.setText(finalHtmlBody, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
}