package repository;

import domain.Book;
import domain.Client;
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

public class ClientFileRepository extends InMemoryRepository<Integer, Client>
{
    protected String fileName;

    /**
     * Instantiates a new InMemoryRepository.
     *
     * @param validator instance of a class implementing the Validator interface
     * @param fileName string representing the path to the CSV file where clients are stored
     */
    public ClientFileRepository(Validator<Client> validator, String fileName) throws BookstoreException {
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
                    String firstName = items.get(1);
                    String lastName = items.get(2);
                    String address = items.get(3);

                    Client client = new Client(firstName, lastName, address);
                    client.setId(id);

                    try {
                        super.save(client);
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
                            value.getId() + "," + value.getFirstName() + "," + value.getLastName() + "," + value.getAddress()+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Saves a client to the file repository.
     * @param client instance of class Client
     * @return instance of Optional that could contain a client or null
     */
    @Override
    public Optional<Client> save(Client client) throws BookstoreException {
        Optional<Client> optionalClient = super.save(client);
        if (optionalClient.isPresent())
            return optionalClient;
        saveData();
        return Optional.empty();
    }

    /**
     * Deletes a client from the file repository.
     * @param id integer representing the id of a client
     * @return instance of Optional that could contain a client or null
     */
    @Override
    public Optional<Client> delete(Integer id) throws BookstoreException {
        Optional<Client> optionalClient = super.delete(id);
        if (!optionalClient.isPresent())
            return optionalClient;
        saveData();
        return optionalClient;
    }

    /**
     * Updates a client from the file repository.
     * @param client instance of class Client
     * @return instance of Optional that could contain a client or null
     */
    @Override
    public Optional<Client> update(Client client) throws BookstoreException {
        Optional<Client> optionalClient = super.update(client);
        if (!optionalClient.isPresent())
            return optionalClient;
        saveData();
        return optionalClient;
    }
}

