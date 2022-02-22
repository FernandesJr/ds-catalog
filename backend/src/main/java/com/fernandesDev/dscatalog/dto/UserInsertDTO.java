package com.fernandesDev.dscatalog.dto;

import com.fernandesDev.dscatalog.services.validation.UserInsertValid;

@UserInsertValid //Anotação criada para validar se o email já se encontra cadastrado no bando de dados
public class UserInsertDTO extends UserDTO{

    private String password;

    public UserInsertDTO(){
        super();
    }
    public UserInsertDTO(String password){
        super();
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
