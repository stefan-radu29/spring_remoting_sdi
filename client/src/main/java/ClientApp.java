import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import service.*;
import ui.Console;

public class ClientApp {
    public static void main(String[] args){
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(
                        "config"
                );

        ClientController clientController = context.getBean(ClientController.class);
        BookController bookController = context.getBean(BookController.class);
        PurchaseController purchaseController = context.getBean(PurchaseController.class);

        Console console = new Console(clientController, bookController, purchaseController);
        console.run();
    }
}
