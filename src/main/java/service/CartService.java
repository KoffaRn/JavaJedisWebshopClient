package service;

import com.google.gson.Gson;
import helper.CartResponseHandler;
import models.AddProductRequest;
import models.CartDTO;
import models.ProductDTO;
import models.UserDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

public class CartService {
    public static CartDTO getCart(UserDTO user) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/carts/user/" + user.getUser().getId());
            System.out.println("http://localhost:8080/carts/user/" + user.getUser().getId());
            httpGet.setHeader("Authorization", "Bearer " + user.getJwt());
            HttpClientResponseHandler<CartDTO> responseHandler = new CartResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addToCart(ProductDTO productDTO, int quantity, UserDTO user) {

        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/carts/addProduct");
            AddProductRequest addProductRequest = new AddProductRequest(getCart(user), productDTO, quantity);
            Gson gson = new Gson();
            StringEntity entity = new StringEntity(gson.toJson(addProductRequest));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(entity);
            httpPost.setHeader("Authorization", "Bearer " + user.getJwt());
            HttpClientResponseHandler<CartDTO> responseHandler = new CartResponseHandler();
            httpClient.execute(httpPost, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void buyCart(UserDTO userDTO) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/carts/buy");
            Gson gson = new Gson();
            StringEntity entity = new StringEntity(gson.toJson(getCart(userDTO)));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + userDTO.getJwt());
            httpPost.setEntity(entity);
            HttpClientResponseHandler<CartDTO> responseHandler = new CartResponseHandler();
            httpClient.execute(httpPost, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
