package service;

import helper.UserDTOResponseHandler;
import models.UserDTO;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class AuthService {
    public static boolean register(String username, String password) throws Exception {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/auth/register");
            httpPost.setHeader("Content-type", "application/json");
            String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            httpPost.setEntity(new StringEntity(json));
            HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
            String responseBody = httpClient.execute(httpPost, responseHandler);
            return responseBody.equals("1");
        }
    }
    public static UserDTO login(String username, String password) throws Exception {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/auth/login");
            httpPost.setHeader("Content-type", "application/json");
            String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            httpPost.setEntity(new StringEntity(json));
            HttpClientResponseHandler<UserDTO> responseHandler = new UserDTOResponseHandler();
            return httpClient.execute(httpPost, responseHandler);
        }
    }
}
