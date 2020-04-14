package ui;

import controller.*;
import domain.Book;
import domain.Client;
import domain.Purchase;
import domain.validators.BookstoreException;
import domain.validators.ValidatorException;
import service.BookController;
import service.ClientController;
import service.PurchaseController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Console.
 */
public class Console
{
    private ClientController clientController;
    private BookController bookController;
    private PurchaseController purchaseController;
     /**
     * String representing the text menu that has to be displayed in the console
     */
    private String textMenu;

    /**
     * Instantiates a new Console.
     *
     * @param clientController instance of class Controller
     * @param bookController instance of class Controller
     */
    public Console(ClientController clientController, BookController bookController, PurchaseController purchaseController){
        this.clientController = clientController;
        this.bookController = bookController;
        this.purchaseController = purchaseController;
        this.textMenu = "0. Exit.\n" +
                        "1. Add client.\n" +
                        "2. Add book.\n" +
                        "3. List all clients.\n" +
                        "4. List all books.\n" +
                        "5. Update client.\n" +
                        "6. Update book.\n" +
                        "7. Delete client.\n" +
                        "8. Delete book.\n" +
                        "9. Filter/Partial search clients.\n" +
                        "10. Filter/Partial search books.\n" +
                        "11. Purchase book.\n" +
                        "12. Update purchase\n" +
                        "13. Delete purchase\n" +
                        "14. Get purchases.\n" +
                        "15. Get top 3 clients based on amount of money spent.\n" +
                        "16. Get top 3 best-selling books.\n";
    }

    /**
     * Run the program
     */
    public void run()
    {
        while (true) {
            try {
                System.out.println(this.textMenu);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String choice = reader.readLine();
                switch (choice) {
                    case ("0"):
                        return;
                    case ("1"): {
                        this.addClient();
                        break;
                    }
                    case ("2"): {
                        this.addBook();
                        break;
                    }
                    case ("3"): {
                        this.listAllClients();
                        break;
                    }
                    case ("4"): {
                        this.listAllBooks();
                        break;
                    }
                    case ("5"): {
                        this.updateClient();
                        break;
                    }
                    case("6"): {
                        this.updateBook();
                        break;
                    }
                    case ("7"): {
                        this.deleteClient();
                        break;
                    }
                    case("8"): {
                        this.deleteBook();
                        break;
                    }
                    case ("9"): {
                        this.filterClients();
                        break;
                    }
                    case("10"): {
                        this.filterBooks();
                        break;
                    }
                    case("11"): {
                        this.purchaseBook();
                        break;
                    }
                    case("12"): {
                        this.updatePurchase();
                        break;
                    }
                    case("13"): {
                        this.deletePurchase();
                        break;
                    }
                    case("14"): {
                        this.getPurchases();
                        break;
                    }
                    case("15"): {
                        this.getTop3ClientsBasedOnMoneySpent();
                        break;
                    }
                    case("16"): {
                        this.getTop3BestSellingBooks();
                        break;
                    }
                    default:
                        System.out.println("Not a valid choice!\n");
                }
            }
            catch(IOException | BookstoreException | ValidatorException | NumberFormatException | NullPointerException | SQLException exception)
            {
                System.out.println(exception.toString());
            }
        }
    }

