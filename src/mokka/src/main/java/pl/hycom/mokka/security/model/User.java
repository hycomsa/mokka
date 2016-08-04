package pl.hycom.mokka.security.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity
@Table(name = "users")
@Data
public class User implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;

	@Column(nullable = false, unique = true)
	private String userName;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	@ElementCollection(targetClass = Role.class)
	@Enumerated(EnumType.STRING)
	private Set<Role> roles;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column
	private Boolean disabled = Boolean.FALSE;

	@Column
	private Boolean resetPassword;

	public boolean hasAnyRole(Role... rolesToCheck) {
		if (roles == null || rolesToCheck == null) {
			return false;
		}

		for (Role roleToCheck : rolesToCheck) {
			if (roleToCheck == Role.USER) {
				return true;
			}

			for (Role r : roles) {
				if (r == roleToCheck) {
					return true;
				}
			}
		}

		return false;
	}
}
