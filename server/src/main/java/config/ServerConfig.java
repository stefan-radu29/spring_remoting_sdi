package config;

import controller.BookControllerServerImpl;
import controller.ClientControllerServerImpl;
import controller.PurchaseControllerServerImpl;
import domain.Book;
import domain.Client;
import domain.Purchase;
import domain.validators.BookValidator;
import domain.validators.ClientValidator;
import domain.validators.PurchaseValidator;
import domain.validators.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;
import repository.*;
import service.BookController;
import service.ClientController;
import service.PurchaseController;

@Configuration
public class ServerConfig
{
    @Bean
    RmiServiceExporter rmiClientServiceExporter() {
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("ClientController");
        rmiServiceExporter.setServiceInterface(ClientController.class);
        rmiServiceExporter.setService(clientController());
        return rmiServiceExporter;
    }

    @Bean
    ClientController clientController() {
        Validator<Client> clientValidator = new ClientValidator();
        return new ClientControllerServerImpl(clientRepository(), clientValidator);
    }

    @Bean
    RmiServiceExporter rmiBookServiceExporter() {
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("BookController");
        rmiServiceExporter.setServiceInterface(BookController.class);
        rmiServiceExporter.setService(bookController());
        return rmiServiceExporter;
    }

    @Bean
    BookController bookController() {
        Validator<Book> bookValidator = new BookValidator();
        return new BookControllerServerImpl(bookRepository(), bookValidator);
    }

    @Bean
    RmiServiceExporter rmiPurchaseServiceExporter() {
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("PurchaseController");
        rmiServiceExporter.setServiceInterface(PurchaseController.class);
        rmiServiceExporter.setService(purchaseController());
        return rmiServiceExporter;
    }

    @Bean
    PurchaseController purchaseController() {
        Validator<Purchase> purchaseValidator = new PurchaseValidator();
        return new PurchaseControllerServerImpl((ClientControllerServerImpl)clientController(), (BookControllerServerImpl)bookController(), purchaseRepository(), purchaseValidator);
    }

    @Bean
    ClientRepository clientRepository()
    {
        return new ClientDatabaseRepository();
    }

    @Bean
    BookRepository bookRepository()
    {
        return new BookDatabaseRepository();
    }

    @Bean
    PurchaseRepository purchaseRepository()
    {
        return new PurchaseDatabaseRepository();
    }
}
