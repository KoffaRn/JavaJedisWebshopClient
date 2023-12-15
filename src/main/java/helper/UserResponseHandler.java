package helper;

import com.google.gson.Gson;
import models.UserDTO;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.io.InputStream;

public class UserResponseHandler implements HttpClientResponseHandler<UserDTO.User> {
    @Override
    public UserDTO.User handleResponse(ClassicHttpResponse response) throws HttpException {
        final int status = response.getCode();
        if (status >= 200 && status < 300) {
            try(InputStream body = response.getEntity().getContent()) {
                Gson gson = new Gson();
                return gson.fromJson(new String(body.readAllBytes()), UserDTO.User.class);
            } catch (Exception e) {
                throw new HttpException("Unexpected response status: " + status);
            }
        } else {
            throw new HttpException("Unexpected response status: " + status);
        }
    }
}
