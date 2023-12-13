package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import helper.CartResponseHandler;
import helper.ProductResponseHandler;
import models.CartDTO;
import models.ProductDTO;
import models.UserDTO;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

import javax.json.Json;
import javax.json.JsonPatch;
import javax.json.JsonPatchBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    public static List<ProductDTO> getAllProducts() {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/products");
            HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
            String responseBody = httpClient.execute(httpGet, responseHandler);
            Gson gson = new Gson();
            return gson.fromJson(responseBody, new TypeToken<ArrayList<ProductDTO>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ProductDTO getOneProduct(ProductDTO productDTO) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/products/" + productDTO.getId());
            HttpClientResponseHandler<ProductDTO> responseHandler = new ProductResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ProductDTO getOneProduct(int id) {
        return getOneProduct(ProductDTO.builder().id(id).build());
    }

    private static ProductDTO editProduct(ProductDTO productDTO, JsonPatch patch) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPatch httpPatch = new HttpPatch("http://localhost:8080/products/" + productDTO.getId());
            httpPatch.setHeader("Content-type", "application/json-patch+json");
            httpPatch.setEntity(new StringEntity(patch.toJsonArray().toString()));
            HttpClientResponseHandler<ProductDTO> responseHandler = new ProductResponseHandler();
            return httpClient.execute(httpPatch, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteProduct(ProductDTO productDTO) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpDelete httpDelete = new HttpDelete("http://localhost:8080/products/" + productDTO.getId());
            HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
            String response = httpClient.execute(httpDelete, responseHandler);
            if(!response.equals("Product deleted: " + productDTO.getId())) {
                throw new RuntimeException("Product not deleted");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ProductDTO editName(String newName, ProductDTO productDTO) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();
        jsonPatchBuilder.replace("/name", newName);
        return editProduct(productDTO, jsonPatchBuilder.build());
    }

    public static ProductDTO editDescription(String stringInput, ProductDTO productDTO) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();
        jsonPatchBuilder.replace("/description", stringInput);
        return editProduct(productDTO, jsonPatchBuilder.build());
    }

    public static ProductDTO editPrice(double doubleInput, ProductDTO productDTO) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();
        jsonPatchBuilder.replace("/price", String.valueOf(doubleInput));
        return editProduct(productDTO, jsonPatchBuilder.build());
    }

    public static ProductDTO editActive(boolean booleanInput, ProductDTO productDTO) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();
        jsonPatchBuilder.replace("/active", String.valueOf(booleanInput));
        return editProduct(productDTO, jsonPatchBuilder.build());
    }

    public static List<ProductDTO> getProductsFromCart(UserDTO user) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/carts/user/" + user.getUser().getId());
            System.out.println(user.getJwt());
            httpGet.setHeader("Authorization", "Bearer " + user.getJwt());
            HttpClientResponseHandler<CartDTO> responseHandler = new CartResponseHandler();
            CartDTO cart = httpClient.execute(httpGet, responseHandler);
            //System.out.println(cart);
            return cart.getCartItems().stream().map(CartDTO.CartItemDTO::getProduct).toList();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static List<CartDTO.CartItemDTO> getCartProducts(UserDTO user) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/carts/user/" + user.getUser().getId());
            httpGet.setHeader("Authorization", "Bearer " + user.getJwt());
            HttpClientResponseHandler<CartDTO> responseHandler = new CartResponseHandler();
            CartDTO cart = httpClient.execute(httpGet, responseHandler);
            return cart.getCartItems();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
