package repository;

import domain.BaseEntity;
import domain.Client;
import domain.validators.BookstoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ClientDatabaseRepository class
 */
public class ClientDatabaseRepository implements ClientRepository{

    @Autowired
    private JdbcOperations jdbcOperations;

    /**
     * Gets the client having a certain id from the database.
     * @param id must be not null.
     * @return an Optional containing an instance of Client if a client was found, Optional with null otherwise
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Optional<Client> findOne(Integer id) throws BookstoreException {
        String getClientSqlStatement = "select * from client where id=?";

        try {
            List<Client> resultList = jdbcOperations.query(getClientSqlStatement, new Object[]{id}, (row, rowNum) -> {
                int clientId = row.getInt("id");
                String firstName = row.getString("firstname");
                String lastName = row.getString("lastname");
                String address = row.getString("address");
                Client newClient = new Client(firstName, lastName, address);
                newClient.setId(clientId);
                return newClient;
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
     * Gets all the clients from the database.
     * @return an Iterable instance containing all the clients in the database
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Iterable<Client> findAll() throws BookstoreException {
        String sqlStatement = "select * from client";

        try {
            return jdbcOperations.query(sqlStatement,(row, rowNum) -> {
                int clientId = row.getInt("id");
                String firstName = row.getString("firstname");
                String lastName = row.getString("lastname");
                String address = row.getString("address");
                Client newClient = new Client(firstName, lastName, address);
                newClient.setId(clientId);
                return newClient;
            });
        }
        catch(DataAccessException databaseException)
        {
            throw new BookstoreException(databaseException.toString());
        }
    }

    /**
     * Save a client into the database.
     * @param entity must not be null, instance of Client
     * @return Optional containing null if the client was added, Optional with the client otherwise
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Optional<Client> save(Client entity) throws BookstoreException{
        String addClientSqlStatement = "insert into client (id, firstname, lastname, address) values (?, ?, ?, ?)";

        try
        {
            int rowsUpdated = jdbcOperations.update(addClientSqlStatement, entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getAddress());
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
     * Deletes a client from the database.
     * @param id must not be null, integer representing the id of a client
     * @return Optional containing the deleted client if there was a client with such an id, Optional with null otherwise
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Optional<Client> delete(Integer id) throws BookstoreException {
        String deleteClientSqlStatement = "delete from client where id=?";

        try
        {
            Optional<Client> clientToDelete = this.findOne(id);

            if (clientToDelete.isPresent())
            {
                jdbcOperations.update(deleteClientSqlStatement, id);
                return clientToDelete;
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
     * Updates a client from the database.
     * @param entity must not be null, instance of class Client
     * @return Optional with the updated client if such a client exists, Optional with null otherwise
     * @throws BookstoreException if there is any SQL database related error
     */
    @Override
    public Optional<Client> update(Client entity) throws BookstoreException {
        String updateClientSqlStatement = "update client set firstname = ?, lastname = ?, address = ? where id = ?";

        try
        {
            int rowsUpdated = jdbcOperations.update(updateClientSqlStatement, entity.getFirstName(), entity.getLastName(), entity.getAddress(), entity.getId());
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
