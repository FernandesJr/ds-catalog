package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.dto.ClientDTO;
import com.fernandesDev.dscatalog.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Page<ClientDTO> findPaged(PageRequest pageRequest){
        return clientRepository.findAll(pageRequest).map(c -> new ClientDTO(c));
    }
}
