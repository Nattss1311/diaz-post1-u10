package com.universidad.enviosservice.estrategia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvioOvernightStrategyTest {

    private final EnvioOvernightStrategy estrategia = new EnvioOvernightStrategy();

    @ParameterizedTest
    @CsvSource({
            "3.0, 100.0, 220.0",   // peso <= 5: 200 + 20
            "5.0, 100.0, 220.0",   // frontera: peso = 5 sigue en el primer tramo
            "8.0, 100.0, 227.5",   // 5 < peso <= 20: 220 + 3*2.5
            "20.0, 100.0, 257.5",  // frontera: 220 + 15*2.5
            "25.0, 100.0, 272.5"   // peso > 20: 220 + 37.5 + 5*3.0 (la guia indica 250.0, ver README)
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @Test
    void aceptaDistanciaExactaDe300Km() {
        // frontera: la restriccion es > 300, asi que 300 km es valido: 300*2 + 20 = 620
        assertEquals(620.0, estrategia.calcular(3.0, 300.0), 0.01);
    }

    @Test
    void rechazaDistanciasMayoresA300Km() {
        assertThrows(IllegalArgumentException.class, () -> estrategia.calcular(3.0, 301.0));
    }
}
