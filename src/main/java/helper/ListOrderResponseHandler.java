package helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.OrderDTO;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import java.io.InputStream;
import java.util.List;

public class ListOrderResponseHandler implements HttpClientResponseHandler<List<OrderDTO>> {
    @Override
    public List<OrderDTO> handleResponse(ClassicHttpResponse response) throws HttpException {
        if(response.getCode() >= 200 && response.getCode() < 300) {
            try(InputStream body = response.getEntity().getContent()) {
                Gson gson = new Gson();
                return gson.fromJson(new String(body.readAllBytes()), new TypeToken<>() {
                });
            } catch (Exception e) {
                throw new HttpException("Unexpected response status: " + response.getCode());
            }
        } else {
            throw new HttpException("Unexpected response status: " + response.getCode());
        }
    }
}
