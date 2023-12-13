package models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class OrderDTO {
    private int id;
    private UserDTO.User user;
    private ArrayList<OrderItemDTO> orderItems;
    @Data
    public class OrderItemDTO {
        private int id;
        private ProductDTO product;
        private int quantity;
    }
}
