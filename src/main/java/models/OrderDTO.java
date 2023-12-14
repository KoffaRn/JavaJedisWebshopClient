package models;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
public class OrderDTO {
    private int id;
    private UserDTO.User user;
    private ArrayList<OrderItemDTO> products;
    @Data
    public class OrderItemDTO {
        private int id;
        private ProductDTO product;
        private int quantity;
    }
}
