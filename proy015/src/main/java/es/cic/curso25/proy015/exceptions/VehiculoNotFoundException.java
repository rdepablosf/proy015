package es.cic.curso25.proy015.exceptions;

public class VehiculoNotFoundException extends RuntimeException {
    public VehiculoNotFoundException(Long idVehiculo) {
        super("No se encontró el vehículo con ID: " + idVehiculo);
    }
}