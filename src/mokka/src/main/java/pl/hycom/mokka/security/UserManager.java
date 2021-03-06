package pl.hycom.mokka.security;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequiredArgsConstructor
public class UserManager {

	private static final String NOT_VALID = "not-valid";

	@Value("${default.password}")
    @Setter
	private String defaultPassword;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

    public User createAdminUser() {
        log.info("Creating admin user.");
        User user = new User();
        user.setUserName("admin");
        user.setPasswordHash(passwordEncoder.encode(getDefaultPassword()));
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setDisabled(Boolean.FALSE);
        user.setRoles(ImmutableSet.of(Role.ADMIN));

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return Lists.newArrayList(userRepository.findAll());
    }

    public UserDetails loadUserByUsername(String userName) {
        User user = userRepository.findOneByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("User[" + userName + "] was not found"));

        Set<GrantedAuthority> authorities = new HashSet<>(user.getRoles().size());
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        return new CurrentUser(user, authorities);
    }

	public User getUser(long id) {
		return userRepository.findById(id).orElse(null);
	}

	public boolean setDisabled(Long id, boolean disable) {
		if (id == null) {
			return false;
		}

		Optional<User> user = userRepository.findById(id);
		user.ifPresent(u -> u.setDisabled(disable));
		user.ifPresent(userRepository::save);

		return user.isPresent();
	}

	public boolean removeUser(long id) {
		try {
			userRepository.deleteById(id);
			log.info("User (id: {}) deleted", id);
			return true;
		} catch (Exception e) {
			log.error("User (id: {}}) could not be deleted", id, e);
		}

		return userRepository.findById(id).isPresent();
	}

	public User saveOrUpdateUser(User user) {
		if (StringUtils.isBlank(user.getPasswordHash())) {
			user.setPasswordHash(passwordEncoder.encode(getDefaultPassword()));
			user.setResetPassword(Boolean.TRUE);
		}

		User u = userRepository.save(user);
		log.info("User [username:{}, id:{}] added or updated", u.getUserName(), u.getId());
		return u;
	}

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

	public int numberOfAdmins() {
		return userRepository.findByRoles(Role.ADMIN).size();
	}

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

    public String getDefaultEncodedPassword() {
        return passwordEncoder.encode(defaultPassword);
    }
}
