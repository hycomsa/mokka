package pl.hycom.mokka.security;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.hycom.mokka.security.model.CurrentUser;
import pl.hycom.mokka.security.model.Role;
import pl.hycom.mokka.security.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
@RestController
public class UserController {

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	@Autowired
	private UserManager userManager;

	@GetMapping(value = "/user/current")
	public CurrentUser getCurrentUser() {
		if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext()
				.getAuthentication() == null || !(SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal() instanceof CurrentUser)) {
			return null;
		}
		return (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER_ADMIN')")
	@GetMapping(value = "/user")
	public List<User> getAllUsers() {
		return userManager.getAllUsers();
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@GetMapping(value = "/user/{id}")
	public Object getUser(@PathVariable("id") String id, HttpServletRequest request) {
		if (StringUtils.equalsIgnoreCase("current", id)) {
			if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
				return null;
			}
			return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}

		if (StringUtils.isNumeric(id) && (request.isUserInRole(ROLE_ADMIN) || request.isUserInRole("ROLE_USER_ADMIN"))) {
			return userManager.getUser(Long.parseLong(id));
		}

		return null;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER_ADMIN')")
	@PutMapping(value = "/user")
	public Object saveUser(@RequestBody User user, HttpServletRequest request) {
		removeAdminRole(user, request);

		// Not admin can't change other admin user name and role
		if (user != null && user.getId() != null) {
			User user2 = userManager.getUser(user.getId());
			if (user2 != null && user2.hasAnyRole(Role.ADMIN) && !request.isUserInRole(ROLE_ADMIN)) {
				user.setUserName(user2.getUserName());
				if (!user.hasAnyRole(Role.ADMIN)) {
					user.getRoles().add(Role.ADMIN);
				}
			}
		}

		Map<String, String> errors = userManager.validate(user);
		if (!errors.isEmpty()) {
			return ImmutableMap.of("errors", errors);
		}

		return userManager.saveOrUpdateUser(user);
	}

	private void removeAdminRole(@RequestBody User user, HttpServletRequest request) {
		// Not admin can't add admin role
		if (user != null && user.getId() == null && user.hasAnyRole(Role.ADMIN) && !request.isUserInRole(ROLE_ADMIN)) {
			Iterator<Role> it = user.getRoles().iterator();
			while (it.hasNext()) {
				if (it.next() == Role.ADMIN) {
					it.remove();
					break;
				}
			}
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/user/{id}")
	public boolean removeUser(@PathVariable("id") long id, HttpServletRequest request) {

		User user = userManager.getUser(id);
		if (user != null && user.hasAnyRole(Role.ADMIN) && !request.isUserInRole(ROLE_ADMIN)) {
			return false;
		}

		if (user != null && user.hasAnyRole(Role.ADMIN) && userManager.numberOfAdmins() == 1) {
			return false;
		}

		return userManager.removeUser(id);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER_ADMIN')")
	@PostMapping(value = {"/user/{id}/enable", "/user/{id}/disable" })
	public boolean setDisabled(@PathVariable("id") long id, HttpServletRequest request) {

		User user = userManager.getUser(id);
		if (user != null && user.hasAnyRole(Role.ADMIN) && !request.isUserInRole(ROLE_ADMIN)) {
			return user.getDisabled();
		}

		if (user != null && user.hasAnyRole(Role.ADMIN) && userManager.numberOfAdmins() == 1) {
			return false;
		}

		return userManager.setDisabled(id, StringUtils.endsWith(request.getRequestURL(), "disable"));
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER_ADMIN')")
	@PostMapping(value = "/user/{id}/reset-password")
	public boolean resetPassword(@PathVariable("id") long id) {
		userManager.resetPassword(id);
		return true;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER_ADMIN')")
	@PostMapping(value = "/user/{id}/role")
	public boolean switchRole(@PathVariable("id") long id, @RequestBody Map<String, String> params, HttpServletRequest request) {

		User user = userManager.getUser(id);
		if (user == null) {
			return false;
		}

		Role role = Role.fromString(params.get("role"));
		if (checkRole(request, user, role)) {
			changeRoles(user, role);
			userManager.saveOrUpdateUser(user);
			return true;
		}
		return false;
	}

	private boolean checkRole(HttpServletRequest request, User user, Role role) {
		if (role == Role.USER) {
			return false;
		}

		if (role == Role.ADMIN && !request.isUserInRole(ROLE_ADMIN)) {
			return false;
		}

        return user == null || !user.hasAnyRole(Role.ADMIN) || userManager.numberOfAdmins() != 1;
    }

	private void changeRoles(User user, Role role) {
		if (user.hasAnyRole(role)) {
			Iterator<Role> it = user.getRoles().iterator();
			while (it.hasNext()) {
				if (it.next() == role) {
					it.remove();
					break;
				}
			}

		} else {
			user.getRoles().add(role);
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@PostMapping(value = "/user/{id}/change-password")
	public boolean changePassword(@PathVariable("id") long id, @RequestBody Map<String, String> params) {

		User user = userManager.getUser(id);
		if (user == null) {
			return false;
		}

		if (SecurityContextHolder.getContext().getAuthentication() == null || !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CurrentUser)) {
			return false;
		}

		CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (!StringUtils.equals(currentUser.getUsername(), user.getUserName())) {
			return false;
		}

		return userManager.changePassword(id, params.get("password"));
	}
}
