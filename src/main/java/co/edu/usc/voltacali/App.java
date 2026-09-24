package co.edu.usc.voltacali;

import java.util.Locale;

import co.edu.usc.voltacali.CargadorVE.TipoCargador;
import co.edu.usc.voltacali.CargadorVE.TipoConector;
import co.edu.usc.voltacali.CargadorVE.Ubicacion;

/**
 * Caso de prueba obligatorio, ruta individual y extensión personalizada de
 * VoltaCali S.A.S.
 */
public class App {

    /* Últimos dos dígitos de la cédula del estudiante. */
    private static final int NUMERO_CEDULA = 88;

    public static void main(String[] args) {
        System.out.println("=== VoltaCali S.A.S. - Prototipo de gestion de cargadores VE ===\n");

        /* Paso 1: flota base. */
        CargadorVE c1 = new CargadorVE("ABB", 2023, 400, TipoConector.CCS2,
                TipoCargador.RAPIDO_DC, 2, 2, 60, Ubicacion.UNIVERSIDAD);
        CargadorVE c2 = new CargadorVE("Siemens", 2022, 220, TipoConector.TIPO_2,
                TipoCargador.MURAL, 1, 1, 22, Ubicacion.CENTRO_COMERCIAL);
        CargadorVE c3 = new CargadorVE("Delta", 2024, 800, TipoConector.CCS2,
                TipoCargador.ULTRARRAPIDO, 2, 2, 150, Ubicacion.ESTACION_SERVICIO);
        CargadorVE c4 = new CargadorVE("Wallbox", 2021, 220, TipoConector.TIPO_2,
                TipoCargador.MURAL, 1, 1, 11, Ubicacion.RESIDENCIAL);
        CargadorVE c5 = new CargadorVE("Enel X", 2025, 22);
        CargadorVE[] flota = {c1, c2, c3, c4, c5};
        System.out.println("--- Paso 1: flota creada con " + flota.length + " cargadores ---\n");

        /* Paso 2: sesión de carga de C1. */
        System.out.println("--- Paso 2: sesion de carga sobre C1 ---");
        c1.setPotenciaActual(40);
        System.out.println("[P01] Potencia C1: " + c1.getPotenciaActual() + " kW");

        c1.aumentarPotencia(15);
        System.out.println("[P02] Potencia C1: " + c1.getPotenciaActual() + " kW");

        double tiempoP03 = c1.tiempoEstimadoCarga(66);
        System.out.println("[P03] Tiempo estimado C1 (66 kWh): " + fmt(tiempoP03) + " horas");

        c1.aumentarPotencia(10);
        System.out.println("[P04] Potencia C1: " + c1.getPotenciaActual()
                + " kW (se esperaba rechazo)");

        c1.reducirPotencia(30);
        System.out.println("[P05] Potencia C1: " + c1.getPotenciaActual() + " kW");

        double tiempoP06 = c1.tiempoEstimadoCarga(50, 2, 15);
        System.out.println("[P06] Tiempo estimado C1 (50 kWh, 2 pausas de 15 min): "
                + fmt(tiempoP06) + " horas");

        double tiempoP07 = c1.tiempoEstimadoCarga(50, 40.0);
        System.out.println("[P07] Tiempo estimado C1 (50 kWh, potencia programada 40 kW): "
                + fmt(tiempoP07) + " horas");

        c1.aumentarPotencia();
        System.out.println("[P08] Potencia C1: " + c1.getPotenciaActual() + " kW");

        c1.aumentarPotencia(5, 3);
        System.out.println("[P09] Potencia C1: " + c1.getPotenciaActual() + " kW");

        c1.reducirPotencia(50);
        System.out.println("[P10] Potencia C1: " + c1.getPotenciaActual()
                + " kW (se esperaba rechazo)");

        c1.cortarCarga();
        System.out.println("[P11] Potencia C1: " + c1.getPotenciaActual() + " kW");

        double tiempoP12 = c1.tiempoEstimadoCarga(10);
        System.out.println("[P12] Tiempo estimado C1 (10 kWh, potencia en cero): "
                + fmt(tiempoP12) + "\n");

        /* Paso 3: operaciones sobre el resto de la flota. */
        System.out.println("--- Paso 3: operaciones sobre el resto de la flota ---");
        c2.setPotenciaActual(22);
        c3.setPotenciaActual(120);
        c4.aumentarPotencia(7.4);
        c5.aumentarPotencia(30);
        System.out.println("[P13] Potencia final C2: " + c2.getPotenciaActual() + " kW");
        System.out.println("[P13] Potencia final C3: " + c3.getPotenciaActual() + " kW");
        System.out.println("[P13] Potencia final C4: " + c4.getPotenciaActual() + " kW");
        System.out.println("[P13] Potencia final C5: " + c5.getPotenciaActual() + " kW\n");

        /* Paso 4: estadísticas, filtros y validaciones. */
        System.out.println("--- Paso 4: estadisticas y validaciones ---");
        int[] conteo = CargadorVE.contarPorTipo(flota);
        System.out.println("[P14] Conteo por tipo de cargador:");
        for (TipoCargador tipo : TipoCargador.values()) {
            System.out.println("       " + tipo + ": " + conteo[tipo.ordinal()]);
        }

        System.out.println("[P15] Promedio de potencia actual de la flota: "
                + fmt(CargadorVE.promedioPotencia(flota)) + " kW");

        CargadorVE mayor = CargadorVE.mayorPotencia(flota);
        System.out.println("[P16] Mayor potencia: " + mayor.getFabricante()
                + " con " + mayor.getPotenciaActual() + " kW");

        System.out.println("[P17] Excesos de potencia contratada (> "
                + CargadorVE.LIMITE_RED + " kW): "
                + CargadorVE.excesosDePotenciaContratada(flota));

        System.out.println("[P18] Filtros:");
        imprimirFiltro("TipoConector.TIPO_2",
                CargadorVE.filtrar(flota, TipoConector.TIPO_2));
        imprimirFiltro("TipoCargador.MURAL",
                CargadorVE.filtrar(flota, TipoCargador.MURAL));
        imprimirFiltro("Ubicacion.UNIVERSIDAD",
                CargadorVE.filtrar(flota, Ubicacion.UNIVERSIDAD));

        System.out.println("[P19] c5.mostrar(false) (valores por defecto del constructor reducido):");
        c5.mostrar(false);

        CargadorVE copia = new CargadorVE(c3);
        System.out.println("[P20] Copia de C3 -> fabricante: " + copia.getFabricante()
                + ", potencia actual: " + copia.getPotenciaActual()
                + " kW, tamano bitacora: " + copia.getBitacora().size()
                + " | Total cargadores: " + CargadorVE.getTotalCargadores());

        System.out.println("[P21] c1.mostrar(true):");
        c1.mostrar(true);

        System.out.println("[P22] contadorRegistros: " + CargadorVE.getContadorRegistros());

        CargadorVE[] arregloConNulo = {c1, null, c3};
        double promedioParcial = CargadorVE.promedioPotencia(arregloConNulo);
        int[] conteoNulo = CargadorVE.contarPorTipo(null);
        System.out.println("[P23] promedioPotencia({c1, null, c3}) = "
                + fmt(promedioParcial) + " kW (sin excepcion)");
        System.out.println("[P23] contarPorTipo(null) no lanzo excepcion, tamano del arreglo retornado: "
                + conteoNulo.length + "\n");

        /* Ruta individual: se ejecuta después de P23 y antes de C6. */
        int n = NUMERO_CEDULA;
        int r = n % 4;
        String nConDosDigitos = String.format(Locale.US, "%02d", n);
        System.out.println("--- Ruta individual: N = " + nConDosDigitos + ", r = " + r + " ---");
        ejecutarRuta(n, r, flota);
        System.out.println();

        /* Parte F: C6 se construye exclusivamente a partir de N. */
        int d1 = n / 10;
        int d2 = n % 10;
        String fabricanteC6 = "USC-" + n;
        int anioC6 = 2015 + d2;
        int voltajeC6 = n % 2 == 0 ? 220 : 400;
        TipoConector conectorC6 = TipoConector.values()[n % 5];
        TipoCargador tipoC6 = TipoCargador.values()[n % 6];
        int numeroConectoresC6 = d1 % 3 + 1;
        int puestosParqueoC6 = d2 % 4 + 1;
        double potenciaMaximaC6 = 20 + n;
        Ubicacion ubicacionC6 = Ubicacion.values()[n % 8];
        CargadorVE c6 = new CargadorVE(fabricanteC6, anioC6, voltajeC6,
                conectorC6, tipoC6, numeroConectoresC6, puestosParqueoC6,
                potenciaMaximaC6, ubicacionC6);

        System.out.println("--- Parte F: extension C6 ---");
        System.out.println("[X01] N = " + nConDosDigitos + ", d1 = " + d1
                + ", d2 = " + d2);
        System.out.println("[X01] c6.mostrar(false):");
        c6.mostrar(false);

        c6.setPotenciaActual(c6.getPotenciaMaxima() / 2.0);
        c6.aumentarPotencia(d2 + 5, d1 + 1);
        boolean huboRechazoC6 = false;
        for (CargadorVE.RegistroSesion registro : c6.getBitacora()) {
            if (!registro.isValido()) {
                huboRechazoC6 = true;
                break;
            }
        }
        System.out.println("[X02] Potencia final C6: " + c6.getPotenciaActual()
                + " kW; algum paso fue rechazado: " + huboRechazoC6);

        double tiempoC6 = c6.tiempoEstimadoCarga(n + 10);
        System.out.println("[X03] Tiempo estimado C6 (" + (n + 10) + " kWh): "
                + fmt(tiempoC6) + " horas");

        CargadorVE[] flotaExtendida = new CargadorVE[flota.length + 1];
        for (int i = 0; i < flota.length; i++) {
            flotaExtendida[i] = flota[i];
        }
        flotaExtendida[flota.length] = c6;
        System.out.println("[X04] flotaExtendida creada con " + flotaExtendida.length
                + " cargadores");

        System.out.println("[X05] Estadisticas sobre flotaExtendida ("
                + flotaExtendida.length + " cargadores):");
        int[] conteoExtendido = CargadorVE.contarPorTipo(flotaExtendida);
        for (TipoCargador tipo : TipoCargador.values()) {
            System.out.println("       " + tipo + ": " + conteoExtendido[tipo.ordinal()]);
        }
        System.out.println("       Promedio potencia: "
                + fmt(CargadorVE.promedioPotencia(flotaExtendida)) + " kW");
        CargadorVE mayorExtendido = CargadorVE.mayorPotencia(flotaExtendida);
        System.out.println("       Mayor potencia: " + mayorExtendido.getFabricante()
                + " con " + mayorExtendido.getPotenciaActual() + " kW");
        System.out.println("       Excesos de potencia contratada: "
                + CargadorVE.excesosDePotenciaContratada(flotaExtendida));

        System.out.println("[X06] Total cargadores creados: " + CargadorVE.getTotalCargadores());
        System.out.println("[X06] contadorRegistros: " + CargadorVE.getContadorRegistros());
        System.out.println("[X06] c6.mostrar(true):");
        c6.mostrar(true);
    }

