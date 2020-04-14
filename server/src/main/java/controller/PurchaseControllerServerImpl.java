package controller;


import domain.Book;
import domain.Client;
import domain.Purchase;
import domain.validators.BookstoreException;
import domain.validators.Validator;
import domain.validators.ValidatorException;
import repository.Repository;
import service.PurchaseController;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 */
public class PurchaseControllerServerImpl implements PurchaseController
{
    private BookControllerServerImpl bookControllerServerImpl;
    private ClientControllerServerImpl clientControllerServerImpl;
    private Repository<Integer, Purchase> purchaseRepository;
    private Validator<Purchase> purchaseValidator;

    public PurchaseControllerServerImpl(ClientControllerServerImpl clientControllerServerImpl, BookControllerServerImpl bookControllerServerImpl, Repository<Integer,
            Purchase> purchaseRepository, Validator<Purchase> purchaseValidator)
    {
        this.clientControllerServerImpl = clientControllerServerImpl;
        this.bookControllerServerImpl = bookControllerServerImpl;
        this.purchaseRepository = purchaseRepository;
        this.purchaseValidator = purchaseValidator;
    }

    /**
     * Checks if the book and client ids related to the purchase exist
     * @param purchase instance of the class Purchase
     * @return True if both the Client and the Book related to the purchase exist, false otherwise
     */
    private Boolean checkBookClientAvailability(Purchase purchase) throws BookstoreException {
        Optional<Client> optionalClient = this.clientControllerServerImpl.findOne(purchase.getClientId());
        Optional<Book> optionalBook = this.bookControllerServerImpl.findOne(purchase.getBookId());
        return optionalClient.isPresent() && optionalBook.isPresent();
    }

    /**
     * Function returns the first available id for a new purchase
     * @return First available id
     */
    private Integer getAvailableId() throws BookstoreException {
        Set<Purchase> purchaseSet = getPurchaseList();
        //List<Integer> purchaseIdList= purchaseSet.stream().map(Purchase::getId).collect(Collectors.toList());
        if(purchaseSet.size() > 0)
            return Collections.max(purchaseSet.stream().map(Purchase::getId).collect(Collectors.toList())) + 1;
        else
            return 1;
    }


    /**
     * Adds a purchase from the purchase repository
     * @param purchase instance of the class Purchase
     * @throws ValidatorException if the purchase is not valid
     * @throws BookstoreException if the book or client doesnt exist
     */
    public Optional<Purchase> add(Purchase purchase) throws BookstoreException, ValidatorException, SQLException {
        if(checkBookClientAvailability(purchase)) {
            purchaseValidator.validate(purchase);
            purchase.setId(getAvailableId());
            return this.purchaseRepository.save(purchase);
        }
        else
            throw new BookstoreException("Invalid book id and/or client id!\n");
    }

    /**
     * Get purchase list set
     * @return the set containing all the purchases inside the purchase repository
     */
    public Set<Purchase> getPurchaseList() throws BookstoreException {
        Iterable<Purchase> purchaseList = this.purchaseRepository.findAll();
        return StreamSupport.stream(purchaseList.spliterator(), false).collect(Collectors.toSet());
    }

    /**
     * Updates a purchase from the purchase repository
     * @param purchase instance of the class Purchase
     * @throws ValidatorException if the purchase is not valid
     * @throws BookstoreException if the book or client doesnt exist
     */
    public Optional<Purchase> updatePurchase(Purchase purchase) throws ValidatorException, BookstoreException {
        if(checkBookClientAvailability(purchase)) {
            purchaseValidator.validate(purchase);
            return this.purchaseRepository.update(purchase);
        }
        else
            throw new BookstoreException("Invalid book id and/or client id!\n");
    }

    /**
     * Deletes a purchase from the purchase repository
     * @param id integer representing the id of the purchase to be deleted
     */
    public Optional<Purchase> deletePurchase(Integer id) throws BookstoreException {
        return this.purchaseRepository.delete(id);
    }

