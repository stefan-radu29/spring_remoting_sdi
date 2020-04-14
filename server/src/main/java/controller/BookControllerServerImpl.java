package controller;

import domain.Book;
import domain.validators.BookstoreException;
import domain.validators.Validator;
import domain.validators.ValidatorException;
import repository.Repository;
import service.BookController;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The type BookController.
 */
public class BookControllerServerImpl implements BookController {
    private Repository<Integer, Book> bookRepository;
    private Validator<Book> bookValidator;
    /**
     * Instantiates a new Controller.
     *
     * @param bookRepository   instance of class implementing the Repository interface representing the book repository
     */
    public BookControllerServerImpl(Repository<Integer, Book> bookRepository, Validator<Book> bookValidator){
        this.bookRepository = bookRepository;
        this.bookValidator = bookValidator;
    }

    /**
     * Add a book to the book repository.
     * @param book instance of class Book
     * @throws ValidatorException if the book is not valid
     */
    public Optional<Book> addBook(Book book) throws ValidatorException, BookstoreException, SQLException {
        bookValidator.validate(book);
        return this.bookRepository.save(book);
    }

    /**
     * Get book list set.
     * @return the set containing all the books inside the book repository
     */
    public Set<Book> getBookList() throws BookstoreException {
        Iterable<Book> bookList = this.bookRepository.findAll();
        return StreamSupport.stream(bookList.spliterator(), false).collect(Collectors.toSet());
    }

    /**
     * Updates a book from the book repository
     * @param book instance of the class Book
     * @throws ValidatorException if the book is not valid
     */
    public Optional<Book> updateBook(Book book) throws ValidatorException, BookstoreException {
        bookValidator.validate(book);
        return this.bookRepository.update(book);
    }

    /**
     * Deletes a book from the client repository
     * @param id integer representing the id of the book to be deleted
     */
    public Optional<Book> deleteBook(Integer id) throws BookstoreException {
        return this.bookRepository.delete(id);
    }

    /**
     * Returns all the books that contain the searchString in either the title, the author name or the publisher
     * @param searchString string used for filtering the books
     * @return a HashSet containing books
     */
    public Set<Book> filter(String searchString) throws BookstoreException {
        Set<Book> books = this.getBookList();
        Set<Book> filteredBooks = new HashSet<Book>();

        books.forEach(book->{
            if (book.getTitle().contains(searchString) ||
                    book.getAuthor().contains(searchString) ||
                    book.getPublisher().contains(searchString))
                filteredBooks.add(book);
        });

        return filteredBooks;
    }

    public Optional<Book> findOne(Integer id) throws BookstoreException {
        return this.bookRepository.findOne(id);
    }
}
