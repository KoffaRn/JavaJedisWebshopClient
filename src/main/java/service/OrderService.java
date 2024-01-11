package service;

import helper.ListOrderResponseHandler;
import models.OrderDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.util.List;

public class OrderService {
    public static List<OrderDTO> getOrders(String jwt, int userId) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://localhost:8080/orders/user/" + userId);
            httpGet.setHeader("Authorization", "Bearer " + jwt);
            HttpClientResponseHandler<List<OrderDTO>> responseHandler = new ListOrderResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<OrderDTO> getAllOrders(String jwt) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://localhost:8080/orders");
            httpGet.setHeader("Authorization", "Bearer " + jwt);
            HttpClientResponseHandler<List<OrderDTO>> responseHandler = new ListOrderResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
