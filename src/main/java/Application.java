import lombok.Getter;
import lombok.Setter;
import models.CartDTO;
import models.OrderDTO;
import models.ProductDTO;
import models.UserDTO;
import service.*;

import java.util.*;

public class Application {
    private UserDTO user;
    @Getter
    @Setter
    private boolean running;

    public void showMenu() {
        System.out.println("Welcome to the web shop!");
        Map<String, Runnable> menu = new HashMap<>();
        menu.put("Show all products", showAllProducts());
        if (user == null) {
            menu.put("Login", login());
            menu.put("Register", register());
        }
        if (isAdmin(user)) {
            menu.put("Show all users", showAllUsers());
            menu.put("Show all orders", showOrders());
        }
        if (user != null) {
            menu.put("Show cart", showCart());
            menu.put("Show orders", showOrders());
            menu.put("Show user info", changeUser());
            menu.put("Logout", () -> user = null);
        }
        printMenu(menu);
    }

    private Runnable showAllUsers() {
        return () -> {
            List<UserDTO.User> users = new ArrayList<>();
            try {
                users = UserService.getAllUsers(user.getJwt());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i).getUsername());
            }
            System.out.println("0. Back");
            int choice = getIntInput("Enter user number: ");
            if (choice == 0)
                showMenu();
            else if (choice <= users.size())
                adminShowOneUser(users.get(choice - 1)).run();
            else showAllUsers().run();
        };
    }

    private Runnable adminShowOneUser(UserDTO.User userToChange) {
        System.out.println(userToChange);
        return () -> {
            if (isAdmin(user)) {
                adminUserMenu(userToChange);
            }
        };
    }

    private void adminUserMenu(UserDTO.User userToChange) {
        Arrays.asList("1. Edit", "2. Delete", "0. Back").forEach(System.out::println);
        int choice = getIntInput("Enter choice: ");
        switch (choice) {
            case 1:
                adminEditUser(userToChange);
                break;
            case 2:
                adminDeleteUser(userToChange);
                break;
            case 0:
                break;
            default:
                adminUserMenu(userToChange);
        }
    }

    private void adminEditUser(UserDTO.User userDTO) {
        System.out.println("1. Username");
        System.out.println("2. Password");
        System.out.println("0. Back");
        int choice = getIntInput("Enter choice: ");
        switch (choice) {
            case 1:
                adminEditUsername(userDTO);
                break;
            case 2:
                adminEditPassword(userDTO);
                break;
            case 0:
                break;
            default:
                adminEditUser(userDTO);
        }
    }

    private void adminEditPassword(UserDTO.User userDTO) {
        try{
            UserService.changePassword(user.getJwt(), userDTO.getId(), getStringInput("Enter new password: "));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void adminEditUsername(UserDTO.User userDTO) {
        try{
            UserService.changeUsername(user.getJwt(), userDTO.getId(), userDTO.getUsername(), getStringInput("Enter new username: "));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void adminDeleteUser(UserDTO.User userDTO) {
        try {
            UserService.deleteUser(user.getJwt(), userDTO.getId());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private Runnable changeUser() {
        return () -> {
            System.out.println(user.getUser());
            System.out.println("Token: " + user.getJwt());
            System.out.println("1. Delete user");
            System.out.println("2. Change password");
            System.out.println("0. Back");
            int choice = getIntInput("Enter choice: ");
            switch (choice) {
                case 1: {
                    deleteUser();
                    user = null;
                    break;
                }
                case 2: {
                    changePassword();
                    break;
                }
                case 0:
                    break;
                default:
                    changeUser().run();
            }
        };
    }

    private void deleteUser() {
        try {
            UserService.deleteUser(user.getJwt(), user.getUser().getId());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void changePassword() {
        String newPassword = getStringInput("Enter new password: ");
        try {
            UserService.changePassword(user.getJwt(), user.getUser().getId(), newPassword);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    private Runnable showOrders() {
        return () -> {
            List<OrderDTO> orders = new ArrayList<>();
            try{
                orders = OrderService.getAllOrders(user.getJwt(), user.getUser().getId());
            } catch (Exception e) {
                System.err.println("Error getting data from API : " + e.getMessage());
            }
            for(int i = 0; i < orders.size(); i++) {
                System.out.println((i + 1) + ". " + "Order nr. : " + orders.get(i).getId());
            }
            System.out.println("0. Back");
            int choice = getIntInput("Enter order number: ");
            if(choice == 0)
                showMenu();
            else if(choice <= orders.size())
                showOneOrder(orders.get(choice - 1)).run();
            else showOrders().run();
        };
    }

    private Runnable showOneOrder(OrderDTO orderDTO) {
        return () -> {
            double runningTotal = 0;
            for(OrderDTO.OrderItemDTO orderItemDTO : orderDTO.getProducts()) {
                System.out.println(orderItemDTO.getProduct().getName() + " x" + orderItemDTO.getQuantity());
                runningTotal += orderItemDTO.getProduct().getPrice() * orderItemDTO.getQuantity();
            }
            System.out.println("Total: " + runningTotal);
            System.out.println("Press enter to continue");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
        };
    }

    private Runnable showCart() {
        return () -> {
            List<CartDTO.CartItemDTO> cart = new ArrayList<>();
            try {
                cart = ProductService.getCartProducts(user);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            double runningTotal = 0;
            for (CartDTO.CartItemDTO cartItemDTO : cart) {
                double price = cartItemDTO.getProduct().getPrice() * cartItemDTO.getQuantity();
                runningTotal += price;
                System.out.println(cartItemDTO.getProduct().getName() + " x" + cartItemDTO.getQuantity() + ", " + price);
            }
            System.out.println("Total: " + runningTotal);
            System.out.println("1. Buy cart");
            System.out.println("0. Back");
            int choice = getIntInput("Enter choice: ");
            if(choice == 0)
                showMenu();
            if(choice == 1)
                try {
                    CartService.buyCart(user.getJwt(), user.getUser().getId());
                } catch (Exception e) {
                    System.err.println("Error getting data from API: " + e.getMessage());
                }
            else showCart().run();
        };
    }

    private Runnable register() {
        return () -> {
            String userName = getStringInput("Enter username: ");
            String password = getStringInput("Enter password: ");
            try {
                boolean success = AuthService.register(userName, password);
                if(success) System.out.println("Registered successfully");
                else System.err.println("Registration failed");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        };
    }

    private Runnable showAllProducts() {
        return () -> {
            List<Runnable> productMenu = new ArrayList<>();
            List<ProductDTO> productDTOS = new ArrayList<>();
            try {
                if(isAdmin(user)) productDTOS = ProductService.adminGetAllProducts(user.getJwt());
                else productDTOS = ProductService.getAllProducts();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            for(int i = 0; i < productDTOS.size(); i++) {
                ProductDTO currentProduct = productDTOS.get(i);
                System.out.println((i + 1) + ". " + currentProduct.getName());
                productMenu.add(showOneProduct(currentProduct));
            }
            if(isAdmin(user)) {
                System.out.println((productDTOS.size() + 1) + ". Create new product");
                productMenu.add(createProduct());
            }
            System.out.println("0. Back");
            productMenu.add(this::showMenu);
            int choice = getIntInput("Enter choice: ");
            if(choice == 0)
                showMenu();
            else if(choice <= productMenu.size())
                productMenu.get(choice - 1).run();
            else showAllProducts().run();
        };
    }

    private Runnable createProduct() {
        return () -> {
            String name = getStringInput("Enter name: ");
            String description = getStringInput("Enter description: ");
            double price = getDoubleInput("Enter price: ");
            try {
                boolean success = ProductService.createProduct(user.getJwt(), name, description, price);
                if(success) System.out.println("Product created successfully");
                else System.err.println("Product creation failed");
            } catch (Exception e) {
                System.err.println("Error getting data from API: " + e.getMessage());
            }
        };
    }

    private Runnable showOneProduct(ProductDTO productDTO) {
        return () -> {
            System.out.println(productDTO);
            if(isAdmin(user)) {
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
                    showOneProduct(productDTO).run();
            }
        }
    }

    private void addToCart(ProductDTO productDTO) {
        int quantity = getIntInput("Enter quantity: ");
        try {
            CartService.addToCart(user.getJwt(), productDTO.getId(), quantity, user.getUser().getId());
        } catch (Exception e) {
            System.err.println("Error getting data from API: " + e.getMessage());
        }
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
                    showOneProduct(productDTO).run();
            }
        }
    }

    private void deleteProduct(ProductDTO productDTO) {
        try {
            ProductService.deleteProduct(user.getJwt(), productDTO.getId());
        } catch (Exception e) {
            System.err.println("Not deleted, probably present in carts or orders.");
        }
    }

    private void editProduct(ProductDTO productDTO) {
        System.out.println("1. Name");
        System.out.println("2. Description");
        System.out.println("3. Price");
        System.out.println("4. Active (false for soft delete)");
        System.out.println("0. Back");
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    try {
                        showOneProduct(ProductService.editName(user.getJwt(), getStringInput("Enter new name: "), productDTO.getId())).run();
                    } catch (Exception e) {
                        System.err.println("Not edited, probably present in carts or orders.");
                    }
                    break;
                case 2:
                    try {
                        showOneProduct(ProductService.editDescription(user.getJwt(), getStringInput("Enter new description: "), productDTO.getId())).run();
                    } catch (Exception e) {
                        System.err.println("Not edited, probably present in carts or orders.");
                    }
                    break;
                case 3:
                    try {
                        showOneProduct(ProductService.editPrice(user.getJwt(), getDoubleInput("Enter new price: "), productDTO.getId())).run();
                    } catch (Exception e) {
                        System.err.println("Not edited, probably present in carts or orders.");
                    }
                    break;
                case 4:
                    try {
                        showOneProduct(ProductService.editActive(user.getJwt(), getBooleanInput("Enter new active: "), productDTO.getId())).run();
                    } catch (Exception e) {
                        System.err.println("Not edited, probably present in carts or orders.");
                    }
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
            if(choice == 0)
                running = false;
            else if (choice <= menuItems.size())
                menu.get(menuItems.get(choice - 1)).run();
            else
                printMenu(menu);
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
                System.err.println("Could not login, try again");
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
        if(user == null) return false;
        for(UserDTO.Role role : user.getUser().getRoles()) {
            if(role.getAuthority().equals("ADMIN")) return true;
        }
        return false;
    }
}
