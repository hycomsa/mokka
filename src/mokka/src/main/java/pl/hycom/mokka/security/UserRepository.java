package pl.hycom.mokka.security;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import pl.hycom.mokka.security.model.Role;
import pl.hycom.mokka.security.model.User;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findOneByUserName(String userName);

	List<User> findByRoles(Role role);
}
