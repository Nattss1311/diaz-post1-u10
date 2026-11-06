package com.universidad.enviosservice.estrategia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvioExpressStrategyTest {

    private final EnvioExpressStrategy estrategia = new EnvioExpressStrategy();

    @ParameterizedTest
    @CsvSource({
            "3.0, 100.0, 156.0",    // (100 * 1.2) * 1.3 = 156.0
            "10.0, 100.0, 165.75",  // (120 + 5*1.5) * 1.3 = 127.5 * 1.3 = 165.75
            "25.0, 100.0, 198.25"   // (120 + 22.5 + 5*2.0) * 1.3 = 152.5 * 1.3 = 198.25
    })
    void calculaCostoPorTramosDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @Test
    void aplicaRecargoDistanciaMayorA500Km() {
        // (600 * 1.2 + 25) * 1.3 = 745 * 1.3 = 968.5
        assertEquals(968.5, estrategia.calcular(3.0, 600.0), 0.01);
    }
}