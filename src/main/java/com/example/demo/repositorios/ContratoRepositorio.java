
package com.example.demo.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entidades.Contrato;

public interface ContratoRepositorio
        extends JpaRepository<Contrato, Long> {

}
