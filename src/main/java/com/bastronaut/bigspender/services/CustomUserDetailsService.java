package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.config.SecurityUtil;
import com.bastronaut.bigspender.exceptions.UserRegistrationException;
import com.bastronaut.bigspender.exceptions.UserUpdateException;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_USER_EXISTS;
import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_USER_NOTFOUND;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException {

        final Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException(String.format(ERRORMSG_USER_NOTFOUND, username));
    }

    public User registerUser(final User user) throws UserRegistrationException {
        if (isValidRegistration(user)) {
            final String encodedPassword = SecurityUtil.encode(user.getPassword());
            return userRepository.save(new User(user.getEmail(), encodedPassword));
        }
        throw new UserRegistrationException(String.format(ERRORMSG_USER_EXISTS, user.getEmail()));
    }

    /**
     * Allows updating of name and password, not the username
     * @param userToUpdate the user to update
     * @param updateUserDetails a user object with fields populated according to changes
     * @return the new user if saved to database successfully
     */
    public User updateUser(final User userToUpdate, final User updateUserDetails) throws UsernameNotFoundException {
            final User user = populateUserWithUpdateData(userToUpdate, updateUserDetails);
            return userRepository.save(user);
    }

    private User populateUserWithUpdateData(final User user, final User updateUserDetails) {

        final String password = updateUserDetails.getPassword();
        if (StringUtils.isNotBlank(password)) {
            user.setPassword(SecurityUtil.encode(password));
        }

        final String email = updateUserDetails.getEmail();
        if (StringUtils.isNotBlank(email)) {
            user.setEmail(email);
        }

        return user;
    }


    private boolean isValidRegistration(final User user) {
        final Optional<User> maybeUser = userRepository.findByEmail(user.getEmail());
        return !maybeUser.isPresent();
    }

    /**
     * todo: public for now for mocking purposes, must probably change the way we do login
     * @param request to call the login method on
     * @param username the users username
     * @param password users password
     * @throws ServletException
     */
    public void logUserIn(final HttpServletRequest request, final String username,
                             final String password) throws ServletException {
        request.login(username, password);
    }

}
