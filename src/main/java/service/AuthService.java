package service;

import helper.UserDTOResponseHandler;
import models.UserDTO;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.nio.charset.StandardCharsets;

public class AuthService {
    public static UserDTO register(String username, String password) throws Exception {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/auth/register");
            httpPost.setHeader("Content-type", "application/json");
            String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            httpPost.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
            HttpClientResponseHandler<UserDTO> responseHandler = new UserDTOResponseHandler();
            return httpClient.execute(httpPost, responseHandler);
        }
    }
    public static UserDTO login(String username, String password) throws Exception {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/auth/login");
            httpPost.setHeader("Content-type", "application/json");
            String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            httpPost.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
            HttpClientResponseHandler<UserDTO> responseHandler = new UserDTOResponseHandler();
            return httpClient.execute(httpPost, responseHandler);
        }
    }
}
