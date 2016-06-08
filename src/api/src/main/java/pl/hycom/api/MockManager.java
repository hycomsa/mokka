package pl.hycom.api;

import pl.hycom.api.model.Mapping;

import java.util.List;

/**
 * Interface for managing mock
 *
 * @author Michal Adamczyk, HYCOM S.A.
 */
public interface MockManager {
    /**
     * creates mapping
     *
     * @param mapping mapping to create
     */
    void create(Mapping mapping);

    /**
     * removes mapping
     *
     * @param mapping mapping to remove
     */
    void remove(Mapping mapping);

    /**
     * updates mapping
     *
     * @param mapping mapping to remove
     */
    void update(Mapping mapping);

    /**
     * gets all mappings
     *
     * @return list of all mappings
     */
    List<Mapping> getAllMappings();
}
