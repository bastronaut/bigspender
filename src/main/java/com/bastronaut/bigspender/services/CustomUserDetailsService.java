package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.config.SecurityUtil;
import com.bastronaut.bigspender.dto.in.UserUpdateDTO;
import com.bastronaut.bigspender.exceptions.LoginAttemptException;
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

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_TOO_MANY_LOGIN_ATTEMPTS;
import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_USER_EXISTS;
import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_USER_NOTFOUND;
import static com.bastronaut.bigspender.utils.ApplicationConstants.MAX_LOGIN_ATTEMPTS;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException, LoginAttemptException {

        int attempts = loginAttemptService.getLoginAttempts(username);
        if (attempts > MAX_LOGIN_ATTEMPTS) {
            throw new LoginAttemptException(ERRORMSG_TOO_MANY_LOGIN_ATTEMPTS);
        }

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
     * Perhaps not ideal, the service is now also aware of a DTO class. The logic for updating the userToUpdate
     * is put in this service however as the amount of logic may grow as we add fields to the User class. Additionally,
     * this makes unit testing the controller a bunch easier as we can mock the response from the updateUser method
     * @param userToUpdate the user to update information for
     * @param userUpdateDTO a DTO containing updatable information
     * @return the updated User
     * @throws UsernameNotFoundException
     */
    public User updateUser(final User userToUpdate, final UserUpdateDTO userUpdateDTO) throws UsernameNotFoundException {

        if (StringUtils.isNotEmpty(userUpdateDTO.getEmail())) {
            userToUpdate.setEmail(userUpdateDTO.getEmail());
        }
        if (StringUtils.isNotEmpty(userUpdateDTO.getPassword())) {
            userToUpdate.setPassword(userUpdateDTO.getPassword());
        }
        return userRepository.save(userToUpdate);
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
