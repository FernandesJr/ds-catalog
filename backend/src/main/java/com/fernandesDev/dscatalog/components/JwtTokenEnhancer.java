package com.fernandesDev.dscatalog.components;

import com.fernandesDev.dscatalog.entities.User;
import com.fernandesDev.dscatalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    @Autowired
    private UserRepository userRepository;

    //Adicionando mais atributos ao Token
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

        User user = userRepository.findByEmail(oAuth2Authentication.getName()); //O nome da autenticação que neste caso é o email
        Map<String, Object> map =  new HashMap<>();
        map.put("firstName", user.getFirstName());
        map.put("userId", user.getId());

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) oAuth2AccessToken; //Um tipo da mesma implementação, porém mais específico
        token.setAdditionalInformation(map);

        //Ficam salvas as mesmas referências das variáveis
        return oAuth2AccessToken;
    }
}
