package controller;

import domain.Client;
import domain.validators.BookstoreException;
import domain.validators.Validator;
import domain.validators.ValidatorException;
import repository.Repository;
import service.ClientController;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The type ClientController.
 */
public class ClientControllerServerImpl implements ClientController {

    private Repository<Integer, Client> clientRepository;
    private Validator<Client> clientValidator;

    /**
     * Instantiates a new Controller.
     *
     * @param clientRepository instance of class implementing the Repository interface representing the client repository
     */
    public ClientControllerServerImpl(Repository<Integer, Client> clientRepository, Validator<Client> clientValidator){
        this.clientRepository = clientRepository;
        this.clientValidator = clientValidator;
    }

    /**
     * Add a client to the client repository.
     * @param client instance of class Client
     * @throws ValidatorException if the entity is not valid
     */
    public Optional<Client> addClient(Client client) throws ValidatorException, BookstoreException, SQLException {
        clientValidator.validate(client);
        return this.clientRepository.save(client);
    }

    /**
     * Get client list set.
     * @return the set containing all the clients inside the client repository
     */
    public Set<Client> getClientList() throws BookstoreException {
        Iterable<Client> clientList = this.clientRepository.findAll();
        return StreamSupport.stream(clientList.spliterator(), false).collect(Collectors.toSet());
    }

    /**
     * Delete a client from the client repository
     * @param id integer representing the id of a client
     */
    public Optional<Client> deleteClient(Integer id) throws BookstoreException {
        return this.clientRepository.delete(id);
    }

    /**
     * Update a client from the client repository
     * @param client instance of the class Client
     * @throws ValidatorException if the client is not valid
     */
    public Optional<Client> updateClient(Client client) throws ValidatorException, BookstoreException {
        clientValidator.validate(client);
        return this.clientRepository.update(client);
    }

    /**
     * Returns all the clients that contain the searchString in one or more of their attributes.
     * @param searchString string used for filtering the clients
     * @return a HashSet containing clients
     */
    public Set<Client> filter(String searchString) throws BookstoreException {
        Set<Client> clients = this.getClientList();
        Set<Client> filteredClients = new HashSet<Client>();
        clients.forEach(client ->
        {
            if(client.getAddress().contains(searchString) || client.getLastName().contains(searchString) || client.getFirstName().contains(searchString))
            {
                filteredClients.add(client);
            }
        });
        return filteredClients;
    }

    public Optional<Client> findOne(Integer id) throws BookstoreException {
        return this.clientRepository.findOne(id);
    }
}
