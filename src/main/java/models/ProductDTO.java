package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {
    private int id;
    private String name;
    private String description;
    private double price;
    private boolean active;
    private boolean current;
    @Override
    public String toString() {
        return "Product:\n" +
                "name: " + name + '\n' +
                "description: " + description + '\n' +
                "price: " + price;
    }
}
