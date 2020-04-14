package service;

import domain.Book;
import domain.validators.BookstoreException;
import domain.validators.ValidatorException;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface BookController {
    public Optional<Book> addBook(Book book) throws ValidatorException, BookstoreException, SQLException;

    /**
     * Get book list set.
     * @return the set containing all the books inside the book repository
     */
    public Set<Book> getBookList() throws BookstoreException;

    /**
     * Updates a book from the book repository
     * @param book instance of the class Book
     * @throws ValidatorException if the book is not valid
     */
    public Optional<Book> updateBook(Book book) throws ValidatorException, BookstoreException;

    /**
     * Deletes a book from the client repository
     * @param id integer representing the id of the book to be deleted
     */
    public Optional<Book> deleteBook(Integer id) throws BookstoreException;

    /**
     * Returns all the books that contain the searchString in either the title, the author name or the publisher
     * @param searchString string used for filtering the books
     * @return a HashSet containing books
     */
    public Set<Book> filter(String searchString) throws BookstoreException ;

    public Optional<Book> findOne(Integer id) throws BookstoreException;

}
