package com.universidad.enviosservice.estrategia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvioEstandarStrategyTest {

    private final EnvioEstandarStrategy estrategia = new EnvioEstandarStrategy();

    @ParameterizedTest
    @CsvSource({
            "3.0, 100.0, 50.0",     // peso <= 5
            "10.0, 100.0, 54.0",    // 5 < peso <= 20 (50 + 5*0.8)
            "25.0, 100.0, 68.0"     // peso > 20 (50 + 12 + 5*1.2)
    })
    void calculaCostoPorTramosDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @Test
    void aplicaRecargoDistanciaMayorA500Km() {
        // peso 3.0 (<5) -> dist 600 * 0.5 = 300 + 10 recargo = 310
        assertEquals(310.0, estrategia.calcular(3.0, 600.0), 0.01);
    }
}