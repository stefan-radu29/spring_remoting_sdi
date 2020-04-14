package repository;

import domain.Purchase;
import domain.validators.BookstoreException;
import domain.validators.Validator;
import domain.validators.ValidatorException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PurchaseFileRepository extends InMemoryRepository<Integer, Purchase> {
    protected String fileName;

    public PurchaseFileRepository(Validator<Purchase> validator, String fileName) throws BookstoreException {
        this.fileName = fileName;

        this.loadData();
    }

    /**
     * Loads data into the repository from a CSV file.
     */
    protected void loadData() throws BookstoreException {
        Path path = Paths.get(fileName);

        try
        {
            Files.lines(path).forEach(line -> {
                if(!(line.equals("") || line.equals("\n")))
                {
                    List<String> items = Arrays.asList(line.split(","));

                    Integer id = Integer.valueOf(items.get(0));
                    int idClient = Integer.valueOf(items.get(1));
                    int idBook = Integer.valueOf(items.get(2));
                    String library = items.get(3);

                    Purchase purchase = new Purchase(idClient, idBook, library);
                    purchase.setId(id);

                    try {
                        super.save(purchase);
                    } catch (IllegalArgumentException | BookstoreException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Saves all the data from the local repository in a CSV text file.
     */
    protected void saveData() throws BookstoreException {
        try (FileWriter fileWriter = new FileWriter(fileName, false))
        {
            super.entities.forEach((key, value) -> {
                try {
                    fileWriter.write(
                            value.getId() + "," + value.getClientId() + "," + value.getBookId() + "," + value.getLibrary()+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        catch(IOException e)
        {
            e.printStackTrace();
        };
    }

    /**
     * Saves a purchase to the file repository
     * @param purchase instance of class Purchase
     * @return instance of Optional that could contain a purchase or null
     * @throws domain.validators.ValidatorException if the purchase is not valid
     */
    @Override
    public Optional<Purchase> save(Purchase purchase) throws BookstoreException {
        Optional<Purchase> optionalPurchase = super.save(purchase);
        if (optionalPurchase.isPresent())
            return optionalPurchase;
        saveData();
        return Optional.empty();
    }

    /**
     * Deletes a purchase from the file repository
     * @param id integer representing the id of a purchase
     * @return instance of Optional that could contain a purchase or null
     */
    @Override
    public Optional<Purchase> delete(Integer id) throws BookstoreException {
        Optional<Purchase> optionalPurchase = super.delete(id);
        if (!optionalPurchase.isPresent())
            return optionalPurchase;
        saveData();
        return optionalPurchase;
    }

    /**
     * Updates a purchase from the file repository.
     * @param purchase instance of class Purchase
     * @return instance of Optional that could contain a Purchase or null
     */
    @Override
    public Optional<Purchase> update(Purchase purchase) throws BookstoreException {
        Optional<Purchase> optionalPurchase = super.update(purchase);
        if (!optionalPurchase.isPresent())
            return optionalPurchase;
        saveData();
        return optionalPurchase;
    }

}
