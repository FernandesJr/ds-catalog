package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.dto.ClientDTO;
import com.fernandesDev.dscatalog.entities.Client;
import com.fernandesDev.dscatalog.repositories.ClientRepository;
import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findPaged(Pageable pageable){
        return clientRepository.findAll(pageable).map(c -> new ClientDTO(c));
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return new ClientDTO(client);
    }

    @Transactional
    public ClientDTO insert(ClientDTO dto) {
        Client client = new Client();
        CopyDtoToEntity(client, dto);
        client = clientRepository.save(client);
        return new ClientDTO(client);
    }

    @Transactional
    public ClientDTO update(Long id, ClientDTO dto) {
        try {
            Client client =  clientRepository.getById(id);
            CopyDtoToEntity(client, dto);
            client = clientRepository.save(client);
            return new ClientDTO(client);
        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Entity not found by id: "+id);
        }
    }

    @Transactional
    public void delete(Long id){
        try{
            clientRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Entity not found by id: "+id);
        }
    }

    private void CopyDtoToEntity(Client client, ClientDTO dto){
        client.setName(dto.getName());
        client.setCpf(dto.getCpf());
        client.setIncome(dto.getIncome());
        client.setBirthDate(dto.getBirthDate());
        client.setChildren(dto.getChildren());
    }
}
