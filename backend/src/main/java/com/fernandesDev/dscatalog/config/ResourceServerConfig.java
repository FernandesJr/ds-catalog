package com.fernandesDev.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private Environment environment; //Ambiente de execução, nesta class focado para config o db h2

    public static final String[] PUBLIC = {"/oauth/token", "/h2-console/**"}; //login
    public static final String[] OPERATOR_OR_ADMIN = {"/products/**","/categories/**"}; //PUBLIC para somente o GET, para os demais métodos é necessário um ROLE
    public static final String[] ADMIN = {"/users/**"}; //PermitAll

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_OPERATOR = "OPERATOR";


    @Autowired
    private JwtTokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(PUBLIC).permitAll()
                .antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll() //Nessas rotas somente o método GET pode ser acessado sem ROLES
                .antMatchers(OPERATOR_OR_ADMIN).hasAnyRole(ROLE_OPERATOR, ROLE_ADMIN)
                .antMatchers(ADMIN).hasRole(ROLE_ADMIN)
                .anyRequest().authenticated(); //Informando que qualquer outra rota não especificada será necessário se autenticar

        //Configuração especial para rodar o H2 com a segurança dos recursos
        if(Arrays.asList(environment.getActiveProfiles()).contains("test")){
            http.headers().frameOptions().disable();
        }
    }
}
