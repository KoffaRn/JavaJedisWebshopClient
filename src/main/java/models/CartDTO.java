package models;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
@Data
@Builder
public class CartDTO {
    private int id;
    private UserDTO.User user;
    private ArrayList<CartItemDTO> cartItems;
    @Data
    public class CartItemDTO {
        private int id;
        private ProductDTO product;
        private int quantity;
    }
}
