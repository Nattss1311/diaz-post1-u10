package com.universidad.enviosservice.estrategia;

public class EnvioExpressStrategy implements EstrategiaEnvio {
    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        double costo;
        if (pesoKg <= 5) {
            costo = distanciaKm * 1.2;
        } else if (pesoKg <= 20) {
            costo = distanciaKm * 1.2 + (pesoKg - 5) * 1.5;
        } else {
            costo = distanciaKm * 1.2 + 15 * 1.5 + (pesoKg - 20) * 2.0;
        }
        if (distanciaKm > 500) {
            costo += 25;
        }
        return costo * 1.3;
    }
}