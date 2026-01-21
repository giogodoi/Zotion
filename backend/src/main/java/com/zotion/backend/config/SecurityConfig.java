package com.zotion.backend.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

//Gerei as chaves RSA usando o comando abaixo no terminal:
//openssl genrsa -out key.priv 2048
//openssl rsa -in key.priv -pubout -out key.pub

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Um detalhe: JAMAIS devemos subir as chaves privadas para repositórios públicos.
    //Aqui estou fazendo isso apenas para fins didáticos.
    //Em um ambiente real, devemos usar variáveis de ambiente ou serviços de gerenciamento de segredos.
    
    @Value("${jwt.public.key}")
    private RSAPublicKey chavePublica;

    @Value("${jwt.private.key}")
    private RSAPrivateKey chavePrivada;

    @Bean
    public SecurityFilterChain filtroSeguranca(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(autorizar -> autorizar
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/autenticacao/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/autenticacao/cadastro").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public JwtDecoder decodificadorJwt() {
        return NimbusJwtDecoder.withPublicKey(chavePublica).build();
    }

    @Bean
    public JwtEncoder codificadorJwt() {
        JWK jwk = new RSAKey.Builder(this.chavePublica).privateKey(chavePrivada).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public BCryptPasswordEncoder codificadorSenha() {
        return new BCryptPasswordEncoder();
    }
}