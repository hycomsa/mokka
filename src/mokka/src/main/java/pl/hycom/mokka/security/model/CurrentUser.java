package pl.hycom.mokka.security.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof CurrentUser)) {
			return false;
		}

		CurrentUser that = (CurrentUser) o;

		return new EqualsBuilder().appendSuper(super.equals(o))
				.append(user, that.user)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
				.append(user)
				.toHashCode();
	}
}
