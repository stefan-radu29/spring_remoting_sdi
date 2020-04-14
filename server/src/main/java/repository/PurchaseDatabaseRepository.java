package repository;

import domain.Client;
import domain.Purchase;
import domain.validators.BookstoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PurchaseDatabaseRepository class
 */
public class PurchaseDatabaseRepository implements PurchaseRepository{

    @Autowired
    private JdbcOperations jdbcOperations;
    /**
     * Gets the purchase having a certain id from the database.
     * @param id must be not null.
     * @return an Optional containing an instance of Purchase if a purchase was found, Optional with null otherwise
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Optional<Purchase> findOne(Integer id) throws BookstoreException {
        String getPurchaseSqlStatement = "select * from purchase where id=?";

        try {
            List<Purchase> resultList = jdbcOperations.query(getPurchaseSqlStatement, new Object[]{id}, (row, rowNum) ->
            {
                int purchaseId = row.getInt("id");
                int clientId = row.getInt("clientid");
                int bookId = row.getInt("bookid");
                String library = row.getString("library");
                Purchase newPurchase = new Purchase(clientId, bookId, library);
                newPurchase.setId(purchaseId);
                return newPurchase;
            });

            if (!resultList.isEmpty())
            {
                return Optional.of(resultList.get(0));
            }
            else
            {
                return Optional.empty();
            }
        }
        catch (DataAccessException databaseException)
        {
            throw new BookstoreException(databaseException.toString());
        }
    }


    /**
     * Gets all the purchases from the database.
     * @return an Iterable instance containing all the purchases in the database
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Iterable<Purchase> findAll() throws BookstoreException
    {
        String sqlStatement = "select * from purchase";

        try
        {
            return jdbcOperations.query(sqlStatement,  (row, rowNum) ->
            {
                int purchaseId = row.getInt("id");
                int clientId = row.getInt("clientid");
                int bookId = row.getInt("bookid");
                String library = row.getString("library");
                Purchase newPurchase = new Purchase(clientId, bookId, library);
                newPurchase.setId(purchaseId);
                return newPurchase;
            });
        }
        catch(DataAccessException databaseException)
        {
            throw new BookstoreException(databaseException.toString());
        }
    }

    /**
     * Save a purchase into the database.
     * @param entity must not be null, instance of Purchase
     * @return Optional containing null if the purchase was added, Optional with the purchase otherwise
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Optional<Purchase> save(Purchase entity) throws BookstoreException{
        String addPurchaseSqlStatement = "insert into purchase (id, clientid, bookid, library) values (?, ?, ?, ?)";

        try
        {
            int rowsUpdated = jdbcOperations.update(addPurchaseSqlStatement, entity.getId(), entity.getClientId(), entity.getBookId(), entity.getLibrary());
            if(rowsUpdated == 0)
            {
                return Optional.of(entity);
            }
            else
            {
                return Optional.empty();
            }
        }
        catch (DataAccessException databaseException)
        {
            throw new BookstoreException(databaseException.toString());
        }
    }


    /**
     * Deletes a purchase from the database.
     * @param id must not be null, integer representing the id of a purchase.
     * @return Optional containing the deleted purchase if there was a purchase with such an id, Optional with null otherwise
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Optional<Purchase> delete(Integer id) throws BookstoreException {
        String deletePurchaseSqlStatement = "delete from purchase where id=?";

        try
        {
            Optional<Purchase> purchaseToDelete = this.findOne(id);

            if (purchaseToDelete.isPresent())
            {
                jdbcOperations.update(deletePurchaseSqlStatement, id);
                return purchaseToDelete;
            }
            else
            {
                return Optional.empty();
            }
        }
        catch(DataAccessException databaseException)
        {
            throw new BookstoreException(databaseException.toString());
        }
    }


    /**
     * Updates a purchase from the database.
     * @param entity must not be null, instance of class Purchase
     * @return Optional with the updated purchase if such a purchase exists, Optional with null otherwise
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Optional<Purchase> update(Purchase entity) throws BookstoreException {
        String updatePurchaseSqlStatement = "update purchase set clientid = ?, bookid = ?, library = ? where id = ?";

        try
        {
            int rowsUpdated = jdbcOperations.update(updatePurchaseSqlStatement, entity.getClientId(), entity.getBookId(),
                    entity.getLibrary(), entity.getId());
            if(rowsUpdated == 0)
            {
                return Optional.empty();
            }
            else
            {
                return Optional.of(entity);
            }
        }
        catch (DataAccessException databaseException)
        {
            throw new BookstoreException(databaseException.toString());
        }
    }
}
