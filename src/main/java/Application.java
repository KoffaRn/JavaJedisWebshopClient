import lombok.Getter;
import lombok.Setter;
import models.CartDTO;
import models.OrderDTO;
import models.ProductDTO;
import models.UserDTO;
import service.AuthService;
import service.CartService;
import service.OrderService;
import service.ProductService;

import java.util.*;

public class Application {
    private UserDTO user;
    @Getter
    @Setter
    private boolean running;
    public void showMenu() {
        System.out.println("Welcome to the webshop!");
        Map<String, Runnable> menu = new HashMap<>();
        menu.put("Show all products", showAllProducts());
        if(user == null) {
            menu.put("Login", login());
            menu.put("Register", register());
        }
        if(user != null) {
            menu.put("Show cart", showCart());
            menu.put("Show orders", showOrders());
            menu.put("Logout", () -> user = null);
        }

        printMenu(menu);
    }

    private Runnable showOrders() {
        return () -> {
            List<OrderDTO> orders = OrderService.getAllOrders(user);
            for(int i = 0; i < orders.size(); i++) {
                System.out.println((i + 1) + ". " + orders.get(i).getId());
            }
            System.out.println("0. Back");
            int choice = getIntInput("Enter order number: ");
            if(choice == 0)
                showMenu();
            else showOneOrder(orders.get(choice - 1)).run();
        };
    }

    private Runnable showOneOrder(OrderDTO orderDTO) {
        return () -> {
            for(OrderDTO.OrderItemDTO orderItemDTO : orderDTO.getOrderItems()) {
                System.out.println(orderItemDTO.getProduct().getName() + " x" + orderItemDTO.getQuantity());
            }
        };
    }

    private Runnable showCart() {
        return () -> {
            List<CartDTO.CartItemDTO> cart = ProductService.getCartProducts(user);
            for(int i = 0; i < cart.size(); i++) {
                System.out.println((i + 1) + ". " + cart.get(i).getProduct().getName() + " x" + cart.get(i).getQuantity());
            }
            System.out.println(cart.size() + 1 + ". Buy cart");
            System.out.println("0. Back");
            int choice = getIntInput("Enter product number: ");
            if(choice == 0)
                showMenu();
            if(choice == cart.size() + 1)
                CartService.buyCart(user);
            else
                showOneProduct(cart.get(choice - 1).getProduct()).run();

        };
    }

    private Runnable register() {
        return () -> {
            String userName = getStringInput("Enter username: ");
            String password = getStringInput("Enter password: ");
            try {
                AuthService.register(userName, password);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        };
    }

    private Runnable showAllProducts() {
        List<Runnable> menu = new ArrayList<>();
        List<ProductDTO> productDTOS = ProductService.getAllProducts();
        return () -> {
            for(int i = 0; i < productDTOS.size(); i++) {
                ProductDTO currentProduct = productDTOS.get(i);
                System.out.println((i + 1) + ". " + currentProduct.getName());
                menu.add(showOneProduct(currentProduct));
            }
            menu.add(() -> showMenu());
            int choice = getIntInput("Enter product number: ");
            menu.get(choice - 1).run();
        };
    }

    private Runnable showOneProduct(ProductDTO productDTO) {
        return () -> {
            System.out.println(productDTO);
            if(user != null && isAdmin(user)) {
                adminProductMenu(productDTO);
            }
            else if(user != null && !isAdmin(user)) {
                userProductMenu(productDTO);
            }
        };
    }

    private void userProductMenu(ProductDTO productDTO) {
        System.out.println("1. Add to cart");
        System.out.println("0. Back");
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    addToCart(productDTO);
                    break;
                case 0:
                    break;
                default:
                    showOneProduct(productDTO);
            }
        }
    }

    private void addToCart(ProductDTO productDTO) {
        int quantity = getIntInput("Enter quantity: ");
        System.out.println( CartService.getCart(user));
        CartService.addToCart(productDTO, quantity, user);
    }

    private int getIntInput(String prompt) {
        System.out.println(prompt);
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextInt()) {
            return scanner.nextInt();
        }
        else return getIntInput(prompt);
    }

    private void adminProductMenu(ProductDTO productDTO) {
        System.out.println("1. Edit");
        System.out.println("2. Delete");
        System.out.println("0. Back");
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    editProduct(productDTO);
                    break;
                case 2:
                    deleteProduct(productDTO);
                    break;
                case 0:
                    break;
                default:
                    showOneProduct(productDTO);
            }
        }
    }

    private void deleteProduct(ProductDTO productDTO) {
        ProductService.deleteProduct(productDTO);
    }

    private void editProduct(ProductDTO productDTO) {
        System.out.println("1. Name");
        System.out.println("2. Description");
        System.out.println("3. Price");
        System.out.println("4. Active");
        System.out.println("0. Back");
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    showOneProduct(ProductService.editName(getStringInput("Enter new name: "), productDTO));
                    break;
                case 2:
                    showOneProduct(ProductService.editDescription(getStringInput("Enter new description: "), productDTO));
                    break;
                case 3:
                    showOneProduct(ProductService.editPrice(getDoubleInput("Enter new price: "), productDTO));
                    break;
                case 4:
                    showOneProduct(ProductService.editActive(getBooleanInput("Enter new active: "), productDTO));
                    break;
                case 0:
                    break;
                default:
                    editProduct(productDTO);
            }
        }
    }
    private boolean getBooleanInput(String s) {
        System.out.println(s + " (true/false)");
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextBoolean()) {
            return scanner.nextBoolean();
        }
        else return getBooleanInput(s);
    }

    private double getDoubleInput(String prompt) {
        System.out.println(prompt);
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextDouble()) {
            return scanner.nextDouble();
        }
        else return getDoubleInput(prompt);
    }

    private void printMenu(Map<String, Runnable> menu) {
        ArrayList<String> menuItems = new ArrayList<>(menu.keySet());
        for(int i = 0; i < menuItems.size(); i++) {
            System.out.println((i + 1) + ". " + menuItems.get(i));
        }
        System.out.println("0. Exit");
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            if(choice == 0) {
                running = false;
            }
            else {
                menu.get(menuItems.get(choice - 1)).run();
            }
        }
        else {
            printMenu(menu);
        }
    }

    private Runnable login() {
        return () -> {
            String userName = getStringInput("Enter username: ");
            String password = getStringInput("Enter password: ");
            try {
                user = AuthService.login(userName, password);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        };
    }
    private String getStringInput(String prompt) {
        System.out.println(prompt);
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        else return getStringInput(prompt);
    }

    private boolean isAdmin(UserDTO user) {
        for(UserDTO.Role role : user.getUser().getRoles()) {
            if(role.getAuthority().equals("ADMIN")) return true;
        }
        return false;
    }
}