    private static void imprimirFiltro(String nombre, CargadorVE[] resultado) {
        System.out.println("       " + nombre + " -> " + resultado.length + " cargador(es):");
        for (CargadorVE cargador : resultado) {
            System.out.println("         - " + cargador.getFabricante());
        }
    }

    private static void ejecutarRuta(int n, int r, CargadorVE[] flota) {
        if (r == 0) {
            int conectores = n % 3 + 1;
            CargadorVE[] resultado = CargadorVE.cargadoresPorConectores(flota, conectores);
            if (resultado.length == 0) {
                System.out.println("[R] No hay cargadores con " + conectores + " conector(es).");
            } else {
                System.out.println("[R] Cargadores con " + conectores + " conector(es): "
                        + resultado.length);
                for (CargadorVE cargador : resultado) {
                    System.out.println("     - " + cargador.getFabricante());
                }
            }
        } else if (r == 1) {
            TipoConector conector = TipoConector.values()[n % 5];
            double promedio = CargadorVE.promedioVoltajePorConector(flota, conector);
            if (promedio < 0.0) {
                System.out.println("[R] No hay cargadores con conector " + conector + ".");
            } else {
                System.out.println("[R] Promedio de voltaje para " + conector + ": "
                        + fmt(promedio) + " V");
            }
        } else if (r == 2) {
            TipoCargador[] masFrecuentes = CargadorVE.tipoMasFrecuente(flota);
            if (masFrecuentes.length == 0) {
                System.out.println("[R] No hay tipos para calcular una frecuencia.");
            } else {
                System.out.println("[R] Tipo(s) mas frecuente(s):");
                for (TipoCargador tipo : masFrecuentes) {
                    System.out.println("     - " + tipo);
                }
            }
        } else {
            System.out.println("[R] Registros validos sobre " + CargadorVE.LIMITE_RED
                    + " kW:");
            int cantidad = CargadorVE.sesionesSobreLimiteRed(flota);
            if (cantidad == 0) {
                System.out.println("     No hay registros validos sobre "
                        + CargadorVE.LIMITE_RED + " kW.");
            } else {
                System.out.println("     Total encontrados: " + cantidad);
            }
        }
    }

    private static String fmt(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }
}
