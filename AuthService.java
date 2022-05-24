package org.prod.tgk.services;

import org.prod.tgk.entitys.*;
import org.prod.tgk.repositories.ApartmentRepository;
import org.prod.tgk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ApartmentRepository apartmentRepository;
    @Autowired
    MailService mailService;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public boolean activate(String code){
        Optional<User> checkCode = userRepository.findByActivationCode(code);
        if(checkCode.isPresent()){
            User activateIt = checkCode.get();
            activateIt.setActivationCode(null);
            userRepository.save(activateIt);
            return true;
        }else{
            return false;
        }
    }

    public void saveUser(String email, String name, String surname, String patronymic, Date birthday, String password){
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setSurname(surname);
        user.setPatronymic(patronymic);
        user.setBirthday(birthday);
        user.setRole(Role.ROLE_USER);
        user.setPassword(passwordEncoder.encode(password));
        /*
            С 30.05.2022 Gmail объявил, что запретит
            использовать его API в сторонних приложениях.
            Нужно либо найти альтернативный API,
            либо выпилить эту фичу (инструкция ниже).
        */

        user.setActivationCode(UUID.randomUUID().toString()); //закомментить это
        userRepository.save(user);
        mailService.activate(user); //закомментить это
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDetails> currentUser = userRepository.findByEmail(username);
        if(currentUser.isPresent()) {
            return currentUser.get();
        }else{
            throw new UsernameNotFoundException("Пользователь не найден");
        }
    }
}
