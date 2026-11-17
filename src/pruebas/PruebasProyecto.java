package pruebas;

import algoritmos.AlgoritmosRecursivos;
import java.time.LocalDate;
import modelo.*;
import servicio.GestorMonedero;

public final class PruebasProyecto {

    private PruebasProyecto() {
    }

    public static GestorMonedero ejecutarSuiteDePruebasSemilla() {
        System.out.println("==========================================================================");
        System.out.println("  SUITE DE PRUEBAS AUTOMATICAS - SIMULADOR MONEDERO VIRTUAL GAMIFICADO");
        System.out.println("==========================================================================\n");

        GestorMonedero gestor = new GestorMonedero();

        // 1. Registro de clientes semilla
        gestor.registrarCliente("C01", "Ana Lopez", 8500.0);
        gestor.registrarCliente("C02", "Maritza Calderon", 4200.0);
        gestor.registrarCliente("C03", "Duvan Palomares", 15000.0);
        gestor.registrarCliente("C04", "Robinson Arias", 1200.0);

        // 2. Prueba de Depositos, Retiros y Transferencias con acumulacion de puntos
        // Deposito: 1 pt por cada 100 | Retiro: 2 pts por cada 100 | Transferencia: 3 pts por cada 100
        gestor.realizarDeposito("C01", "Principal", 2000.0); // +20 pts
        gestor.realizarRetiro("C01", "Principal", "Alimentacion", 1500.0); // +30 pts
        gestor.realizarDeposito("C01", "Principal", 30000.0); // +300 pts
        gestor.realizarTransferencia("C01", "C02", "Arriendo y Servicios", 10000.0); // +300 pts -> Total C01 = 650 pts (Rango PLATA)

        // Subir a C03 a rango PLATINO (>5000 puntos) y C02 a rango ORO (1001-5000 puntos)
        gestor.realizarDeposito("C03", "Principal", 200000.0); // +2000 pts
        gestor.realizarTransferencia("C03", "C01", "Inversion Negocio", 120000.0); // +3600 pts -> Total C03 = 5600 pts (PLATINO)

        gestor.realizarDeposito("C02", "Principal", 60000.0); // +600 pts
        gestor.realizarRetiro("C02", "Principal", "Educacion", 30000.0); // +600 pts -> Total C02 = 1200 pts (ORO)

        // 3. Prueba de Multimonedero y Grafo Dirigido de subcuentas
        gestor.transferirEntreSubmonederos("C01", "Principal", "Ahorros", 2500.0);
        gestor.transferirEntreSubmonederos("C01", "Principal", "Gastos Diarios", 800.0);

        // 4. Prueba de Reversion con Pila LIFO y recalculo dinamico de puntos
        Transaccion txTemporal = gestor.realizarRetiro("C04", "Principal", "Ocio", 500.0); // +10 pts
        System.out.println("[PRUEBA PILA LIFO] C04 antes de revertir: " + gestor.consultarPuntosHashO1("C04") + " pts.");
        gestor.revertirUltimaTransaccion("C04");
        System.out.println("[PRUEBA PILA LIFO] C04 despues de revertir (" + txTemporal.getIdTransaccion() + "): "
                + gestor.consultarPuntosHashO1("C04") + " pts.\n");

        // 5. Prueba de Canje de Puntos (100 pts)
        String beneficio = gestor.canjearPuntos("C01", 100);
        System.out.println("[PRUEBA CANJE] Cliente C01 canjeo 100 pts -> " + beneficio);
        System.out.println("[PRUEBA TABLA HASH O(1)] Puntos actuales de C01: " + gestor.consultarPuntosHashO1("C01") + " pts (Rango: "
                + gestor.buscarCliente("C01").getRango() + ")\n");

        // 6. Prueba de Cola de Prioridad de Transacciones Programadas (Orden cronologico)
        gestor.programarTransaccion(TipoTransaccion.TRANSFERENCIA, "C01", "C02", "Suscripcion", 150.0, LocalDate.now().plusDays(10), "Mensual");
        gestor.programarTransaccion(TipoTransaccion.DEPOSITO, "C01", null, "Ahorro Nomina", 1000.0, LocalDate.now().plusDays(2), "Quincenal");
        gestor.programarTransaccion(TipoTransaccion.RETIRO, "C02", null, "Gimnasio", 120.0, LocalDate.now().plusDays(5), "Mensual");

        System.out.println("[PRUEBA COLA DE PRIORIDAD] Orden de ejecucion programado por fecha mas proxima:");
        for (TransaccionProgramada tp : gestor.getColaProgramadas()) {
            System.out.println("   " + tp);
        }

        // 7. Prueba de Arbol AVL de Rangos y Arbol ABB de Puntos
        System.out.println("\n[PRUEBA ARBOL AVL BALANCEADO] Ranking de Clientes de Mayor a Menor Puntaje:");
        for (Cliente c : gestor.obtenerRankingRangosAVL()) {
            System.out.println("   " + c);
        }

        // 8. Prueba de Verificacion Recursiva de Integridad y Divide y Venceras
        Cliente c01 = gestor.buscarCliente("C01");
        boolean integro = AlgoritmosRecursivos.verificarIntegridadHistorial(c01.getHistorialTransacciones());
        Transaccion mayor = AlgoritmosRecursivos.transaccionMayorMonto(c01.getHistorialTransacciones());
        System.out.println("\n[PRUEBA RECURSIVIDAD] Integridad del historial de C01: " + integro);
        System.out.println("[PRUEBA DIVIDE Y VENCERAS] Transaccion de mayor monto de C01: " + mayor);
        System.out.println("==========================================================================\n");

        return gestor;
    }
}