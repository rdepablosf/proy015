package es.cic.curso25.proy015.controller;

import es.cic.curso25.proy015.model.Plaza;
import es.cic.curso25.proy015.model.Vehiculo;
import es.cic.curso25.proy015.model.Multa;
import es.cic.curso25.proy015.service.GarajeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/garaje")
public class GarajeController {

    @Autowired
    private GarajeService garajeService;

    // crear plaza
    @PostMapping("/plazas")
    public Plaza crearPlaza(@RequestParam String numero) {
        return garajeService.crearPlaza(numero);
    }

    // listar plazas
    @GetMapping("/plazas")
    public List<Plaza> listarPlazas() {
        return garajeService.listarPlazas();
    }

    // registrar vehículo en plaza
    @PostMapping("/vehiculos")
    public Vehiculo registrarVehiculo(@RequestParam String matricula, @RequestParam Long idPlaza) {
        return garajeService.registrarVehiculo(matricula, idPlaza);
    }

    // asignar vehículo a plaza
    @PutMapping("/vehiculos/{idVehiculo}/asignar-plaza/{idPlaza}")
    public Vehiculo asignarVehiculoAPlaza(@PathVariable Long idVehiculo, @PathVariable Long idPlaza) {
        garajeService.asignarVehiculoAPlaza(idVehiculo, idPlaza);
        return garajeService.asignarPlaza(idVehiculo, idPlaza);
    }

    // poner multa a vehículo
    @PostMapping("/vehiculos/{idVehiculo}/multas")
    public Multa ponerMulta(@PathVariable Long idVehiculo,
            @RequestParam String motivo,
            @RequestParam double importe) {
        return garajeService.ponerMulta(idVehiculo, motivo, importe);
    }

    // comprobar si el vehículo está en una plaza diferente y poner multa si es
    // necesario
    @PostMapping("/vehiculos/{idVehiculo}/comprobar-multa")
    public Multa comprobarMultaPorPlaza(@PathVariable Long idVehiculo, @RequestParam Long idPlazaActual) {
        return garajeService.comprobarYPonerMultaPorPlaza(idVehiculo, idPlazaActual);
    }

    // listar multas de vehículo
    @GetMapping("/vehiculos/{idVehiculo}/multas")
    public List<Multa> listarMultasVehiculo(@PathVariable Long idVehiculo) {
        return garajeService.listarMultasVehiculo(idVehiculo);
    }
}
