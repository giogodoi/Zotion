package com.zotion.backend.repositories;

import com.zotion.backend.models.Nota;
import com.zotion.backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface NotaRepository extends JpaRepository<Nota, UUID> {
    List<Nota> findAllByUsuario(Usuario usuario);
}