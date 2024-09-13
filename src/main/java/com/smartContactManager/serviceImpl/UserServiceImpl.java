package com.smartContactManager.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartContactManager.dao.UserRepository;
import com.smartContactManager.entities.User;
import com.smartContactManager.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncode;

	@Override
	public User addUser(User user) {
		user.setRole("USER");
		user.setEnabled(true);
		user.setImageUrl("default.png");
		user.setPassword(passwordEncode.encode(user.getPassword()));
		userRepository.save(user);
		return user;
	}

}
