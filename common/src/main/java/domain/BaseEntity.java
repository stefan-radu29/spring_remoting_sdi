package domain;

import java.io.Serializable;

/**
 * The type BaseEntity.
 *
 * @param <T> the type parameter
 */
public class BaseEntity<T> implements Serializable
{
    private T id;

    /**
     * Gets id.
     * @return the id which is of type T
     */
    public T getId()
    {
        return this.id;
    }

    /**
     * Sets id.
     * @param newId the new id of type T
     */
    public void setId(T newId)
    {
        this.id = newId;
    }

    @Override
    public String toString()
    {
        return "BaseEntity{"+
                "id=" +
                this.id.toString() +
                "}";
    }
}
