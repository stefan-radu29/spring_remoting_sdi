package controller;

import domain.Client;
import domain.validators.BookstoreException;
import domain.validators.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import service.ClientController;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class ClientControllerClientImpl implements ClientController {
    @Autowired
    private ClientController clientController;

    public Optional<Client> addClient(Client client) throws BookstoreException, ValidatorException, SQLException {
        return clientController.addClient(client);
    }

    public Optional<Client> updateClient(Client client) throws ValidatorException, BookstoreException {
        return clientController.updateClient(client);
    }

    public Optional<Client> deleteClient(Integer id) throws BookstoreException {
        return clientController.deleteClient(id);
    }

    public Set<Client> getClientList() throws BookstoreException {
        return clientController.getClientList();
    }

    public Set<Client> filter(String searchString) throws BookstoreException {
        return clientController.filter(searchString);
    }

    @Override
    public Optional<Client> findOne(Integer id) throws BookstoreException {
        return clientController.findOne(id);
    }
}