    /**
     * Prints on the screen the top 3 books ordered based sales.
     */
    private void getTop3BestSellingBooks() throws BookstoreException {
        CompletableFuture.supplyAsync(
                () ->{
                    System.out.println("Top 3 books ordered based on sales:");
                    AtomicInteger rank = new AtomicInteger(1);
                    StringBuilder output = new StringBuilder("");
                    try {
                        this.purchaseController.reportTop3BestSellingBooks().forEach(entry ->{
                            output.append(rank).append(". ").append(entry.getKey().toString()).append(" sold ").append(entry.getValue().toString()).append(" times.\n");
                            rank.addAndGet(1);
                        });
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return output;
                }
        )
                .thenAcceptAsync(System.out::println);

    }

    /**
     * Prints on the screen the top 3 clients ordered based on money spent.
     */
    private void getTop3ClientsBasedOnMoneySpent() throws BookstoreException {
        CompletableFuture.supplyAsync(
                () ->{
                    System.out.println("Top 3 clients ordered based on amount of money spent:");
                    AtomicInteger rank = new AtomicInteger(1);
                    StringBuilder output = new StringBuilder("");
                    try {
                        this.purchaseController.reportTop3ClientsBasedOnMoneySpent().forEach(entry ->{
                            output.append(rank).append(". ").append(entry.getKey().toString()).append(" spent ").append(entry.getValue().toString());
                            rank.addAndGet(1);
                        });
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return output;
                }
        )
                .thenAcceptAsync(System.out::println);
    }

    /**
     * Prints on the screen all the purchases.
     */
    private void getPurchases() throws BookstoreException {
        CompletableFuture.supplyAsync(
                ()->{
                    try {
                        return this.purchaseController.getPurchaseList();
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        )
                .thenAcceptAsync(allPurchases -> allPurchases.forEach(System.out::println));
    }

    /**
     * Filters the books based on a string read from the keyboard
     * @throws IOException if there is an error concerning the reading of data from the console
     */
    private void filterBooks() throws IOException, BookstoreException {
        System.out.println("Search for books: ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String searchString = reader.readLine();

        CompletableFuture.supplyAsync(
                ()->
                {
                    try {
                        return this.bookController.filter(searchString);
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        )
                .thenAcceptAsync(filteredBooks -> filteredBooks.forEach(System.out::println));
    }

    /**
     * Reads data from the keyboard and updates an existing book.
     * @throws IOException if there is an error concerning the reading of data from the console
     * @throws ValidatorException if the book created with data read from the console is not valid
     */
    private void updateBook() throws IOException {
        System.out.println("Book{id, title, author, publisher, publicationYear, price}");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int id = Integer.parseInt(reader.readLine());
        String title = reader.readLine();
        String author = reader.readLine();
        String publisher = reader.readLine();
        int publicationYear = Integer.parseInt(reader.readLine());
        float price = Float.parseFloat(reader.readLine());

        Book newBook = new Book(title, author, publisher, publicationYear, price);
        newBook.setId(id);

        CompletableFuture.supplyAsync(
                () -> {
                    Optional<Book> optionalBook = Optional.empty();
                    try {
                        optionalBook = this.bookController.updateBook(newBook);
                    } catch (BookstoreException | ValidatorException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    if (!optionalBook.isPresent())
                        return newBook.toString() + " was updated successfully!\n";
                    else
                        return newBook.toString() + " was not updated!\n";
                }
        )
                .thenAcceptAsync(System.out::println);
    }

    /**
     * Reads data from the keyboard and deletes the book having a certain id.
     * @throws IOException if there is an error concerning the reading of data from the console
     */
    private void deleteBook() throws IOException, BookstoreException {
        System.out.println("Book{id}");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int id = Integer.parseInt(reader.readLine());

        CompletableFuture.supplyAsync(
                () -> {
                    Optional<Book> optionalBook = Optional.empty();
                    try {
                        optionalBook = this.bookController.deleteBook(id);
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    if (!optionalBook.isPresent())
                        return "There is no such book with id=" + id + "!\n";
                    else
                        return optionalBook.get().toString() + " was deleted successfully!\n";

                }
        )
                .thenAcceptAsync(System.out::println);
    }

    /**
     * Filters the clients based on a string read from the keyboard (partial search).
     * @throws IOException if there is an error concerning the reading of data from the console
     */
    private void filterClients() throws IOException, BookstoreException {
        System.out.println("Search for clients: ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String searchString = reader.readLine();

        CompletableFuture.supplyAsync(
                ()->
                {
                    try {
                        return this.clientController.filter(searchString);
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        )
                .thenAcceptAsync(filteredClients -> filteredClients.forEach(System.out::println));
    }

    /**
     * Reads data from the keyboard and deletes the client having a certain id.
     * @throws IOException if there is an error concerning the reading of data from the console
     */
    private void deleteClient() throws IOException, BookstoreException {
        System.out.println("Client{id}");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int id = Integer.parseInt(reader.readLine());

        CompletableFuture.supplyAsync(
                () -> {
                    Optional<Client> optionalClient = Optional.empty();
                    try {
                        optionalClient = this.clientController.deleteClient(id);
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    if (!optionalClient.isPresent())
                        return "There is no such client with id=" + id + "!\n";
                    else
                        return optionalClient.get().toString() + " was deleted successfully!\n";

                }
        )
                .thenAcceptAsync(System.out::println);
    }

    /**
     * Reads data from the keyboard and updates an existing client.
     * @throws IOException if there is an error concerning the reading of data from the console
     * @throws ValidatorException if the book created with data read from the console is not valid
     */
    private void updateClient() throws IOException, ValidatorException, BookstoreException {
        System.out.println("Client{id, firstName, lastName, address}");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int id = Integer.parseInt(reader.readLine());
        String firstName = reader.readLine();
        String lastName = reader.readLine();
        String address = reader.readLine();

        Client newClient = new Client(firstName, lastName, address);
        newClient.setId(id);

        CompletableFuture.supplyAsync(
                () -> {
                    Optional<Client> optionalClient = Optional.empty();
                    try {
                        optionalClient = this.clientController.updateClient(newClient);
                    } catch (BookstoreException | ValidatorException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    if (!optionalClient.isPresent())
                        return newClient.toString() + " was updated successfully!\n";
                    else
                        return newClient.toString() + " was not updated!\n";
                }
        )
                .thenAcceptAsync(System.out::println);

    }

    /**
     * Lists all books stored by the program
     */
    private void listAllBooks() throws BookstoreException {
        CompletableFuture.supplyAsync(
                ()->{
                    try {
                        return this.bookController.getBookList();
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        )
                .thenAcceptAsync(allBooks -> allBooks.forEach(System.out::println));
    }

    /**
     * Lists all clients stored by the program
     */
    private void listAllClients() throws BookstoreException {
        CompletableFuture.supplyAsync(
                ()->{
                    try {
                        return this.clientController.getClientList();
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        )
                .thenAcceptAsync(allClients -> allClients.forEach(System.out::println));
    }

    /**
     * Reads data from the keyboard, creates an object of type Book and stores it
     * @throws IOException if there is an error concerning the reading of data from the console
     * @throws ValidatorException if the book created with data read from the console is not valid
     * @throws BookstoreException
     */
    private void addBook() throws IOException, ValidatorException, BookstoreException, SQLException {
        System.out.println("Book{id, title, author, publisher, publicationYear, price}");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int id = Integer.parseInt(reader.readLine());
        String title = reader.readLine();
        String author = reader.readLine();
        String publisher = reader.readLine();
        int publicationYear = Integer.parseInt(reader.readLine());
        float price = Float.parseFloat(reader.readLine());

        Book newBook = new Book(title, author, publisher, publicationYear, price);
        newBook.setId(id);

        CompletableFuture.supplyAsync(
                () -> {
                    Optional<Book> optionalBook = Optional.empty();
                    try {
                        optionalBook = this.bookController.addBook(newBook);
                    } catch (BookstoreException | ValidatorException | SQLException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    if (!optionalBook.isPresent())
                        return newBook.toString() + " was added successfully!\n";
                    else
                        return newBook.toString() + " was not added!\n";
                }
        )
                .thenAcceptAsync(System.out::println);
    }

    /**
     * Reads data from the keyboard, creates an object of type Client and stores it
     * @throws IOException if there is an error concerning the reading of data from the console
     * @throws ValidatorException if the client created with data read from the console is not valid
     * @throws BookstoreException
     */
    private void addClient() throws IOException{
        System.out.println("Client{id, firstName, lastName, address}");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int id = Integer.parseInt(reader.readLine());
        String firstName = reader.readLine();
        String lastName = reader.readLine();
        String address = reader.readLine();

        Client newClient = new Client(firstName, lastName, address);
        newClient.setId(id);

        CompletableFuture.supplyAsync(
                () -> {
                    Optional<Client> optionalClient = Optional.empty();
                    try {
                        optionalClient = this.clientController.addClient(newClient);
                    } catch (BookstoreException | ValidatorException | SQLException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    if (!optionalClient.isPresent())
                        return newClient.toString() + " was added successfully!\n";
                    else
                        return newClient.toString() + " was not added!\n";
                }
        )
                .thenAcceptAsync(System.out::println);
    }

    /**
     * Reads data from the keyboard, creates an object of type Purchase and stores it
     * @throws IOException if there is an error concerning the reading of data from the console
     * @throws ValidatorException if the client created with data read from the console is not valid
     * @throws BookstoreException
     */
    private void purchaseBook() throws IOException, BookstoreException, ValidatorException, SQLException {
        System.out.println("Client{id} Book{id} Purchase{library}");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int idClient = Integer.parseInt(reader.readLine());
        int idBook = Integer.parseInt(reader.readLine());
        String library = reader.readLine();

        Purchase newPurchase = new Purchase(idClient, idBook, library);

        CompletableFuture.supplyAsync(
                () -> {
                    Optional<Purchase> optionalPurchase = Optional.empty();
                    try {
                        optionalPurchase = this.purchaseController.add(newPurchase);
                    } catch (BookstoreException | ValidatorException | SQLException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    if (!optionalPurchase.isPresent())
                        return newPurchase.toString() + " was added successfully!\n";
                    else
                        return newPurchase.toString() + " was not added!\n";
                }
        )
                .thenAcceptAsync(System.out::println);
    }

    /**
     * Reads data from the keyboard and updates an existing purchase
     * @throws IOException if there is an error concerning the reading of data from the console
     * @throws ValidatorException if the client created with data read from the console is not valid
     * @throws BookstoreException
     */
    private void updatePurchase() throws IOException, ValidatorException, BookstoreException {
        System.out.println("Purchase{id} Client{id} Book{id} Purchase{library}");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int id = Integer.parseInt(reader.readLine());
        int idClient = Integer.parseInt(reader.readLine());
        int idBook = Integer.parseInt(reader.readLine());
        String library = reader.readLine();

        Purchase newPurchase = new Purchase(idClient, idBook, library);
        newPurchase.setId(id);

        CompletableFuture.supplyAsync(
                () -> {
                    Optional<Purchase> optionalPurchase = Optional.empty();
                    try {
                        optionalPurchase = this.purchaseController.updatePurchase(newPurchase);
                    } catch (BookstoreException | ValidatorException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    if (!optionalPurchase.isPresent())
                        return newPurchase.toString() + " was updated successfully!\n";
                    else
                        return newPurchase.toString() + " was not updated!\n";
                }
        )
                .thenAcceptAsync(System.out::println);
    }

    private void deletePurchase() throws IOException, BookstoreException {
        System.out.println("Purchase{id}");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int id = Integer.parseInt(reader.readLine());


        CompletableFuture.supplyAsync(
                () -> {
                    Optional<Purchase> optionalPurchase = Optional.empty();
                    try {
                        optionalPurchase = this.purchaseController.deletePurchase(id);
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    if (!optionalPurchase.isPresent())
                        return "There is no such purchase with id=" + id + "!\n";
                    else
                        return optionalPurchase.get().toString() + " was deleted successfully!\n";

                }
        )
                .thenAcceptAsync(System.out::println);
    }
}