    /**
     * Function takes a clientId and deletes the purchase having the respective clientId
     * @param clientId integer representing the id of the client to be deleted
     */
    public void deletePurchaseWithClientID(int clientId) throws BookstoreException {
        Set<Purchase> purchaseList= getPurchaseList();
        purchaseList.forEach(purchase ->{
            try {
                if (purchase.getClientId() == clientId)
                    this.deletePurchase(purchase.getId());
            } catch (BookstoreException e) {
                    e.printStackTrace();
                }
            });
    }

    /**
     * Function takes a bookId and deletes the purchase having the respective bookId
     * @param bookId integer representing the id of the book to be deleted
     */
    public void deletePurchaseWithBookID(int bookId) throws BookstoreException {
        Set<Purchase> purchaseList = getPurchaseList();
        purchaseList.forEach(purchase ->{
            try{
                if (purchase.getBookId() == bookId)
                    this.deletePurchase(purchase.getId());
            } catch (BookstoreException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Returns the total amount of money a client has spent on books
     * @param clientId integer
     * @return a double representing the amount of money the client with clientId has spent
     */
    public double getMoneySpentForClient(int clientId) throws BookstoreException {
        return StreamSupport.stream(this.purchaseRepository.findAll().spliterator(), false)
                .filter(purchase -> purchase.getClientId() == clientId)
                .map(purchase -> purchase.getBookId())
                .map(bookId -> {
                    try {
                        return this.bookControllerServerImpl.findOne(bookId);
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .map(bookOptional -> bookOptional.orElse(null))
                .filter(Objects::nonNull)
                .map(book -> book.getPrice())
                .mapToDouble(floatPrice -> Double.parseDouble(((Number) floatPrice).toString()))
                .sum();
    }

    /**
     * Returns top 3 clients, sorted based on amount of money spent.
     * @return a list containing 3 clients or less than 3 if there are less than 3 clients
     */
    public List<Map.Entry<Client, Double>> reportTop3ClientsBasedOnMoneySpent() throws BookstoreException {
        List<Map.Entry<Client, Double>> sortedClientsBasedOnMoneySpent = this.clientControllerServerImpl.getClientList().stream()
                .map(client -> {
                    try {
                        return new AbstractMap.SimpleEntry<Client, Double> (client, getMoneySpentForClient(client.getId()));
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .sorted(Comparator.comparingDouble(entry ->  entry.getValue()))
                .collect(Collectors.toList());

        Collections.reverse(sortedClientsBasedOnMoneySpent);

        if(sortedClientsBasedOnMoneySpent.size() > 3)
            return sortedClientsBasedOnMoneySpent.subList(0,3);
        else
            return sortedClientsBasedOnMoneySpent;
    }

    /**
     * Returns how many times a book was sold.
     * @param bookId integer
     * @return an integer representing the sales of the book with bookId
     */
    public long getBookSales(int bookId) throws BookstoreException {
        return StreamSupport.stream(this.purchaseRepository.findAll().spliterator(), false)
                .filter(purchase -> purchase.getBookId() == bookId)
                .count();
    }

    /**
     * Returns top 3 books, sorted based on sales.
     * @return a list containing 3 books or less than 3 if there are less than 3 books
     */
    public List<Map.Entry<Book, Long>> reportTop3BestSellingBooks() throws BookstoreException {
        List<Map.Entry<Book, Long>> sortedBooksBasedOnSales = this.bookControllerServerImpl.getBookList().stream()
                .map(book -> {
                    try {
                        return new AbstractMap.SimpleEntry<Book, Long> (book, getBookSales(book.getId()));
                    } catch (BookstoreException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .sorted(Comparator.comparingLong(entry -> {
                    assert entry != null;
                    return entry.getValue();
                }))
                .collect(Collectors.toList());

        Collections.reverse(sortedBooksBasedOnSales);

        if(sortedBooksBasedOnSales.size() > 3) {
            List<Map.Entry<Book, Long>> result = new ArrayList<>(sortedBooksBasedOnSales.subList(0, 3));
            return result;
        }
        else
            return sortedBooksBasedOnSales;

    }
}
