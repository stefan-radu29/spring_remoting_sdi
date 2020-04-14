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

public class ClientXmlRepository extends ClientFileRepository {

    /**
     * Instantiates a new InMemoryRepository.
     *
     * @param validator instance of a class implementing the Validator interface
     * @param fileName  string representing the path to the CSV file where clients are stored
     */
    public ClientXmlRepository(Validator<Client> validator, String fileName) throws BookstoreException {
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
     * Turns an instance of Element into an instance of Client.
     * @param clientElement instance of class Element
     * @return an instance of class Client created using the information contained by the element
     */
    private static Client createClientFromElement(Element clientElement)
    {
        int id = Integer.parseInt(getTextFromTagName(clientElement, "id"));
        String firstName = getTextFromTagName(clientElement, "firstName");
        String lastName = getTextFromTagName(clientElement, "lastName");
        String address = getTextFromTagName(clientElement, "address");

        Client newClient = new Client(firstName, lastName, address);
        newClient.setId(id);

        return newClient;
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
                    .map(node -> createClientFromElement((Element) node))
                    .forEach(client -> {
                        try {
                            super.save(client);
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
     * Turns an instance of Client into an instance of Node
     * @param client instance of class Client
     * @param document instance of class Document
     * @return instance of Node class representing a client
     */
    private static Node clientToNode(Client client, Document document)
    {
        Element clientElement = document.createElement("client");

        appendChildWithTextToNode(document, clientElement, "id", client.getId().toString());
        appendChildWithTextToNode(document, clientElement, "firstName", client.getFirstName());
        appendChildWithTextToNode(document, clientElement, "lastName", client.getLastName());
        appendChildWithTextToNode(document, clientElement, "address", client.getAddress());

        return clientElement;
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

            Element root = document.createElement("clients");
            document.appendChild(root);
            this.entities.forEach((id, client) ->
            {
                Node clientNode = clientToNode(client, document);
                root.appendChild(clientNode);
            });

            //File oldFile = new File(fileName);
            //boolean deletedOldFile = oldFile.delete();

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
