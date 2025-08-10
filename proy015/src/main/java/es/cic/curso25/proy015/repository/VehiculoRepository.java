package es.cic.curso25.proy015.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cic.curso25.proy015.model.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    List<Vehiculo> findByPlazaId(Long idPlaza);

}
