package co.edu.usc.voltacali;

import java.util.Vector;

/**
 * Modelo de un cargador de vehiculos electricos de VoltaCali S.A.S.
 *
 * La clase mantiene sus datos encapsulados y una bitacora propia de cambios de
 * potencia. Los registros inválidos se conservan para poder auditar los
 * intentos que no pudieron aplicarse.
 */
public class CargadorVE {

    /* Enums anidados: el orden de sus constantes forma parte del contrato. */
    public enum TipoConector {
        TIPO_1, TIPO_2, CCS2, CHADEMO, GBT
    }

    public enum TipoCargador {
        MURAL, PEDESTAL, RAPIDO_DC, ULTRARRAPIDO, PORTATIL, BIDIRECCIONAL_V2G
    }

    public enum Ubicacion {
        CENTRO_COMERCIAL, UNIVERSIDAD, ESTACION_SERVICIO, PARQUEADERO_PUBLICO,
        RESIDENCIAL, HOTEL, TERMINAL, FLOTA_CORPORATIVA
    }

    /* Parte A: los diez atributos del modelo. */
    private String fabricante;
    private int anioInstalacion;
    private int voltajeNominal;
    private TipoConector tipoConector;
    private TipoCargador tipoCargador;
    private int numeroConectores;
    private int puestosParqueo;
    private double potenciaMaxima;
    private Ubicacion ubicacion;
    private double potenciaActual;

    /* Parte D-c: historial propio del cargador. */
    private Vector<RegistroSesion> bitacora;

    /* Parte D-e: miembros compartidos por todos los cargadores. */
    private static int totalCargadores = 0;
    private static int contadorRegistros = 0;
    public static final double LIMITE_RED = 50.0;
    public static final double INCREMENTO_DEFECTO = 5.0;

    /**
     * Constructor completo. La potencia actual siempre comienza en cero.
     */
    public CargadorVE(String fabricante, int anioInstalacion, int voltajeNominal,
                      TipoConector tipoConector, TipoCargador tipoCargador,
                      int numeroConectores, int puestosParqueo,
                      double potenciaMaxima, Ubicacion ubicacion) {
        this.fabricante = fabricante;
        this.anioInstalacion = anioInstalacion;
        this.voltajeNominal = voltajeNominal;
        this.tipoConector = tipoConector;
        this.tipoCargador = tipoCargador;
        this.numeroConectores = numeroConectores;
        this.puestosParqueo = puestosParqueo;
        this.potenciaMaxima = potenciaMaxima;
        this.ubicacion = ubicacion;
        this.potenciaActual = 0.0;
        this.bitacora = new Vector<RegistroSesion>();
        totalCargadores++;
    }

    /**
     * Constructor reducido para el caso de prueba.
     */
    public CargadorVE(String fabricante, int anioInstalacion, double potenciaMaxima) {
        this(fabricante, anioInstalacion, 220, TipoConector.TIPO_2,
                TipoCargador.PEDESTAL, 1, 1, potenciaMaxima,
                Ubicacion.PARQUEADERO_PUBLICO);
    }

    /**
     * Constructor copia: copia las características técnicas, no la potencia ni
     * la bitácora, y cuenta como un nuevo objeto para totalCargadores.
     */
    public CargadorVE(CargadorVE otro) {
        this(otro.fabricante, otro.anioInstalacion, otro.voltajeNominal,
                otro.tipoConector, otro.tipoCargador, otro.numeroConectores,
                otro.puestosParqueo, otro.potenciaMaxima, otro.ubicacion);
    }

    /* Getters y setters del modelo. */
    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public int getAnioInstalacion() {
        return anioInstalacion;
    }

    public void setAnioInstalacion(int anioInstalacion) {
        this.anioInstalacion = anioInstalacion;
    }

    public int getVoltajeNominal() {
        return voltajeNominal;
    }

    public void setVoltajeNominal(int voltajeNominal) {
        this.voltajeNominal = voltajeNominal;
    }

    public TipoConector getTipoConector() {
        return tipoConector;
    }

    public void setTipoConector(TipoConector tipoConector) {
        this.tipoConector = tipoConector;
    }

    public TipoCargador getTipoCargador() {
        return tipoCargador;
    }

    public void setTipoCargador(TipoCargador tipoCargador) {
        this.tipoCargador = tipoCargador;
    }

    public int getNumeroConectores() {
        return numeroConectores;
    }

    public void setNumeroConectores(int numeroConectores) {
        this.numeroConectores = numeroConectores;
    }

    public int getPuestosParqueo() {
        return puestosParqueo;
    }

    public void setPuestosParqueo(int puestosParqueo) {
        this.puestosParqueo = puestosParqueo;
    }

