package pl.hycom.mokka.security;

import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Service;
import pl.hycom.mokka.security.model.CurrentUser;
import pl.hycom.mokka.security.model.Role;
import pl.hycom.mokka.security.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Mapper gets information about user from database using LDAP data and saves them to UserDetails. If user
 * does not exists in database yet, new entry in database is created using LDAP data.
 *
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsContextMapper implements UserDetailsContextMapper {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseUserDetailsContextMapper.class);

    private final UserManager userManager;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String userName, Collection<? extends GrantedAuthority> collection) {
        try {
            return userManager.loadUserByUsername(userName);
        } catch (UsernameNotFoundException e) {
            LOG.info("User with [{}] not found in local database. Creating new entry.", userName);
            User user = userManager.saveOrUpdateUser(mapDirContextOperationsToUser(userName, dirContextOperations));
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new CurrentUser(user, authorities);
        }
    }

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
        throw new UnsupportedOperationException();
    }

    private User mapDirContextOperationsToUser(String username, DirContextOperations dirContextOperations) {
        User user = new User();
        user.setUserName(username);
        user.setFirstName(dirContextOperations.getStringAttribute("givenname"));
        user.setPasswordHash(userManager.getDefaultEncodedPassword());
        user.setLastName(dirContextOperations.getStringAttribute("sn"));
        user.setDisabled(Boolean.FALSE);
        user.setRoles(ImmutableSet.of(Role.USER));
        return user;
    }
}
