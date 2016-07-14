package pl.hycom.mokka.security.model;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public class CurrentUser extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = -5466743555688002724L;

	private User user;

	public CurrentUser(User user, Set<GrantedAuthority> authorities) {
		super(user.getUserName(), user.getPasswordHash(), !Boolean.TRUE.equals(user.getDisabled()), true, true, true, authorities);
		this.user = user;
	}

	public String getFirstName() {
		return user.getFirstName();
	}

	public String getLastName() {
		return user.getLastName();
	}

	public Long getId() {
		return user.getId();
	}
}
