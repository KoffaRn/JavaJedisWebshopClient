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
import org.apache.hc.client5.http.classic.methods.HttpPost;
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
    public static ProductDTO getOneProduct(String jwt, int productId) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/products/" + productId);
            httpGet.setHeader("Authorization", "Bearer " + jwt);
            HttpClientResponseHandler<ProductDTO> responseHandler = new ProductResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ProductDTO editProduct(String jwt, int productId, JsonPatch patch) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPatch httpPatch = new HttpPatch("http://localhost:8080/products/" + productId);
            httpPatch.setHeader("Content-type", "application/json-patch+json");
            httpPatch.setHeader("Authorization", "Bearer " + jwt);
            httpPatch.setEntity(new StringEntity(patch.toJsonArray().toString()));
            HttpClientResponseHandler<ProductDTO> responseHandler = new ProductResponseHandler();
            return httpClient.execute(httpPatch, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteProduct(String jwt, int productId) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpDelete httpDelete = new HttpDelete("http://localhost:8080/products/" + productId);
            httpDelete.setHeader("Authorization", "Bearer " + jwt);
            HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
            String response = httpClient.execute(httpDelete, responseHandler);
            if(!response.equals("Product deleted: " + productId)) {
                throw new RuntimeException("Product not deleted");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ProductDTO editName(String jwt, String newName, int productId) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();
        jsonPatchBuilder.replace("/name", newName);
        return editProduct(jwt, productId, jsonPatchBuilder.build());
    }

    public static ProductDTO editDescription(String jwt, String stringInput, int productId) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();
        jsonPatchBuilder.replace("/description", stringInput);
        return editProduct(jwt, productId, jsonPatchBuilder.build());
    }

    public static ProductDTO editPrice(String jwt, double doubleInput, int productId) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();
        jsonPatchBuilder.replace("/price", String.valueOf(doubleInput));
        return editProduct(jwt, productId, jsonPatchBuilder.build());
    }

    public static ProductDTO editActive(String jwt, boolean booleanInput, int productId) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();
        jsonPatchBuilder.replace("/active", String.valueOf(booleanInput));
        return editProduct(jwt, productId, jsonPatchBuilder.build());
    }

    public static List<CartDTO.CartItemDTO> getCartProducts(UserDTO user) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/carts/user/" + user.getUser().getId());
            httpGet.setHeader("Authorization", "Bearer " + user.getJwt());
            HttpClientResponseHandler<CartDTO> responseHandler = new CartResponseHandler();
            CartDTO cart = httpClient.execute(httpGet, responseHandler);
            return cart.getCartItems();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<ProductDTO> adminGetAllProducts(String jwt) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://localhost:8080/products/all");
            httpGet.setHeader("Authorization", "Bearer " + jwt);
            HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
            String responseBody = httpClient.execute(httpGet, responseHandler);
            Gson gson = new Gson();
            return gson.fromJson(responseBody, new TypeToken<ArrayList<ProductDTO>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static boolean createProduct(String jwt, String name, String description, double price) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/products");
            httpPost.setHeader("Content-type", "application/json");
            ProductDTO product = ProductDTO.builder()
                    .active(true)
                    .current(true)
                    .description(description)
                    .price(price)
                    .name(name)
                    .build();
            Gson gson = new Gson();
            StringEntity entity = new StringEntity(gson.toJson(product));
            httpPost.setEntity(entity);
            httpPost.setHeader("Authorization", "Bearer " + jwt);
            HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
            String responseBody = httpClient.execute(httpPost, responseHandler);
            return responseBody.equals("1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
