package repository;

import domain.Book;
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
 * The type BookDatabaseRepository
 *
 */
public class BookDatabaseRepository implements BookRepository
{

    @Autowired
    private JdbcOperations jdbcOperations;

    /**
     * Find the book with the given id
     * @param id
     *            must be not null.
     * @return an Optional encapsulating the entity with the given id
     * @throws BookstoreException
     */
    @Override
    public Optional<Book> findOne(Integer id) throws BookstoreException {
        String getBookSQLStatement = "select * from book where id=?";

        try
        {
            List<Book> resultList = jdbcOperations.query(getBookSQLStatement,new Object[]{id}, (row, rowNum) ->
            {
                int bookId = row.getInt("id");
                String title = row.getString("title");
                String author = row.getString("author");
                String publisher = row.getString("publisher");
                int publicationYear = row.getInt("publicationyear");
                float price = (float)row.getDouble("price");
                Book newBook = new Book(title, author, publisher, publicationYear, price);
                newBook.setId(bookId);
                return newBook;
            });

            if (!resultList.isEmpty()) {
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
     * return all books
     * @return a list containing all the books
     * @throws BookstoreException
     */
    @Override
    public Iterable<Book> findAll() throws BookstoreException {
        String sqlStatement = "select * from book";

        try
        {
            return jdbcOperations.query(sqlStatement, (row, rowNum) ->
            {
                int bookId = row.getInt("id");
                String title = row.getString("title");
                String author = row.getString("author");
                String publisher = row.getString("publisher");
                int publicationYear = row.getInt("publicationyear");
                float price = (float)row.getDouble("price");
                Book newBook = new Book(title, author, publisher, publicationYear, price);
                newBook.setId(bookId);
                return newBook;
            });
        }
        catch(DataAccessException databaseException){
            throw new BookstoreException(databaseException.toString());
        }
    }

    /**
     * Saves the given book
     * @param entity
     *            must not be null.
     * @return an Optional - null if the entity was saved, otherwise returns the optional
     * @throws BookstoreException
     */
    @Override
    public Optional<Book> save(Book entity) throws BookstoreException {
        String sqlStatement = "insert into book (id, title, author, publisher, publicationyear, price) values (?, ?, ?, ?, ?, ?)";

        try
        {
            int rowsAffected = jdbcOperations.update(sqlStatement, entity.getId(), entity.getTitle(), entity.getAuthor(),
                    entity.getPublisher(), entity.getPublicationYear(), entity.getPrice());
            if (rowsAffected != 0)
                return Optional.empty();
            else
                return Optional.of(entity);
        }
        catch(DataAccessException databaseException)
        {
            throw new BookstoreException(databaseException.toString());
        }
    }

    /**
     * Removes the book with the given id
     * @param id
     *            must not be null.
     * @return an Optional - null if there is no book with the given id, otherwise returns the removed book
     * @throws BookstoreException
     */
    @Override
    public Optional<Book> delete(Integer id) throws BookstoreException {
        String deleteBookSQLStatement = "delete from book where id=?";

        try
        {
            Optional<Book> bookToDelete = this.findOne(id);

            if (bookToDelete.isPresent())
            {
                jdbcOperations.update(deleteBookSQLStatement, id);
                return bookToDelete;
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
     * Updates the given book
     *
     * @param entity
     *            must not be null.
     * @return an Optional - null if the book was updated, otherwise returns the book
     * @throws BookstoreException
     */
    @Override
    public Optional<Book> update(Book entity) throws BookstoreException {
        String sqlStatement = "update book set title=?, author=?, publisher=?, publicationyear=?, price=? where id=?";

        try {
            int rowsAffected = jdbcOperations.update(sqlStatement, entity.getTitle(), entity.getAuthor(),
                    entity.getPublisher(), entity.getPublicationYear(), entity.getPrice(), entity.getId());
            if (rowsAffected != 0)
                return Optional.of(entity);
            else
                return Optional.empty();
        }
        catch(DataAccessException databaseException)
        {
            throw new BookstoreException(databaseException.toString());
        }
    }
}

