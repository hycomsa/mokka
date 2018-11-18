package pl.hycom.mokka.security;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.hycom.mokka.security.model.CurrentUser;
import pl.hycom.mokka.security.model.Role;
import pl.hycom.mokka.security.model.User;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
@Service
public class UserManager {

	public static final String NOT_VALID = "not-valid";
	@Value("${default.password}")
	private String defaultPassword;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public List<User> getAllUsers() {
		return Lists.newArrayList(userRepository.findAll());
	}

	@Transactional
	public UserDetails loadUserByUsername(String userName){

		if (!userRepository.findAll().iterator().hasNext()) {
			User user = new User();
			user.setUserName("admin");
			user.setPasswordHash(passwordEncoder.encode(getDefaultPassword()));
			user.setFirstName("Admin");
			user.setLastName("Admin");
			user.setDisabled(Boolean.FALSE);
			user.setRoles(ImmutableSet.of(Role.ADMIN));
			userRepository.save(user);
		}

		User user = userRepository.findOneByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("User[" + userName + "] was not found"));

		Set<GrantedAuthority> authorities = new HashSet<>(user.getRoles().size());
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		for (Role role : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
		}

		return new CurrentUser(user, authorities);
	}

	@Transactional
	public User getUser(long id) {
		return userRepository.findById(id).orElse(null);
	}

	@Transactional
	public boolean setDisabled(Long id, boolean disable) {
		if (id == null) {
			return false;
		}

		Optional<User> user = userRepository.findById(id);
		user.ifPresent(u -> u.setDisabled(disable));
		user.ifPresent(u -> userRepository.save(u));

		return user.isPresent();
	}

	@Transactional
	public boolean removeUser(long id) {
		try {
			userRepository.deleteById(id);
			log.info("User (id: " + id + ") deleted");
			return true;
		} catch (Exception e) {
			log.error("Userk (id: " + id + ") could not be deleted", e);
		}

		return userRepository.findById(id).isPresent();
	}

	@Transactional
	public User saveOrUpdateUser(User user) {
		if (StringUtils.isBlank(user.getPasswordHash())) {
			user.setPasswordHash(passwordEncoder.encode(getDefaultPassword()));
			user.setResetPassword(Boolean.TRUE);
		}

		User u = userRepository.save(user);
		log.info("Mock (id: " + u.getId() + ") added or updated");
		return u;
	}

	@Transactional
	public Map<String, String> validate(User user) {
		Map<String, String> out = new HashMap<>();

		if (StringUtils.isBlank(user.getFirstName())) {
			out.put("firstName", NOT_VALID);
		}

		if (StringUtils.isBlank(user.getLastName())) {
			out.put("lastName", NOT_VALID);
		}

		if (StringUtils.isBlank(user.getUserName())) {
			out.put("userName", NOT_VALID);
		}

		if (user.getId() == null && userRepository.findOneByUserName(user.getUserName()).isPresent()) {
			out.put("userName", NOT_VALID);
		}

		return out;
	}

	@Transactional
	public int numberOfAdmins() {
		return userRepository.findByRoles(Role.ADMIN).size();
	}

	@Transactional
	public void resetPassword(long id) {
		User user = getUser(id);
		if (user == null) {
			return;
		}

		user.setPasswordHash(passwordEncoder.encode(getDefaultPassword()));
		user.setResetPassword(Boolean.TRUE);

		userRepository.save(user);
	}

	public boolean changePassword(long id, String password) {
		if (StringUtils.isBlank(password)) {
			return false;
		}

		User user = getUser(id);
		if (user == null) {
			return false;
		}

		user.setResetPassword(Boolean.FALSE);
		user.setPasswordHash(passwordEncoder.encode(password));
		userRepository.save(user);

		return true;
	}

	public String getDefaultPassword() {
		return defaultPassword;
	}
}
