package helper;

import com.google.gson.Gson;
import models.ProductDTO;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import java.io.InputStream;

public class ProductResponseHandler implements HttpClientResponseHandler<ProductDTO> {
    @Override
    public ProductDTO handleResponse(ClassicHttpResponse response) throws HttpException {
        final int status = response.getCode();
        if (status >= 200 && status < 300) {
            try(InputStream body = response.getEntity().getContent()) {
                Gson gson = new Gson();
                return gson.fromJson(new String(body.readAllBytes()), ProductDTO.class);
            } catch (Exception e) {
                throw new HttpException("Unexpected response status: " + status);
            }
        } else {
            throw new HttpException("Unexpected response status: " + status);
        }
    }
}
