package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.dto.RoleDTO;
import com.fernandesDev.dscatalog.dto.UserDTO;
import com.fernandesDev.dscatalog.dto.UserInsertDTO;
import com.fernandesDev.dscatalog.dto.UserUpdateDTO;
import com.fernandesDev.dscatalog.entities.User;
import com.fernandesDev.dscatalog.repositories.RoleRepository;
import com.fernandesDev.dscatalog.repositories.UserRepository;
import com.fernandesDev.dscatalog.services.exceptions.DataBaseException;
import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class UserService implements UserDetailsService {


    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findPaged(Pageable pageable){
        return repository.findAll(pageable).map(u -> new UserDTO(u));
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User user = new User();
        CopyDtoToEntity(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword())); //Criptografando a senha
        user = repository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User user = repository.getById(id); //N??o acessa ao database, retorna apenas uma inst??ncia com id
            CopyDtoToEntity(dto, user);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user = repository.save(user); //Por causa do vinculo do getById mesmo sendo um id inexistente ele n??o salva na bd
            return new UserDTO(user);
        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Entity not find by id "+id);
        }
    }

    public void delete(Long id) {
        try{
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            //Caso o id n??o tenha na bd
            throw new ResourceNotFoundException("Entity not found by id "+id);
        } catch (DataIntegrityViolationException d){
            //Caso tenho algum produto vinculado a User n??o pode ser exclu??da
            throw new DataBaseException("Integrity violation");
        }
    }

    private void CopyDtoToEntity(UserDTO dto, User user) {
        //Em estado de percist??ncia
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        for(RoleDTO role : dto.getRoles()){
            user.getRoles().add(roleRepository.getById(role.getId())); //Uma refer??ncia ao bd *getby*
        }
    }

    //SpringSecurity retorna o user by email
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username);
        if(user == null){
            logger.error("User not found: " + username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        logger.info("User found: " + username);
        return user;
    }
}
