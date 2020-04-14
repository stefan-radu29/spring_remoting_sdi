package domain;

public class Purchase extends BaseEntity<Integer>
{
    private int clientId;
    private int bookId;
    private String library;

    public Purchase(int clientId, int bookId, String library)
    {
        this.clientId = clientId;
        this.bookId = bookId;
        this.library = library;
    }

    @Override
    public String toString() {
        return "Purchase{" + super.toString() + " " +
                "clientId=" + clientId +
                ", bookId=" + bookId +
                ", library=" + library +
                '}';
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

}
