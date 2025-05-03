package zercher.be.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ResourceBundle;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendEmail(String to, String subject, String template, Context context) {
        try {
            var locale = LocaleContextHolder.getLocale();
            var bundle = ResourceBundle.getBundle("messages", locale);

            var templatePath = "mail/" + locale.getLanguage() + "/" + template;
            var translatedSubject = bundle.getString("mail." + subject);

            log.info("Attempting to send email template {} to {}", templatePath, to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            String htmlContent = templateEngine.process(templatePath, context);

            helper.setTo(to);
            helper.setSubject(translatedSubject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            log.info("Successfully sent email template {} to {}", templatePath, to);
        } catch (MessagingException exception) {
            log.error("Could not send email!", exception);
            throw new RuntimeException(exception);
        }
    }
}
