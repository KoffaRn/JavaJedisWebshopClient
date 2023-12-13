package service;

import models.UserDTO;
import org.apache.hc.client5.http.impl.CookieSpecSupport;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public class UserService {
    private final static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static UserDTO getUser(int id, String jwt) throws Exception {
        return null;
    }
}
