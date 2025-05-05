package com.techbytedev.signboardmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbytedev.signboardmanager.entity.Design;
import com.techbytedev.signboardmanager.repository.DesignRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Service
public class CanvaService {

    private final RestTemplate restTemplate;
    private final DesignRepository designRepository;
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    @Value("${canva.api.redirect-uri}")
    private String redirectUri;

    public CanvaService(RestTemplate restTemplate, DesignRepository designRepository, JavaMailSender mailSender, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.designRepository = designRepository;
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
    }

    public String startDesign(Long userId, String accessToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"design_type\":{\"type\":\"preset\",\"name\":\"doc\"},\"title\":\"Customer Design\"}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://api.canva.com/rest/v1/designs", request, String.class);
        JsonNode json = objectMapper.readTree(response.getBody());
        String editUrl = json.path("design").path("urls").path("edit_url").asText();
        String designId = json.path("design").path("id").asText();

        Design design = new Design();
        design.setUserId(userId);
        design.setCanvaDesignId(designId);
        design.setStatus("PENDING");
        designRepository.save(design);

        return editUrl;
    }

    public String exportDesign(String designId, String accessToken) throws IOException, MessagingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"format\":\"pdf\"}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://api.canva.com/rest/v1/designs/" + designId + "/exports", request, String.class);
        JsonNode json = objectMapper.readTree(response.getBody());
        String jobId = json.path("export").path("id").asText();

        String exportUrl = pollExportJob(designId, jobId, accessToken);
        String filePath = downloadFile(exportUrl, designId);
        sendEmail(filePath);
        return filePath;
    }

    private String pollExportJob(String designId, String jobId, String accessToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        while (true) {
            ResponseEntity<String> response = restTemplate.exchange("https://api.canva.com/rest/v1/designs/" + designId + "/exports/" + jobId, HttpMethod.GET, request, String.class);
            JsonNode json = objectMapper.readTree(response.getBody());
            String status = json.path("export").path("status").asText();
            if ("success".equals(status)) {
                return json.path("export").path("url").asText();
            }
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    private String downloadFile(String exportUrl, String designId) throws IOException {
        String filePath = "uploads/" + designId + ".pdf";
        new File("uploads").mkdirs();
        URL url = new URL(exportUrl);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close(); rbc.close();
        return filePath;
    }

    private void sendEmail(String filePath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo("company@example.com");
        helper.setSubject("New Design");
        helper.setText("A new design has been submitted.");
        helper.addAttachment("design.pdf", new File(filePath));
        mailSender.send(message);
    }
}