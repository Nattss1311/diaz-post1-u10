package com.universidad.enviosservice.estrategia;

public class EnvioEstandarStrategy implements EstrategiaEnvio {
    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        double costo;
        if (pesoKg <= 5) {
            costo = distanciaKm * 0.5;
        } else if (pesoKg <= 20) {
            costo = distanciaKm * 0.5 + (pesoKg - 5) * 0.8;
        } else {
            costo = distanciaKm * 0.5 + 15 * 0.8 + (pesoKg - 20) * 1.2;
        }
        if (distanciaKm > 500) {
            costo += 10;
        }
        return costo;
    }
}