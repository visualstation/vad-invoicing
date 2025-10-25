package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.User;
import eu.ageekatyourservice.vadinvoicing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(User user, String rawPasswordIfProvided) {
        if (user.getId() == null) {
            if (rawPasswordIfProvided == null || rawPasswordIfProvided.isBlank()) {
                throw new IllegalArgumentException("Password is required for new users");
            }
            user.setPassword(passwordEncoder.encode(rawPasswordIfProvided));
        } else {
            if (rawPasswordIfProvided != null && !rawPasswordIfProvided.isBlank()) {
                user.setPassword(passwordEncoder.encode(rawPasswordIfProvided));
            } else {
                String existingPassword = userRepository.findById(user.getId())
                        .map(User::getPassword)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: id=" + user.getId()));
                user.setPassword(existingPassword);
            }
        }

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Username must be unique", e);
        }
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
