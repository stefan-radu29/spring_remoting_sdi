package repository;

import domain.BaseEntity;
import domain.validators.BookstoreException;
import domain.validators.Validator;
import domain.validators.ValidatorException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type InMemoryRepository.
 *
 * @param <ID> the type parameter
 * @param <T>  the type parameter
 */
public class InMemoryRepository<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    protected Map<ID, T> entities;

    /**
     * Instantiates a new InMemoryRepository.
     */
    public InMemoryRepository(){
        entities = new HashMap<>();
    }

    /**
     * Find the entity with the given {@code id}.
     *
     * @param id
     *            must be not null.
     * @return an {@code Optional} encapsulating the entity with the given id.
     * @throws IllegalArgumentException
     *             if the given id is null.
     */
    @Override
    public Optional<T> findOne(ID id){
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return Optional.ofNullable(entities.get(id));
    }

    /**
     *
     * @return all entities.
     */
    @Override
    public Iterable<T> findAll() {
        return entities.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());
    }

    /**
     * Saves the given entity.
     *
     * @param entity
     *            must not be null.
     * @return an {@code Optional} - null if the entity was saved otherwise (e.g. id already exists) returns the entity.
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidatorException
     *             if the entity is not valid.
     */
    @Override
    public Optional<T> save(T entity) throws BookstoreException {
        if (entity == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }


    /**
     * Removes the entity with the given id.
     *
     * @param id
     *            must not be null.
     * @return an {@code Optional} - null if there is no entity with the given id, otherwise the removed entity.
     * @throws IllegalArgumentException
     *             if the given id is null.
     */
    @Override
    public Optional<T> delete(ID id) throws BookstoreException {
        if (id == null)
        {
            throw new IllegalArgumentException("id must not be null");
        }
        return Optional.ofNullable(entities.remove(id));
    }

    /**
     * Updates the given entity.
     *
     * @param entity
     *            must not be null.
     * @return an {@code Optional} - null if the entity was updated otherwise (e.g. id does not exist) returns the
     *         entity.
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidatorException
     *             if the entity is not valid.
     */
    @Override
    public Optional<T> update(T entity) throws BookstoreException {
        if (entity == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return Optional.ofNullable(entities.computeIfPresent(entity.getId(), (k, v) -> entity));
    }
}
