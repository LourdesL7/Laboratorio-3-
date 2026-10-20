import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SistemaTecniHogar {
    private final Map<String, OrdenServicio> ordenes;
    private final Map<String, Cuadrilla> cuadrillas;
    private final Map<String, Jornada> jornadas;
    private final List<Ingreso> ingresos;

    public SistemaTecniHogar() {
        ordenes = new LinkedHashMap<>();
        cuadrillas = new LinkedHashMap<>();
        jornadas = new LinkedHashMap<>();
        ingresos = new ArrayList<>();

        cargarDatosIniciales();
    }

    public String cotizar(OrdenServicio orden) {
        if (orden == null || orden.getCodigo() != null) {
            throw new IllegalArgumentException(
                "Debe cotizar un servicio válido sin registrar."
            );
        }

        return "Categoría: " + orden.obtenerCategoria()
                + "\nCosto: Q" + orden.calcularCosto().toPlainString()
                + "\nTiempo estimado: " + orden.estimarDiasAtencion()
                + " día(s) hábil(es).";
    }

    public void registrarOrden(String codigo, OrdenServicio orden) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El código de orden no puede estar vacío."
            );
        }

        codigo = codigo.trim();

        if (ordenes.containsKey(codigo)) {
            throw new IllegalArgumentException(
                "Ya existe una orden con ese código."
            );
        }

        if (orden == null
                || orden.getCodigo() != null
                || orden.getEstado() != EstadoOrden.PENDIENTE
                || orden.getVisitasRealizadas() != 0) {

            throw new IllegalArgumentException(
                "La orden debe ser nueva y no estar registrada."
            );
        }

        BigDecimal costo = orden.calcularCosto();

        if (costo == null || costo.signum() <= 0) {
            throw new IllegalArgumentException(
                "El costo del servicio no es válido."
            );
        }

        orden.asignarCodigo(codigo);
        ordenes.put(codigo, orden);

        ingresos.add(
            new Ingreso(orden, ConceptoIngreso.REGISTRO, costo)
        );
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new IllegalArgumentException(
                "La cuadrilla no es válida."
            );
        }

        if (cuadrillas.containsKey(cuadrilla.getCodigo())) {
            throw new IllegalArgumentException(
                "Ya existe una cuadrilla con ese código."
            );
        }

        cuadrillas.put(cuadrilla.getCodigo(), cuadrilla);
    }

    public void crearJornada(String codigoJornada,
                            String codigoCuadrilla) {

        if (codigoJornada == null || codigoJornada.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El código de jornada no puede estar vacío."
            );
        }

        codigoJornada = codigoJornada.trim();

        if (jornadas.containsKey(codigoJornada)) {
            throw new IllegalArgumentException(
                "Ya existe una jornada con ese código."
            );
        }

        Cuadrilla cuadrilla = cuadrillas.get(codigoCuadrilla);

        if (cuadrilla == null) {
            throw new IllegalArgumentException(
                "La cuadrilla indicada no existe."
            );
        }

        for (Jornada jornada : jornadas.values()) {
            if (jornada.getCuadrilla() == cuadrilla
                    && jornada.getEstado() != EstadoJornada.CERRADA) {

                throw new IllegalStateException(
                    "La cuadrilla ya tiene una jornada sin cerrar."
                );
            }
        }

        jornadas.put(
            codigoJornada,
            new Jornada(codigoJornada, cuadrilla)
        );
    }

    public void asignarOrden(String codigoJornada,
                             String codigoOrden) {

        Jornada jornada = jornadas.get(codigoJornada);
        OrdenServicio orden = ordenes.get(codigoOrden);

        if (jornada == null || orden == null) {
            throw new IllegalArgumentException(
                "La jornada o la orden indicada no existe."
            );
        }

        for (Jornada otra : jornadas.values()) {
            if (otra.getEstado() == EstadoJornada.CERRADA) {
                continue;
            }

            for (OrdenServicio asignada : otra.getOrdenes()) {
                if (asignada == orden
                        && !otra.getResultados().containsKey(codigoOrden)) {

                    throw new IllegalStateException(
                        "La orden ya está asignada a una jornada."
                    );
                }
            }
        }

        jornada.asignar(orden);
    }

    public void retirarOrden(String codigoJornada,
                             String codigoOrden) {

        Jornada jornada = jornadas.get(codigoJornada);

        if (jornada == null) {
            throw new IllegalArgumentException(
                "La jornada indicada no existe."
            );
        }

        jornada.retirar(codigoOrden);
    }

    public void iniciarJornada(String codigoJornada) {
        Jornada jornada = jornadas.get(codigoJornada);

        if (jornada == null) {
            throw new IllegalArgumentException(
                "La jornada indicada no existe."
            );
        }

        jornada.validarInicio();

        List<Ingreso> cargos = new ArrayList<>();

        for (OrdenServicio orden : jornada.getOrdenes()) {
            if (orden.getVisitasRealizadas() >= 1) {
                cargos.add(
                    new Ingreso(
                        orden,
                        ConceptoIngreso.VISITA_ADICIONAL,
                        new BigDecimal("40.00")
                    )
                );
            }
        }

        jornada.iniciar();
        ingresos.addAll(cargos);
    }

    public void registrarResultado(String codigoJornada,
                                    String codigoOrden,
                                    ResultadoVisita resultado) {

        Jornada jornada = jornadas.get(codigoJornada);

        if (jornada == null) {
            throw new IllegalArgumentException(
                "La jornada indicada no existe."
            );
        }

        jornada.registrarResultado(codigoOrden, resultado);
    }

    public List<OrdenServicio> consultarOrdenes() {
        return new ArrayList<>(ordenes.values());
    }

    public List<Cuadrilla> consultarCuadrillas() {
        return new ArrayList<>(cuadrillas.values());
    }

    public List<Jornada> consultarJornadas() {
        return new ArrayList<>(jornadas.values());
    }

    public String generarReporteOrdenes() {
        Map<String, Map<EstadoOrden, Integer>> conteos =
                new LinkedHashMap<>();

        Map<EstadoOrden, Integer> totalesEstado =
                new LinkedHashMap<>();

        for (EstadoOrden estado : EstadoOrden.values()) {
            totalesEstado.put(estado, 0);
        }

        for (OrdenServicio orden : ordenes.values()) {
            String categoria = orden.obtenerCategoria();

            if (!conteos.containsKey(categoria)) {
                Map<EstadoOrden, Integer> estados =
                        new LinkedHashMap<>();

                for (EstadoOrden estado : EstadoOrden.values()) {
                    estados.put(estado, 0);
                }

                conteos.put(categoria, estados);
            }

            Map<EstadoOrden, Integer> estados =
                    conteos.get(categoria);

            EstadoOrden estado = orden.getEstado();

            estados.put(estado, estados.get(estado) + 1);

            totalesEstado.put(
                estado,
                totalesEstado.get(estado) + 1
            );
        }

        StringBuilder reporte = new StringBuilder(
            "ÓRDENES POR CATEGORÍA Y ESTADO\n"
        );

        for (Map.Entry<String, Map<EstadoOrden, Integer>> fila
                : conteos.entrySet()) {

            reporte.append(fila.getKey()).append(":\n");

            int totalCategoria = 0;

            for (Map.Entry<EstadoOrden, Integer> dato
                    : fila.getValue().entrySet()) {

                reporte.append("  ")
                        .append(dato.getKey())
                        .append(": ")
                        .append(dato.getValue())
                        .append('\n');

                totalCategoria += dato.getValue();
            }

            reporte.append("  Total de categoría: ")
                    .append(totalCategoria)
                    .append('\n');
        }

        reporte.append("TOTALES POR ESTADO\n");

        for (Map.Entry<EstadoOrden, Integer> dato
                : totalesEstado.entrySet()) {

            reporte.append(dato.getKey())
                    .append(": ")
                    .append(dato.getValue())
                    .append('\n');
        }

        reporte.append("Total de órdenes: ")
                .append(ordenes.size());

        return reporte.toString();
    }

    public String generarReporteIngresos() {
        BigDecimal registros = new BigDecimal("0.00");
        BigDecimal adicionales = new BigDecimal("0.00");

        Map<String, BigDecimal> porCategoria =
                new LinkedHashMap<>();

        for (OrdenServicio orden : ordenes.values()) {
            porCategoria.put(
                orden.obtenerCategoria(),
                new BigDecimal("0.00")
            );
        }

        for (Ingreso ingreso : ingresos) {
            if (ingreso.getConcepto() == ConceptoIngreso.REGISTRO) {
                registros = registros.add(ingreso.getMonto());
            } else {
                adicionales = adicionales.add(ingreso.getMonto());
            }

            String categoria = ingreso.getOrden().obtenerCategoria();

            porCategoria.put(
                categoria,
                porCategoria.get(categoria).add(ingreso.getMonto())
            );
        }

        StringBuilder reporte = new StringBuilder("INGRESOS\n");

        reporte.append("Por registro: Q")
                .append(registros.toPlainString())
                .append('\n');

        reporte.append("Por visitas adicionales: Q")
                .append(adicionales.toPlainString())
                .append('\n');

        reporte.append("Total: Q")
                .append(registros.add(adicionales).toPlainString())
                .append('\n');

        reporte.append("POR CATEGORÍA\n");

        for (Map.Entry<String, BigDecimal> dato
                : porCategoria.entrySet()) {

            reporte.append(dato.getKey())
                    .append(": Q")
                    .append(dato.getValue().toPlainString())
                    .append('\n');
        }

        return reporte.toString();
    }

    public String generarReporteJornadas() {
        StringBuilder reporte = new StringBuilder(
            "JORNADAS SIN CERRAR\n"
        );

        boolean hayJornadas = false;

        for (Jornada jornada : jornadas.values()) {
            if (jornada.getEstado() != EstadoJornada.CERRADA) {
                hayJornadas = true;

                reporte.append("Jornada: ")
                        .append(jornada.getCodigo())
                        .append(" | Estado: ")
                        .append(jornada.getEstado())
                        .append(" | Cuadrilla: ")
                        .append(jornada.getCuadrilla().getCodigo())
                        .append(" | Horas asignadas: ")
                        .append(jornada.calcularHorasAsignadas())
                        .append(" | Capacidad disponible: ")
                        .append(jornada.calcularCapacidadDisponible())
                        .append('\n');

                for (OrdenServicio orden : jornada.getOrdenes()) {
                    ResultadoVisita resultado =
                            jornada.getResultados().get(orden.getCodigo());

                    reporte.append("  Orden: ")
                            .append(orden.getCodigo())
                            .append(" | Resultado: ")
                            .append(
                                resultado == null
                                    ? "Sin registrar"
                                    : resultado
                            )
                            .append('\n');
                }
            }
        }

        if (!hayJornadas) {
            reporte.append("No hay jornadas sin cerrar.");
        }

        return reporte.toString();
    }

    private void cargarDatosIniciales() {
        OrdenServicio[] iniciales = {
            new Diagnostico(
                "Ana López",
                "Zona 1, Ciudad de Guatemala",
                1.0,
                Zona.METROPOLITANA
            ),

            new Diagnostico(
                "Luis Pérez",
                "Villa Canales",
                1.5,
                Zona.PERIFERICA
            ),

            new Instalacion(
                "María Ruiz",
                "Zona 7, Ciudad de Guatemala",
                2.5,
                Zona.METROPOLITANA,
                5
            ),

            new Instalacion(
                "Carlos Díaz",
                "San José Pinula",
                3.2,
                Zona.PERIFERICA,
                2
            ),

            new InstalacionElectricaCertificada(
                "Sofía García",
                "Zona 10, Ciudad de Guatemala",
                2.5,
                Zona.METROPOLITANA,
                5,
                new BigDecimal("2000.00")
            ),

            new InstalacionElectricaCertificada(
                "Pedro Gómez",
                "Santa Catarina Pinula",
                1.5,
                Zona.PERIFERICA,
                2,
                new BigDecimal("1000.00")
            ),

            new MantenimientoAireAcondicionado(
                "Elena Castro",
                "Zona 12, Ciudad de Guatemala",
                1.5,
                Zona.METROPOLITANA,
                9000
            ),

            new MantenimientoAireAcondicionado(
                "José Morales",
                "Zona 14, Ciudad de Guatemala",
                2.0,
                Zona.METROPOLITANA,
                60000
            )
        };

        String[] codigos = {
            "D01", "D02",
            "I01", "I02",
            "E01", "E02",
            "M01", "M02"
        };

        for (int i = 0; i < iniciales.length; i++) {
            iniciales[i].asignarCodigo(codigos[i]);
            ordenes.put(codigos[i], iniciales[i]);
        }

        registrarCuadrilla(
            new Cuadrilla("C01", 8.0, true)
        );

        registrarCuadrilla(
            new Cuadrilla("C02", 6.0, false)
        );

        registrarCuadrilla(
            new Cuadrilla("C03", 4.0, false)
        );
    }
}