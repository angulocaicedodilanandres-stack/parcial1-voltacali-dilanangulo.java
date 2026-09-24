# Parcial 1 — VoltaCali S.A.S.

**Estudiante:** Dilan Andres Angulo Caicedo
**Código:** APO22
**N (dos últimos dígitos de la cédula):** `88`

Prototipo de consola en Java para modelar y analizar cargadores de vehículos
eléctricos. El proyecto no usa base de datos ni interfaz gráfica: todos los
resultados se calculan en memoria mediante clases, arreglos y `Vector`.

## Ruta individual

- `N = 88`
- `r = N mod 4 = 88 mod 4 = 0`
- **Ruta asignada:** `r = 0`
- Método utilizado: `CargadorVE.cargadoresPorConectores(CargadorVE[] flota, int conectores)`
- Conectores buscados: `N mod 3 + 1 = 2`
- Resultado del caso: `ABB` y `Delta`, ambos con dos conectores.

El código también contiene las implementaciones de las otras tres rutas para
que el prototipo pueda reutilizarse si se cambia `NUMERO_CEDULA` en
`src/main/java/co/edu/usc/voltacali/App.java`.

## Atributos calculados de C6 (Parte F)

Con `N = 88`, `d1 = 8` y `d2 = 8`:

| Atributo | Regla | Valor calculado |
|---|---|---|
| `fabricante` | `"USC-" + N` | `USC-88` |
| `anioInstalacion` | `2015 + d2` | `2023` |
| `voltajeNominal` | `220` si `N` es par; `400` si es impar | `220 V` (N par) |
| `tipoConector` | `TipoConector.values()[N % 5]` | `CHADEMO` (índice 3) |
| `tipoCargador` | `TipoCargador.values()[N % 6]` | `PORTATIL` (índice 4) |
| `numeroConectores` | `d1 % 3 + 1` | `3` |
| `puestosParqueo` | `d2 % 4 + 1` | `1` |
| `potenciaMaxima` | `20 + N` | `108.0 kW` |
| `ubicacion` | `Ubicacion.values()[N % 8]` | `CENTRO_COMERCIAL` (índice 0) |

Los valores anteriores se calculan en `App.main`; no están escritos a mano en
la construcción de C6. En X02, C6 parte de `54.0 kW` e intenta aplicar nueve
pasos de `13.0 kW`: cinco se aceptan y el sexto se rechaza porque superaría
`108.0 kW`; por eso termina en `106.0 kW`.

## Estructura

```text
parcial1-voltacali-dilanangulo.java/
├── pom.xml
├── .gitignore
├── README.md
├── src/
│   ├── main/java/co/edu/usc/voltacali/
│   │   ├── App.java
│   │   └── CargadorVE.java
│   └── test/java/co/edu/usc/voltacali/
│       └── AppTest.java
└── docs/
    └── captura.png
```

## Compilar y ejecutar

Desde la raíz del repositorio, con Maven y Java 8 configurado:

```bash
mvn clean compile
java -cp target/classes co.edu.usc.voltacali.App
```

Para ejecutar también la prueba generada por el archetype:

```bash
mvn test
```

La carpeta `target/` queda excluida por `.gitignore`; los `.class` no deben
subirse al repositorio.

## Qué incluye la implementación

- **Parte A:** diez atributos privados, getters/setters y validación de
  `setPotenciaActual`.
- **Parte B:** aumento, reducción, corte, tiempo estimado y salida del modelo.
- **Parte C:** constructores, métodos de potencia, tiempos, filtros y
  `mostrar(boolean)` sobrecargados.
- **Parte D:** enums anidados en el orden solicitado, clase interna no estática
  `RegistroSesion`, bitácora `Vector`, métodos estáticos y manejo de arreglos
  nulos o con posiciones nulas.
- **Parte E:** pasos P01–P23 en el orden indicado, estadísticas, filtros,
  validación de nulos y salida ordenada.
- **Parte F:** C6, X01–X06, ruta individual `[R]` y valores calculados desde N.
- **Prueba:** `mvn clean compile` y `mvn test` completados correctamente.
- **Captura:** [`docs/captura.png`](docs/captura.png).
