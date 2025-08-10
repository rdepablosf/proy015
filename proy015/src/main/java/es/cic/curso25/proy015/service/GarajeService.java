package es.cic.curso25.proy015.service;

import es.cic.curso25.proy015.model.Plaza;
import es.cic.curso25.proy015.model.Vehiculo;
import es.cic.curso25.proy015.model.Multa;
import es.cic.curso25.proy015.repository.PlazaRepository;
import es.cic.curso25.proy015.repository.VehiculoRepository;
import es.cic.curso25.proy015.repository.MultaRepository;
import es.cic.curso25.proy015.exceptions.VehiculoNotFoundException;
import es.cic.curso25.proy015.exceptions.PlazaNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GarajeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GarajeService.class);

    @Autowired
    private PlazaRepository plazaRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private MultaRepository multaRepository;

    private static final int MAX_VEHICULOS_POR_PLAZA = 5;

    // -------- PLAZA --------
    public Plaza crearPlaza(String numero) {
        Plaza plaza = new Plaza(numero);
        LOGGER.info("Creando nueva plaza con número: {}", numero);
        return plazaRepository.save(plaza);
    }

    public Plaza findPlazaById(Long idPlaza) {
        LOGGER.info("Buscando plaza con ID {}", idPlaza);
        return plazaRepository.findById(idPlaza)
                .orElseThrow(() -> new PlazaNotFoundException(idPlaza));
    }

    public void asignarVehiculoAPlaza(Long idVehiculo, Long idPlaza) {
        Plaza plaza = findPlazaById(idPlaza);

        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new VehiculoNotFoundException(idVehiculo));

        if (vehiculo.getPlaza() != null && vehiculo.getPlaza().getId().equals(idPlaza)) {
            LOGGER.warn("El vehículo {} ya está asignado a la plaza {}", idVehiculo, idPlaza);
            return;
        }

        List<Vehiculo> vehiculosEnPlaza = obtenerVehiculosEnPlaza(idPlaza);
        if (vehiculosEnPlaza.size() >= MAX_VEHICULOS_POR_PLAZA) {
            throw new IllegalStateException(
                    "La plaza con ID " + idPlaza + " ya tiene el máximo de " + MAX_VEHICULOS_POR_PLAZA
                            + " vehículos asignados.");
        }

        vehiculo.setPlaza(plaza);
        vehiculoRepository.save(vehiculo);

        LOGGER.info("Vehículo {} asignado a plaza {}", idVehiculo, idPlaza);
    }

    public List<Plaza> listarPlazas() {
        return plazaRepository.findAll();
    }

    // -------- VEHÍCULO --------
    public Vehiculo registrarVehiculo(String matricula, Long idPlaza) {
        Plaza plaza = findPlazaById(idPlaza);

        List<Vehiculo> vehiculosEnPlaza = obtenerVehiculosEnPlaza(idPlaza);
        if (vehiculosEnPlaza.size() >= MAX_VEHICULOS_POR_PLAZA) {
            throw new IllegalStateException(
                    "La plaza con ID " + idPlaza + " ya tiene el máximo de " + MAX_VEHICULOS_POR_PLAZA
                            + " vehículos asignados.");
        }

        Vehiculo vehiculo = new Vehiculo(matricula, plaza);
        LOGGER.info("Registrando vehículo {} en plaza {}", matricula, idPlaza);
        return vehiculoRepository.save(vehiculo);
    }

    public Vehiculo asignarPlaza(Long idVehiculo, Long idPlaza) {
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new VehiculoNotFoundException(idVehiculo));
        Plaza plaza = findPlazaById(idPlaza);

        List<Vehiculo> vehiculosEnPlaza = obtenerVehiculosEnPlaza(idPlaza);
        if (vehiculosEnPlaza.size() >= MAX_VEHICULOS_POR_PLAZA) {
            throw new IllegalStateException(
                    "La plaza con ID " + idPlaza + " ya tiene el máximo de " + MAX_VEHICULOS_POR_PLAZA
                            + " vehículos asignados.");
        }

        LOGGER.info("Asignando plaza {} al vehículo {}", idPlaza, idVehiculo);
        vehiculo.setPlaza(plaza);
        return vehiculoRepository.save(vehiculo);
    }

    // -------- MULTA --------
    public Multa ponerMulta(Long idVehiculo, String motivo, double importe) {
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new VehiculoNotFoundException(idVehiculo));

        Multa multa = new Multa(motivo, importe, vehiculo);
        LOGGER.info("Poniendo multa a vehículo {}: {} - {}", idVehiculo, motivo, importe);
        return multaRepository.save(multa);
    }

    public Multa comprobarYPonerMultaPorPlaza(Long idVehiculo, Long idPlazaActual) {
        // obtenemos el vehículo por id, o lanzamos excepción si no existe
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new VehiculoNotFoundException(idVehiculo));

        // comprobamos si la plaza asignada al vehículo es nula o distinta a la plaza
        if (vehiculo.getPlaza() == null || !vehiculo.getPlaza().getId().equals(idPlazaActual)) {
            // definimos el motivo de la multa
            String motivo = "vehículo aparcado en plaza no asignada";
            // definimos un importe fijo para la multa (puede ser variable)
            double importe = 100.0;
            // creamos y guardamos la multa
            return ponerMulta(idVehiculo, motivo, importe);
        }

        // si no hay incumplimiento, no se pone multa (retornamos null)
        return null;
    }

    public List<Multa> listarMultasVehiculo(Long idVehiculo) {
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new VehiculoNotFoundException(idVehiculo));
        return vehiculo.getMultas();
    }

    // -------- MÉTODOS PRIVADOS --------
    private List<Vehiculo> obtenerVehiculosEnPlaza(Long idPlaza) {
        return vehiculoRepository.findByPlazaId(idPlaza);
    }

}
