package service;

import helper.ListUserResponseHandler;
import models.UserDTO;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.List;

public class UserService {
    public static void changePassword(String jwt, int userId, String newPassword) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPut httpPut = new HttpPut("http://localhost:8080/users/password");
            httpPut.setHeader("Authorization", "Bearer " + jwt);
            httpPut.setHeader("Content-type", "application/json");
            StringEntity entity = new StringEntity("{\"id\" : " + userId + ", \"password\": \"" + newPassword + "\"}");
            httpPut.setEntity(entity);
            httpClient.execute(httpPut, new BasicHttpClientResponseHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void changeUsername(String jwt, int userId, String oldUsername, String newUsername) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPut httpPut = new HttpPut("http://localhost:8080/users/username");
            httpPut.setHeader("Authorization", "Bearer " + jwt);
            httpPut.setHeader("Content-type", "application/json");
            StringEntity entity = new StringEntity("{\"id\" : " + userId + ", \"username\": \"" + oldUsername + "\", \"newUsername\": \"" + newUsername + "\"}");
            httpPut.setEntity(entity);
            httpClient.execute(httpPut, new BasicHttpClientResponseHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteUser(String jwt, int userId) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpDelete httpDelete = new HttpDelete("http://localhost:8080/users/" + userId);
            httpDelete.setHeader("Authorization", "Bearer " + jwt);
            httpClient.execute(httpDelete, new BasicHttpClientResponseHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<UserDTO.User> getAllUsers(String jwt) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/users");
            httpGet.setHeader("Authorization", "Bearer " + jwt);
            HttpClientResponseHandler<List<UserDTO.User>> responseHandler = new ListUserResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
