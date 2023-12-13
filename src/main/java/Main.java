import models.ProductDTO;
import service.AuthService;
import service.ProductService;

public class Main {
    public static void main(String[] args) {
        for(ProductDTO productDTO : ProductService.getAllProducts()) {
            System.out.println(productDTO);
        }
        Application app = new Application();
        app.setRunning(true);
        while(app.isRunning()) {
            app.showMenu();
        }
    }
}
