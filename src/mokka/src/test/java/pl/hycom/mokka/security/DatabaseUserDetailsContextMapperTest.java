package pl.hycom.mokka.security;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import pl.hycom.mokka.security.model.Role;
import pl.hycom.mokka.security.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
public class DatabaseUserDetailsContextMapperTest {

    private static final String USERNAME = "harry.potah";

    private DatabaseUserDetailsContextMapper databaseUserDetailsContextMapper;

    private UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        userRepository = new InMemoryUserRepository();
        UserManager userManager = new UserManager(userRepository, new BCryptPasswordEncoder());
        userManager.setDefaultPassword("test");
        databaseUserDetailsContextMapper = new DatabaseUserDetailsContextMapper(userManager);
    }

    @Test
    public void mapUserFromContextTest(){
        // given
        userRepository.save(prepareUser());

        // when
        UserDetails userDetails = databaseUserDetailsContextMapper.mapUserFromContext(new DirContextAdapter(), USERNAME, new ArrayList<>());

        // then
        assertNotNull(userDetails);
        assertEquals(USERNAME, userDetails.getUsername());
        assertEquals(1, userDetails.getAuthorities().size());
        assertNotNull(userRepository.findOneByUserName(USERNAME));
    }

    @Test
    public void mapUserFromContextTest_Exception(){
        // given
        assertFalse(userRepository.findOneByUserName(USERNAME).isPresent());
        DirContextAdapter dirContextAdapter = new DirContextAdapter();
        dirContextAdapter.addAttributeValue("givenname", "test");
        dirContextAdapter.addAttributeValue("sn", "test");

        // when
        UserDetails userDetails = databaseUserDetailsContextMapper.mapUserFromContext(dirContextAdapter, USERNAME , new ArrayList<>());

        // then
        assertNotNull(userDetails);
        assertNotNull(USERNAME, userDetails.getUsername());
        assertNotNull(userRepository.findOneByUserName(USERNAME));
    }


    public User prepareUser(){
        User user = new User();
        user.setUserName(USERNAME);
        user.setFirstName(USERNAME);
        user.setLastName(USERNAME);
        user.setPasswordHash("hashedPassword");
        user.setRoles(ImmutableSet.of(Role.USER));
        return user;
    }

    public Set<GrantedAuthority> prepareAuthorities(){
        Set<GrantedAuthority> set = new HashSet<>();
        LdapAuthority ldapAuthority = new LdapAuthority("USER", "user");
        set.add(ldapAuthority);
        return set;
    }
}
