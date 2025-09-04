package pl.hycom.mokka.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

	@Autowired
    @Lazy
	private UserManager userManager;

	@Override
	public UserDetails loadUserByUsername(String userName) {
		return userManager.loadUserByUsername(userName);
	}

}
