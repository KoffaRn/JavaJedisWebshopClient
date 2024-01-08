package service;

import com.google.gson.Gson;
import helper.CartResponseHandler;
import models.AddProductRequest;
import models.CartDTO;
import models.ProductDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

public class CartService {
    public static CartDTO getCart(String jwt, int userId) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/carts/user/" + userId);
            httpGet.setHeader("Authorization", "Bearer " + jwt);
            HttpClientResponseHandler<CartDTO> responseHandler = new CartResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addToCart(String jwt, int productId, int quantity, int userId) {
        ProductDTO product = ProductService.getOneProduct(jwt, productId);
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/carts/addProduct");
            AddProductRequest addProductRequest = new AddProductRequest(getCart(jwt, userId), product, quantity);
            Gson gson = new Gson();
            StringEntity entity = new StringEntity(gson.toJson(addProductRequest));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(entity);
            httpPost.setHeader("Authorization", "Bearer " + jwt);
            HttpClientResponseHandler<CartDTO> responseHandler = new CartResponseHandler();
            httpClient.execute(httpPost, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void buyCart(String jwt, int userId) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/carts/buy");
            Gson gson = new Gson();
            StringEntity entity = new StringEntity(gson.toJson(getCart(jwt, userId)));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + jwt);
            httpPost.setEntity(entity);
            HttpClientResponseHandler<CartDTO> responseHandler = new CartResponseHandler();
            httpClient.execute(httpPost, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeItemFromCart(String jwt, int id, int quantity, int id1) {
        addToCart(jwt, id, -quantity, id1);
    }
}
