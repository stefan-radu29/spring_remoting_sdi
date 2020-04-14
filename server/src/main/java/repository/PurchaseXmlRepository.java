package repository;

import domain.validators.BookstoreException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
import domain.Client;
import domain.validators.Validator;

import domain.Purchase;

public class PurchaseXmlRepository extends PurchaseFileRepository
{
    public PurchaseXmlRepository(Validator<Purchase> validator, String fileName) throws BookstoreException {
        super(validator, fileName);
    }

    /**
     * Gets the text from inside a tag name.
     * @param parentElement instance of class Element
     * @param tagName string
     * @return the text contained in a tag name
     */
    private static String getTextFromTagName(Element parentElement, String tagName)
    {
        Node node = parentElement.getElementsByTagName(tagName).item(0);
        return node.getTextContent();
    }

    /**
     * Turns an instance of Element into an instance of Purchase.
     * @param purchaseElement instance of class Element
     * @return an instance of class Purchase created using the information contained by the element
     */
    private static Purchase createPurchaseFromElement(Element purchaseElement)
    {
        int id = Integer.parseInt(getTextFromTagName(purchaseElement, "id"));
        int clientId = Integer.parseInt(getTextFromTagName(purchaseElement, "clientId"));
        int bookId = Integer.parseInt(getTextFromTagName(purchaseElement, "bookId"));
        String library = getTextFromTagName(purchaseElement, "library");

        Purchase newPurchase = new Purchase(clientId, bookId, library);
        newPurchase.setId(id);

        return newPurchase;
    }

    /**
     * @throws BookstoreException if there is any XML file related exception
     */
    @Override
    protected void loadData() throws BookstoreException{
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
                    .map(node -> createPurchaseFromElement((Element) node))
                    .forEach(purchase -> {
                        try {
                            super.save(purchase);
                        } catch (BookstoreException e) {
                            e.printStackTrace();
                        }
                    });
        }
        catch(ParserConfigurationException | IOException | SAXException exception)
        {
            throw new BookstoreException(exception.toString());
        }

    }

    /**
     * Appends a child node containing text to another node.
     * @param document instance of class Document
     * @param parentNode instance of class Node
     * @param tagName string
     * @param textContent string
     */
    private static void appendChildWithTextToNode(Document document, Node parentNode, String tagName, String textContent) {
        Element element = document.createElement(tagName);
        element.setTextContent(textContent);
        parentNode.appendChild(element);
    }

    /**
     * Turns an instance of Purchase into an instance of Node
     * @param purchase instance of class Purchase
     * @param document instance of class Document
     * @return instance of Node class representing a purchase
     */
    private static Node purchaseToNode(Purchase purchase, Document document)
    {
        Element purchaseElement = document.createElement("purchase");

        appendChildWithTextToNode(document, purchaseElement, "id", purchase.getId().toString());
        appendChildWithTextToNode(document, purchaseElement, "clientId", String.valueOf(purchase.getClientId()));
        appendChildWithTextToNode(document, purchaseElement, "bookId", String.valueOf(purchase.getBookId()));
        appendChildWithTextToNode(document, purchaseElement, "library", purchase.getLibrary());

        return purchaseElement;
    }

    /**
     * Saves all the entities in a XML file
     * @throws BookstoreException if there is any XML related exception
     */
    @Override
    protected void saveData() throws BookstoreException {
        try
        {
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            Element root = document.createElement("purchases");
            document.appendChild(root);
            this.entities.forEach((id, purchase) ->
            {
                Node purchaseNode = purchaseToNode(purchase, document);
                root.appendChild(purchaseNode);
            });

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            transformer.transform(new DOMSource(document),
                    new StreamResult(new File(this.fileName)));
        }
        catch(TransformerException | ParserConfigurationException exception)
        {
            throw new BookstoreException(exception.toString());
        }
    }
}
