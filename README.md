# Proyecto Final: Simulador de un "Monedero Virtual con Sistema de Puntos"

## Información Académica
- **Institución:** Universidad del Quindío
- **Programa:** Ingeniería de Sistemas y Computación
- **Espacio Académico:** Estructuras de Datos (2026-2)
- **Docente:** Robinson Arias
- **Integrantes:** Ana Ligia López y Maritza Calderón Zapata
- **Repositorio:** https://github.com/AnaLopez-1/ProyectoFinal.git

---

## Descripción del Proyecto
Aplicación de escritorio con interfaz gráfica desarrollada en **Java Swing** que simula un monedero virtual gamificado. Permite gestionar múltiples cuentas y submonederos (*Principal*, *Ahorros*, *Gastos Diarios*), ejecutar depósitos, retiros y transferencias con cálculo automático de puntos, clasificar clientes en rangos dinámicos (*Bronce*, *Plata*, *Oro*, *Platino*), canjear beneficios, programar transacciones recurrentes, analizar patrones de gasto y revertir operaciones de forma segura.

---

## Arquitectura y Estructuras de Datos Implementadas
Todas las estructuras de datos fueron diseñadas e implementadas desde cero en Java utilizando tipado genérico (`<T>`, `<K, V>`) y encapsulamiento estricto de nodos internos:

```text
ProyectoFinal/
├── README.md
└── src/
    ├── algoritmos/
    │   └── AlgoritmosRecursivos.java  # Verificación recursiva de integridad y Divide y Vencerás
    ├── estructuras/
    │   ├── ArbolABB.java              # Indexación y gestión de puntos acumulados
    │   ├── ArbolAVL.java              # Árbol auto-balanceado (FE) para rangos de clientes
    │   ├── ColaPrioridad.java         # Procesamiento cronológico de transacciones programadas
    │   ├── Coleccion.java             # Interfaz genérica base del TAD
    │   ├── GrafoDirigido.java         # Patrones de gasto, transferencias y multimonedero
    │   ├── ListaCircular.java         # Gestión rotativa de alertas y notificaciones
    │   ├── ListaEnlazada.java         # Historial de transacciones por cliente con Iterable<T>
    │   ├── Pila.java                  # Reversión LIFO de transacciones y ajuste dinámico de puntos
    │   └── TablaHash.java             # Consulta y actualización en O(1) de puntos por cliente
    ├── modelo/
    │   ├── Cliente.java               # Titular, saldos, puntos, rango y beneficios activos
    │   ├── Monedero.java              # Subcuentas (Principal, Ahorros, Gastos Diarios)
    │   ├── RangoCliente.java          # Enum: Bronce (0-500), Plata (501-1000), Oro (1001-5000), Platino (>5000)
    │   ├── TipoTransaccion.java       # Enum: Depósito (1 pt/100), Retiro (2 pts/100), Transferencia (3 pts/100)
    │   ├── Transaccion.java           # Registro inmutable de auditoría
    │   └── TransaccionProgramada.java # Entidad comparable por fecha de ejecución
    ├── pruebas/
    │   └── PruebasProyecto.java       # Suite de pruebas automáticas y carga de datos semilla
    ├── servicio/
    │   └── GestorMonedero.java        # Orquestador central de reglas de negocio y estructuras
    ├── vista/
    │   └── VentanaMonedero.java       # Interfaz gráfica interactiva en Java Swing
    └── Main.java                      # Punto de entrada principal