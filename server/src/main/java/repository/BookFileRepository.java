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

/**
 * The type BookFileRepository.
 *
 */
public class BookFileRepository extends InMemoryRepository<Integer, Book> {
    protected String fileName;

    /**
     * Instantiates a new BookFileRepository
     *
     * @param validator instance of a class implementing the Validator interface
     * @param fileName string representing the path to the CSV file where clients are stored
     */
    public BookFileRepository(Validator<Book> validator, String fileName) throws BookstoreException{
        this.fileName = fileName;

        this.loadData();
    }

    /**
     * Reads the data from a file into the local repository
     */
    protected void loadData() throws BookstoreException{
        Path path = Paths.get(fileName);

        try{
            Files.lines(path).forEach(line ->{
                List<String> items = Arrays.asList(line.split(","));

                Integer id = Integer.valueOf(items.get(0));
                String title = items.get(1);
                String author = items.get(2);
                String publisher = items.get(3);
                int publicationYear = Integer.parseInt(items.get(4));
                float price = Float.parseFloat(items.get(5));

                Book book = new Book(title, author, publisher, publicationYear, price);
                book.setId(id);

                try{
                    super.save(book);
                } catch (IllegalArgumentException | BookstoreException e){
                    e.printStackTrace();
                }
            });
        } catch (IOException e){
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
                            value.getId() + "," + value.getTitle() + "," + value.getAuthor() + "," + value.getPublisher() +  "," + value.getPublicationYear() + "," + value.getPrice());
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
     * Saves an {@code entity} into the file repository
     * @param book instance of class Book
     * @return instance of Optional that could contain a client or null
     */
    @Override
    public Optional<Book> save(Book book) throws BookstoreException {
        Optional<Book> optionalBook = super.save(book);
        if (optionalBook.isPresent())
            return optionalBook;
        saveData();
        return Optional.empty();
    }

    /**
     * Updates a book from the file repository.
     * @param book instance of class Book
     * @return instance of Optional that could contain a book or null
     */
    @Override
    public Optional<Book> update(Book book) throws BookstoreException {
        Optional<Book> optionalBook = super.update(book);
        if (!optionalBook.isPresent())
            return optionalBook;
        saveData();
        return optionalBook;
    }

    /**
     * Deletes a book from the file repository.
     * @param id integer representing the id of a book
     * @return instance of Optional that could contain a book or null
     */
    @Override
    public Optional<Book> delete(Integer id) throws BookstoreException {
        Optional<Book> optionalBook = super.delete(id);
        if (!optionalBook.isPresent())
            return optionalBook;
        saveData();
        return optionalBook;
    }
}
