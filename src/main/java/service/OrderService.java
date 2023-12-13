package service;

import helper.ListOrderResponseHandler;
import models.OrderDTO;
import models.UserDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.util.List;

public class OrderService {
    public static List<OrderDTO> getAllOrders(UserDTO userDTO) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://localhost:8080/orders/user/" + userDTO.getUser().getId());
            httpGet.setHeader("Authorization", "Bearer " + userDTO.getJwt());
            HttpClientResponseHandler<List<OrderDTO>> responseHandler = new ListOrderResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