    public double getPotenciaMaxima() {
        return potenciaMaxima;
    }

    public void setPotenciaMaxima(double potenciaMaxima) {
        this.potenciaMaxima = potenciaMaxima;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double getPotenciaActual() {
        return potenciaActual;
    }

    /**
     * Fija la potencia actual solo si está dentro del rango permitido. Un
     * intento rechazado no modifica el estado, pero sí queda en la bitácora.
     */
    public void setPotenciaActual(double potenciaActual) {
        if (!esPotenciaValida(potenciaActual)) {
            System.out.println("  >> RECHAZADO: " + fabricante
                    + " no puede fijar potencia en " + potenciaActual
                    + " kW (rango valido: 0 a " + this.potenciaMaxima + " kW).");
            registrar("setPotenciaActual(" + potenciaActual + ")", false);
            return;
        }
        this.potenciaActual = potenciaActual;
        registrar("setPotenciaActual(" + potenciaActual + ")", true);
    }

    private boolean esPotenciaValida(double potencia) {
        return !Double.isNaN(potencia) && !Double.isInfinite(potencia)
                && potencia >= 0.0 && potencia <= potenciaMaxima;
    }

    private void registrar(String evento, boolean valido) {
        bitacora.add(new RegistroSesion(evento, valido));
    }

    public Vector<RegistroSesion> getBitacora() {
        return bitacora;
    }

    /* Parte B y Parte C: cambios de potencia y sus sobrecargas. */
    private boolean aplicarAumento(double incremento) {
        double nueva = potenciaActual + incremento;
        if (!esPotenciaValida(nueva)) {
            System.out.println("  >> RECHAZADO: " + fabricante + " no puede aumentar "
                    + incremento + " kW (resultado " + nueva
                    + " kW fuera de rango 0 a " + potenciaMaxima + " kW).");
            registrar("aumentarPotencia(" + incremento + ")", false);
            return false;
        }
        potenciaActual = nueva;
        registrar("aumentarPotencia(" + incremento + ")", true);
        return true;
    }

    public void aumentarPotencia() {
        aplicarAumento(INCREMENTO_DEFECTO);
    }

    public void aumentarPotencia(double incremento) {
        aplicarAumento(incremento);
    }

    public void aumentarPotencia(double incremento, int veces) {
        for (int i = 0; i < veces; i++) {
            if (!aplicarAumento(incremento)) {
                break;
            }
        }
    }

    private boolean aplicarReduccion(double reduccion) {
        double nueva = potenciaActual - reduccion;
        if (!esPotenciaValida(nueva)) {
            System.out.println("  >> RECHAZADO: " + fabricante + " no puede reducir "
                    + reduccion + " kW (resultado " + nueva
                    + " kW fuera de rango 0 a " + potenciaMaxima + " kW).");
            registrar("reducirPotencia(" + reduccion + ")", false);
            return false;
        }
        potenciaActual = nueva;
        registrar("reducirPotencia(" + reduccion + ")", true);
        return true;
    }

    public void reducirPotencia(double reduccion) {
        aplicarReduccion(reduccion);
    }

    public void cortarCarga() {
        potenciaActual = 0.0;
        registrar("cortarCarga()", true);
    }

    public double tiempoEstimadoCarga(double energiaKWh) {
        if (potenciaActual <= 0.0) {
            System.out.println("  >> No se puede estimar tiempo de carga de " + fabricante
                    + ": la potencia actual es 0 kW.");
            return -1.0;
        }
        return energiaKWh / potenciaActual;
    }

    public double tiempoEstimadoCarga(double energiaKWh, double potenciaProgramada) {
        if (potenciaProgramada == 0.0) {
            System.out.println("  >> No se puede estimar tiempo de carga de " + fabricante
                    + ": la potencia programada es 0 kW.");
            return -1.0;
        }
        if (potenciaProgramada < 0.0 || Double.isNaN(potenciaProgramada)
                || Double.isInfinite(potenciaProgramada)) {
            System.out.println("  >> No se puede estimar tiempo de carga de " + fabricante
                    + ": la potencia programada debe ser mayor que 0 kW.");
            return -1.0;
        }
        return energiaKWh / potenciaProgramada;
    }

    public double tiempoEstimadoCarga(double energiaKWh, int pausas,
                                      double minutosPorPausa) {
        if (potenciaActual <= 0.0) {
            System.out.println("  >> No se puede estimar tiempo de carga de " + fabricante
                    + ": la potencia actual es 0 kW.");
            return -1.0;
        }
        return energiaKWh / potenciaActual + (pausas * minutosPorPausa) / 60.0;
    }

    /* Salida del modelo. */
    public void mostrar() {
        System.out.println("  Fabricante        : " + fabricante);
        System.out.println("  Anio instalacion  : " + anioInstalacion);
        System.out.println("  Voltaje nominal   : " + voltajeNominal + " V");
        System.out.println("  Tipo conector     : " + tipoConector);
        System.out.println("  Tipo cargador     : " + tipoCargador);
        System.out.println("  Num. conectores   : " + numeroConectores);
        System.out.println("  Puestos parqueo   : " + puestosParqueo);
        System.out.println("  Potencia maxima   : " + potenciaMaxima + " kW");
        System.out.println("  Ubicacion         : " + ubicacion);
        System.out.println("  Potencia actual   : " + potenciaActual + " kW");
    }

    public void mostrar(boolean detallado) {
        mostrar();
        if (detallado) {
            System.out.println("  Bitacora (" + bitacora.size() + " registros):");
            for (RegistroSesion registro : bitacora) {
                System.out.println("    " + registro.describir());
            }
        }
    }

    /**
     * Clase interna no estática. El constructor solo recibe el evento y su
     * validez; los datos del cargador externo se capturan implícitamente.
     */
    public class RegistroSesion {
        private int numero;
        private String evento;
        private boolean valido;
        private String fabricanteCap;
        private int anioInstalacionCap;
        private double potenciaCap;

        public RegistroSesion(String evento, boolean valido) {
            contadorRegistros++;
            this.numero = contadorRegistros;
            this.evento = evento;
            this.valido = valido;
            this.fabricanteCap = fabricante;
            this.anioInstalacionCap = anioInstalacion;
            this.potenciaCap = potenciaActual;
        }

        public int getNumero() {
            return numero;
        }

        public boolean isValido() {
            return valido;
        }

        public String getEvento() {
            return evento;
        }

        public double getPotenciaCap() {
            return potenciaCap;
        }

        public String describir() {
            return "#" + numero + " [" + (valido ? "VALIDO" : "RECHAZADO") + "] "
                    + fabricanteCap + " (" + anioInstalacionCap + ") - " + evento
                    + " -> potencia registrada: " + potenciaCap + " kW";
        }
    }

    /* Parte D-d: métodos estáticos que trabajan con arreglos. */
    public static int[] contarPorTipo(CargadorVE[] arreglo) {
        int[] conteo = new int[TipoCargador.values().length];
        if (arreglo == null) {
            return conteo;
        }
        for (CargadorVE cargador : arreglo) {
            if (cargador != null && cargador.tipoCargador != null) {
                conteo[cargador.tipoCargador.ordinal()]++;
            }
        }
        return conteo;
    }

    public static CargadorVE mayorPotencia(CargadorVE[] arreglo) {
        if (arreglo == null) {
            return null;
        }
        CargadorVE mayor = null;
        for (CargadorVE cargador : arreglo) {
            if (cargador != null
                    && (mayor == null || cargador.potenciaActual > mayor.potenciaActual)) {
                mayor = cargador;
            }
        }
        return mayor;
    }

    public static double promedioPotencia(CargadorVE[] arreglo) {
        if (arreglo == null) {
            return 0.0;
        }
        double suma = 0.0;
        int cantidad = 0;
        for (CargadorVE cargador : arreglo) {
            if (cargador != null) {
                suma += cargador.potenciaActual;
                cantidad++;
            }
        }
        return cantidad == 0 ? 0.0 : suma / cantidad;
    }

    public static int excesosDePotenciaContratada(CargadorVE[] arreglo) {
        if (arreglo == null) {
            return 0;
        }
        int excessos = 0;
        for (CargadorVE cargador : arreglo) {
            if (cargador == null) {
                continue;
            }
            for (RegistroSesion registro : cargador.bitacora) {
                if (registro.isValido() && registro.getPotenciaCap() > LIMITE_RED) {
                    excessos++;
                }
            }
        }
        return excessos;
    }

    public static CargadorVE[] filtrar(CargadorVE[] arreglo, TipoConector conector) {
        if (arreglo == null) {
            return new CargadorVE[0];
        }
        int cantidad = 0;
        for (CargadorVE cargador : arreglo) {
            if (cargador != null && cargador.tipoConector == conector) {
                cantidad++;
            }
        }
        CargadorVE[] resultado = new CargadorVE[cantidad];
        int indice = 0;
        for (CargadorVE cargador : arreglo) {
            if (cargador != null && cargador.tipoConector == conector) {
                resultado[indice++] = cargador;
            }
        }
        return resultado;
    }

    public static CargadorVE[] filtrar(CargadorVE[] arreglo, TipoCargador tipo) {
        if (arreglo == null) {
            return new CargadorVE[0];
        }
        int cantidad = 0;
        for (CargadorVE cargador : arreglo) {
            if (cargador != null && cargador.tipoCargador == tipo) {
                cantidad++;
            }
        }
        CargadorVE[] resultado = new CargadorVE[cantidad];
        int indice = 0;
        for (CargadorVE cargador : arreglo) {
            if (cargador != null && cargador.tipoCargador == tipo) {
                resultado[indice++] = cargador;
            }
        }
        return resultado;
    }

    public static CargadorVE[] filtrar(CargadorVE[] arreglo, Ubicacion ubicacion) {
        if (arreglo == null) {
            return new CargadorVE[0];
        }
        int cantidad = 0;
        for (CargadorVE cargador : arreglo) {
            if (cargador != null && cargador.ubicacion == ubicacion) {
                cantidad++;
            }
        }
        CargadorVE[] resultado = new CargadorVE[cantidad];
        int indice = 0;
        for (CargadorVE cargador : arreglo) {
            if (cargador != null && cargador.ubicacion == ubicacion) {
                resultado[indice++] = cargador;
            }
        }
        return resultado;
    }

    public static int getTotalCargadores() {
        return totalCargadores;
    }

    public static int getContadorRegistros() {
        return contadorRegistros;
    }

    /* Ruta r = 0. */
    public static CargadorVE[] cargadoresPorConectores(CargadorVE[] flota,
                                                          int conectores) {
        if (flota == null) {
            return new CargadorVE[0];
        }
        int cantidad = 0;
        for (CargadorVE cargador : flota) {
            if (cargador != null && cargador.numeroConectores == conectores) {
                cantidad++;
            }
        }
        CargadorVE[] resultado = new CargadorVE[cantidad];
        int indice = 0;
        for (CargadorVE cargador : flota) {
            if (cargador != null && cargador.numeroConectores == conectores) {
                resultado[indice++] = cargador;
            }
        }
        return resultado;
    }

    /* Ruta r = 1: devuelve -1.0 cuando no hay elementos para el conector. */
    public static double promedioVoltajePorConector(CargadorVE[] flota,
                                                       TipoConector conector) {
        if (flota == null || conector == null) {
            return -1.0;
        }
        double suma = 0.0;
        int cantidad = 0;
        for (CargadorVE cargador : flota) {
            if (cargador != null && cargador.tipoConector == conector) {
                suma += cargador.voltajeNominal;
                cantidad++;
            }
        }
        return cantidad == 0 ? -1.0 : suma / cantidad;
    }

    /* Ruta r = 2: retorna todos los tipos que empatan en el máximo. */
    public static TipoCargador[] tipoMasFrecuente(CargadorVE[] flota) {
        if (flota == null) {
            return new TipoCargador[0];
        }
        int[] conteo = contarPorTipo(flota);
        int maximo = -1;
        int total = 0;
        for (int valor : conteo) {
            if (valor > maximo) {
                maximo = valor;
            }
            total += valor;
        }
        if (total == 0) {
            return new TipoCargador[0];
        }
        int cantidad = 0;
        for (int valor : conteo) {
            if (valor == maximo) {
                cantidad++;
            }
        }
        TipoCargador[] resultado = new TipoCargador[cantidad];
        int indice = 0;
        TipoCargador[] tipos = TipoCargador.values();
        for (int i = 0; i < tipos.length; i++) {
            if (conteo[i] == maximo) {
                resultado[indice++] = tipos[i];
            }
        }
        return resultado;
    }

    /* Ruta r = 3: muestra los registros válidos que superan el límite y
     * retorna cuántos encontró. */
    public static int sesionesSobreLimiteRed(CargadorVE[] flota) {
        if (flota == null) {
            return 0;
        }
        int cantidad = 0;
        for (CargadorVE cargador : flota) {
            if (cargador == null) {
                continue;
            }
            for (RegistroSesion registro : cargador.bitacora) {
                if (registro.isValido() && registro.getPotenciaCap() > LIMITE_RED) {
                    System.out.println("     " + registro.describir());
                    cantidad++;
                }
            }
        }
        return cantidad;
    }

    /** Alias auxiliar para consultar la cantidad sin imprimir registros. */
    public static int cantidadSesionesSobreLimiteRed(CargadorVE[] flota) {
        if (flota == null) {
            return 0;
        }
        int cantidad = 0;
        for (CargadorVE cargador : flota) {
            if (cargador == null) {
                continue;
            }
            for (RegistroSesion registro : cargador.bitacora) {
                if (registro.isValido() && registro.getPotenciaCap() > LIMITE_RED) {
                    cantidad++;
                }
            }
        }
        return cantidad;
    }
}
