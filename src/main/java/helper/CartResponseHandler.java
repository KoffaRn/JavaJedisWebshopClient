package helper;

import com.google.gson.Gson;
import models.CartDTO;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.io.InputStream;

public class CartResponseHandler implements HttpClientResponseHandler<CartDTO> {
    @Override
    public CartDTO handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        final int status = response.getCode();
        if (status >= 200 && status < 300) {
            try(InputStream body = response.getEntity().getContent()) {
                //System.out.println(new String(body.readAllBytes()));
                Gson gson = new Gson();
                CartDTO result = gson.fromJson(new String(body.readAllBytes()), CartDTO.class);
                //System.out.println(result);
                return result;
            } catch (Exception e) {
                throw new HttpException("Unexpected response status: " + status);
            }
        } else {
            throw new HttpException("Unexpected response status: " + status);
        }
    }
}
