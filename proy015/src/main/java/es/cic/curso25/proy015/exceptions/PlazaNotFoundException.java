package es.cic.curso25.proy015.exceptions;

public class PlazaNotFoundException extends RuntimeException {
    public PlazaNotFoundException(Long idPlaza) {
        super("No se encontró la plaza con ID: " + idPlaza);
    }
}
