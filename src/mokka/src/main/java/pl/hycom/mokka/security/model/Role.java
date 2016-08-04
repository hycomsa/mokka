package pl.hycom.mokka.security.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public enum Role {
	USER, ADMIN, USER_ADMIN, EDITOR;

	public static Role fromString(String s) {
		for (Role r : Role.values()) {
			if (StringUtils.equalsIgnoreCase(s, r.name())) {
				return r;
			}
		}

		return USER;
	}
}
