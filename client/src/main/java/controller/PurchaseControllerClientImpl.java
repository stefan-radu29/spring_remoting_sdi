package controller;

import domain.Book;
import domain.Client;
import domain.Purchase;
import domain.validators.BookstoreException;
import domain.validators.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import service.PurchaseController;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PurchaseControllerClientImpl implements PurchaseController {
    @Autowired
    private PurchaseController purchaseController;

    public Optional<Purchase> add(Purchase purchase) throws BookstoreException, ValidatorException, SQLException {
        return purchaseController.add(purchase);
    }

    public Optional<Purchase> updatePurchase(Purchase purchase) throws ValidatorException, BookstoreException {
        return purchaseController.updatePurchase(purchase);
    }

    public Optional<Purchase> deletePurchase(Integer id) throws BookstoreException {
        return purchaseController.deletePurchase(id);
    }

    public Set<Purchase> getPurchaseList() throws BookstoreException {
        return purchaseController.getPurchaseList();
    }

    public List<Map.Entry<Client, Double>> reportTop3ClientsBasedOnMoneySpent() throws BookstoreException {
        return purchaseController.reportTop3ClientsBasedOnMoneySpent();
    }

    public List<Map.Entry<Book, Long>> reportTop3BestSellingBooks() throws BookstoreException {
        return purchaseController.reportTop3BestSellingBooks();
    }
}
