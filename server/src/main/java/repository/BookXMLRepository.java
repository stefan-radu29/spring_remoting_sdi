package repository;

import domain.Book;
import domain.validators.BookstoreException;
import domain.validators.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class BookXMLRepository extends BookFileRepository {
    /**
     * Instantiates a new InMemoryRepository
     *
     * @param bookValidator instance of a class implementing the Validator interface
     * @param fileName      string representing the path to the CSV file where books are stored
     */
    public BookXMLRepository(Validator<Book> bookValidator, String fileName) throws BookstoreException {
        super(bookValidator, fileName);
    }

    /**
     * Gets the text from inside a tag name.
     *
     * @param parentElement instance of class Element
     * @param tagName       string
     * @return the text contained in a tag name
     */
    private static String getTextFromTagName(Element parentElement, String tagName) {
        Node node = parentElement.getElementsByTagName(tagName).item(0);
        return node.getTextContent();
    }

    /**
     * Turns an instance of Element into an instance of Book.
     *
     * @param bookElement instance of class Element
     * @return an instance of class Book created using the information contained by the element
     */
    private static Book createBookFromElement(Element bookElement) {
        int id = Integer.parseInt(getTextFromTagName(bookElement, "id"));
        String title = getTextFromTagName(bookElement, "title");
        String author = getTextFromTagName(bookElement, "author");
        String publisher = getTextFromTagName(bookElement, "publisher");
        int publicationYear = Integer.parseInt(getTextFromTagName(bookElement, "publicationYear"));
        float price = Float.parseFloat(getTextFromTagName(bookElement, "price"));

        Book newBook = new Book(title, author, publisher, publicationYear, price);
        newBook.setId(id);

        return newBook;
    }

    /**
     * @throws BookstoreException if there is any XML file related exception
     */
    @Override
    protected void loadData() throws BookstoreException {
        try {
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(this.fileName);

            Element root = document.getDocumentElement();

            NodeList children = root.getChildNodes();

            IntStream.range(0, children.getLength())
                    .mapToObj(index -> children.item(index))
                    .filter(node -> node instanceof Element)
                    .map(node -> createBookFromElement((Element) node))
                    .forEach(book -> {
                        try {
                            super.save(book);
                        } catch (BookstoreException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (ParserConfigurationException | IOException | SAXException exception) {
            throw new BookstoreException(exception.toString());
        }
    }

    /**
     * Appends a child node containing text to another node.
     *
     * @param document    instance of class Document
     * @param parentNode  instance of class Node
     * @param tagName     string
     * @param textContent string
     */
    private static void appendChildWithTextToNode(Document document, Node parentNode, String tagName, String textContent) {
        Element element = document.createElement(tagName);
        element.setTextContent(textContent);
        parentNode.appendChild(element);
    }

    /**
     * Turns an instance of Book into an instance of Node
     *
     * @param book     instance of class Book
     * @param document instance of class Document
     * @return instance of Node class representing a book
     */
    private static Node bookToNode(Book book, Document document) {
        Element bookElement = document.createElement("book");

        appendChildWithTextToNode(document, bookElement, "id", book.getId().toString());
        appendChildWithTextToNode(document, bookElement, "title", book.getTitle());
        appendChildWithTextToNode(document, bookElement, "author", book.getAuthor());
        appendChildWithTextToNode(document, bookElement, "publisher", book.getPublisher());
        appendChildWithTextToNode(document, bookElement, "publicationYear", Integer.toString(book.getPublicationYear()));
        appendChildWithTextToNode(document, bookElement, "price", Float.toString(book.getPrice()));

        return bookElement;
    }

    /**
     * Saves all the entities in a XML file
     * @throws BookstoreException if there is any XML related exception
     */
    @Override
    protected void saveData() throws BookstoreException {
        try {
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            Element root = document.createElement("books");
            document.appendChild(root);
            this.entities.forEach((id, book) ->
            {
                Node bookNode = bookToNode(book, document);
                root.appendChild(bookNode);
            });

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            transformer.transform(new DOMSource(document),
                    new StreamResult(new File(this.fileName)));
        } catch (TransformerException | ParserConfigurationException exception) {
            throw new BookstoreException(exception.toString());
        }
    }
}