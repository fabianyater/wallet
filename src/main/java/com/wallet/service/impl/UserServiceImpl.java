package com.wallet.service.impl;

import com.wallet.entity.User;
import com.wallet.repository.UserRepository;
import com.wallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> get() throws Exception {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getById(Integer id) throws Exception {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) throws Exception {
        return userRepository.save(user);
    }
    @Override
    public boolean delete(Integer id) throws Exception {
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User usr = userRepository.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(usr.getUsername(), usr.getPassword(), new ArrayList<>());
    }
}
