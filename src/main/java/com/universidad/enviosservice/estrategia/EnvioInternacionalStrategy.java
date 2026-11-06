package com.universidad.enviosservice.estrategia;

public class EnvioInternacionalStrategy implements EstrategiaEnvio {
    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        double costo;
        if (pesoKg <= 5) {
            costo = distanciaKm * 1.8 + 50;
        } else if (pesoKg <= 20) {
            costo = distanciaKm * 1.8 + 50 + (pesoKg - 5) * 3.0;
        } else {
            costo = distanciaKm * 1.8 + 50 + 15 * 3.0 + (pesoKg - 20) * 4.5;
        }
        if (distanciaKm > 5000) {
            costo += 100;
        }
        return costo;
    }
}