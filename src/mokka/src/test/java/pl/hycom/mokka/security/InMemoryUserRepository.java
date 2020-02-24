package pl.hycom.mokka.security;

import org.apache.commons.lang3.RandomUtils;
import pl.hycom.mokka.security.UserRepository;
import pl.hycom.mokka.security.model.Role;
import pl.hycom.mokka.security.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
public class InMemoryUserRepository implements UserRepository {

    private Map<String, User> users;

    public InMemoryUserRepository(){
        users = new HashMap<>();
    }

    @Override
    public Optional<User> findOneByUserName(String userName) {
        return Optional.ofNullable(users.get(userName));
    }

    @Override
    public List<User> findByRoles(Role role) {
        return null;
    }

    @Override
    public <S extends User> S save(S entity) {
        entity.setId(RandomUtils.nextLong());
        users.put(entity.getUserName(), entity);

        return (S) users.get(entity.getUserName());
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<User> findAll() {
        return users.values();
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(User entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
