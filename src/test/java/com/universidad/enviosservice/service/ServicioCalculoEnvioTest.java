package com.universidad.enviosservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ServicioCalculoEnvioTest {

    private final ServicioCalculoEnvio servicio = new ServicioCalculoEnvio();

    // --- Suite inicial (Parte 1), se conserva sin cambios ---

    @Test
    void calculaEnvioEstandarPesoBajo() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", false);
        assertEquals(50.0, costo, 0.01);
    }

    @Test
    void aplicaDescuentoClientePremium() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", false);
        assertEquals(45.0, costo, 0.01);
    }

    // --- Pruebas agregadas en la Parte 2 (Decisión de diseño 5) ---

    @ParameterizedTest
    @CsvSource({
            "ESTANDAR, 50.0",        // 100 * 0.5
            "EXPRESS, 156.0",        // (100 * 1.2) * 1.3
            "OVERNIGHT, 220.0",      // 100 * 2.0 + 20
            "INTERNACIONAL, 230.0"   // 100 * 1.8 + 50
    })
    void delegaEnLaEstrategiaCorrespondienteAlMetodo(String metodoEnvio, double esperado) {
        // Verifica que el mapa del orquestador asocia cada clave con su estrategia
        double costo = servicio.calcularCosto(metodoEnvio, 3.0, 100.0, "REGULAR", false);
        assertEquals(esperado, costo, 0.01);
    }

    @Test
    void aplicaRecargoPorPaqueteFragil() {
        // 50 + 50 * 0.15 = 57.5
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", true);
        assertEquals(57.5, costo, 0.01);
    }

    @Test
    void aplicaDescuentoClienteCorporativo() {
        // 50 * 0.85 = 42.5
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "CORPORATIVO", false);
        assertEquals(42.5, costo, 0.01);
    }

    @Test
    void aplicaFragilidadAntesQueDescuentoDeCliente() {
        // (50 * 1.15) * 0.9 = 51.75: el orden de las reglas transversales importa
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", true);
        assertEquals(51.75, costo, 0.01);
    }

    @Test
    void rechazaMetodoDeEnvioNoSoportado() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.calcularCosto("MARITIMO", 3.0, 100.0, "REGULAR", false));
        assertEquals("Metodo de envio no soportado: MARITIMO", ex.getMessage());
    }
}
