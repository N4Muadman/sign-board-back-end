package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.UserDesign;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CanvaService {

    @Value("${canva.api.key}")
    private String apiKey;

    @Value("${canva.api.secret}")
    private String apiSecret;

    @Value("${canva.api.redirect-uri}")
    private String redirectUri;

    // Lấy access token qua OAuth (giả sử bạn đã có token từ bước xác thực)
    private String accessToken;

    public String createDesign(String designType) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://api.canva.com/v1/designs");
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Content-Type", "application/json");

        JSONObject body = new JSONObject();
        body.put("design_type", designType); // Ví dụ: "Poster"

        post.setEntity(new StringEntity(body.toString()));
        CloseableHttpResponse response = client.execute(post);

        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject json = new JSONObject(responseBody);
        return json.getString("design_id");
    }

    public String exportDesign(String designId) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://api.canva.com/v1/exports");
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Content-Type", "application/json");

        JSONObject body = new JSONObject();
        body.put("design_id", designId);
        body.put("format", "png");

        post.setEntity(new StringEntity(body.toString()));
        CloseableHttpResponse response = client.execute(post);

        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject json = new JSONObject(responseBody);
        return json.getString("export_url");
    }
}