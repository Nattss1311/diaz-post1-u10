package com.universidad.enviosservice.service;

public class ServicioCalculoEnvio {

    public double calcularCosto(String metodoEnvio, double pesoKg, double distanciaKm,
                                String tipoCliente, boolean esFragil) {
        double costo;
        if (metodoEnvio.equals("ESTANDAR")) {
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
        } else if (metodoEnvio.equals("EXPRESS")) {
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
            costo = costo * 1.3;
        } else if (metodoEnvio.equals("OVERNIGHT")) {
            if (distanciaKm > 300) {
                throw new IllegalArgumentException("OVERNIGHT no disponible para mas de 300 km");
            }
            if (pesoKg <= 5) {
                costo = distanciaKm * 2.0 + 20;
            } else if (pesoKg <= 20) {
                costo = distanciaKm * 2.0 + 20 + (pesoKg - 5) * 2.5;
            } else {
                costo = distanciaKm * 2.0 + 20 + 15 * 2.5 + (pesoKg - 20) * 3.0;
            }
        } else if (metodoEnvio.equals("INTERNACIONAL")) {
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
        } else {
            throw new IllegalArgumentException("Metodo de envio no soportado: " + metodoEnvio);
        }

        if (esFragil) {
            costo = costo + costo * 0.15;
        }

        if (tipoCliente.equals("PREMIUM")) {
            costo = costo * 0.9;
        } else if (tipoCliente.equals("CORPORATIVO")) {
            costo = costo * 0.85;
        }

        return costo;
    }
}