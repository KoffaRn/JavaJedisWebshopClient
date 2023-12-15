public class Main {
    public static void main(String[] args) {
        Application app = new Application();
        app.setRunning(true);
        while(app.isRunning()) {
            app.showMenu();
        }
    }
}
