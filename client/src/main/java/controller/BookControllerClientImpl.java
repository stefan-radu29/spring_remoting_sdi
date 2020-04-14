package controller;

import domain.Book;
import domain.validators.BookstoreException;
import domain.validators.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import service.BookController;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class BookControllerClientImpl implements BookController {
    @Autowired
    private BookController bookController;

    public Optional<Book> addBook(Book book) throws BookstoreException, ValidatorException, SQLException {
        return bookController.addBook(book);
    }

    public Optional<Book> updateBook(Book book) throws ValidatorException, BookstoreException {
        return bookController.updateBook(book);
    }

    public Set<Book> getBookList() throws BookstoreException {
        return bookController.getBookList();
    }

    public Optional<Book> deleteBook(Integer id) throws BookstoreException {
        return bookController.deleteBook(id);
    }

    public Set<Book> filter(String searchString) throws BookstoreException {
        return bookController.filter(searchString);
    }

    @Override
    public Optional<Book> findOne(Integer id) throws BookstoreException {
        return bookController.findOne(id);
    }
}
