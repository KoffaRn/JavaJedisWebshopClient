package models;

public record AddProductRequest(CartDTO cart, ProductDTO product, int quantity) {
}
