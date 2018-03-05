package edu.netcracker.project.logistic.config.security;

import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private PersonService personService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDetailsServiceImpl(PersonService personService,
                                  RoleService roleService,
                                  JdbcTemplate jdbcTemplate) {
        this.personService = personService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Person> personOptional = personService.findOne(username);

        Person person;
        if (personOptional.isPresent()){
            person = personOptional.get();
        } else {
            logger.error("There is no person with a such username!");
            throw new UsernameNotFoundException(username);
        }

        logger.info("Load person by username in UserDetailsServiceImpl. Username = " + person.getUserName());

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : roleService.findRolesByPersonId(person.getId())){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
            logger.info("Roles: " + role.getRoleName());
        }

        return new User(person.getUserName(), person.getPassword(), grantedAuthorities);
    }
}
