package service;

import domain.Client;
import domain.validators.BookstoreException;
import domain.validators.ValidatorException;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface ClientController {
    /**
     * Add a client to the client repository.
     * @param client instance of class Client
     * @throws ValidatorException if the entity is not valid
     */
    public Optional<Client> addClient(Client client) throws ValidatorException, BookstoreException, SQLException;

    /**
     * Get client list set.
     * @return the set containing all the clients inside the client repository
     */
    public Set<Client> getClientList() throws BookstoreException;

    /**
     * Delete a client from the client repository
     * @param id integer representing the id of a client
     */
    public Optional<Client> deleteClient(Integer id) throws BookstoreException;

    /**
     * Update a client from the client repository
     * @param client instance of the class Client
     * @throws ValidatorException if the client is not valid
     */
    public Optional<Client> updateClient(Client client) throws ValidatorException, BookstoreException;

    /**
     * Returns all the clients that contain the searchString in one or more of their attributes.
     * @param searchString string used for filtering the clients
     * @return a HashSet containing clients
     */
    public Set<Client> filter(String searchString) throws BookstoreException;

    public Optional<Client> findOne(Integer id) throws BookstoreException;
}
