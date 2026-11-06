package com.universidad.enviosservice.estrategia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvioInternacionalStrategyTest {

    private final EnvioInternacionalStrategy estrategia = new EnvioInternacionalStrategy();

    @ParameterizedTest
    @CsvSource({
            "3.0, 100.0, 230.0",   // peso <= 5: 100*1.8 + 50 = 230
            "5.0, 100.0, 230.0",   // frontera: peso = 5 sigue en el primer tramo
            "10.0, 100.0, 245.0",  // 5 < peso <= 20: 230 + 5*3 = 245
            "20.0, 100.0, 275.0",  // frontera: 230 + 15*3 = 275
            "25.0, 100.0, 297.5"   // peso > 20: 230 + 45 + 5*4.5 = 297.5
    })
    void calculaCostoPorTramosDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @Test
    void aplicaRecargoDistanciaMayorA5000Km() {
        // 6000 * 1.8 + 50 + 100 = 10950
        assertEquals(10950.0, estrategia.calcular(3.0, 6000.0), 0.01);
    }

    @Test
    void noAplicaRecargoConDistanciaExactaDe5000Km() {
        // frontera: 5000 * 1.8 + 50 = 9050, la condicion es > 5000
        assertEquals(9050.0, estrategia.calcular(3.0, 5000.0), 0.01);
    }
}
