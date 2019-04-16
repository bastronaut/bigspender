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

import java.util.Optional;

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
        throw new UsernameNotFoundException(String.format("User not found: %s", username));
    }

    public User registerUser(final User user) throws UserRegistrationException {
        if (isValidRegistration(user)) {
            final String encodedPassword = SecurityUtil.encode(user.getPassword());
            return userRepository.save(new User(user.getEmail(), user.getName(), encodedPassword));
        }
        throw new UserRegistrationException("User already exists: " + user.getEmail());
    }

    /**
     * Allows updating of name and password, not the username
     * @param userId the users user ID
     * @param updateUserDetails the new user with fields populated according to changes
     * @return the new user if saved to database successfully
     * @throws UsernameNotFoundException if the user does not exist
     */
    public User updateUser(final int userId, final User updateUserDetails) throws UsernameNotFoundException {
        final Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            final User user = populateUserWithUpdateData(optionalUser.get(), updateUserDetails);
            return userRepository.save(user);
        }
        throw new UserUpdateException("User to update does not exist: " + updateUserDetails.getEmail());
    }

    private User populateUserWithUpdateData(final User user, final User updateUserDetails) {

        final String name = updateUserDetails.getName();
        if (StringUtils.isNotBlank(name)){
            user.setName(name);
        }

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

}
