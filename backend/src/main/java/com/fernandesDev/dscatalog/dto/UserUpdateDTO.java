package com.fernandesDev.dscatalog.dto;


import com.fernandesDev.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid //Anotação criada para validar se o email já se encontra cadastrado no bando de dados por outro usuário
public class UserUpdateDTO extends UserDTO{

    private String password;

    public UserUpdateDTO(){
        super();
    }
    public UserUpdateDTO(String password){
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
