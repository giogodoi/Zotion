package com.zotion.backend.repositories;

import com.zotion.backend.models.Lembrete;
import com.zotion.backend.models.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

//Especificaremos a ordenação no serviço ao buscar lembretes de um usuário
//A ideia será ordenarmos primeiro o de maior prioridade e depois o de adata/hora mais próxima!!

public interface LembreteRepository extends JpaRepository<Lembrete, UUID> {
    List<Lembrete> findAllByUsuario(Usuario usuario, Sort ordenacao);
}