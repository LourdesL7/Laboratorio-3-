import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Jornada {
    private final String codigo;
    private final Cuadrilla cuadrilla;
    private EstadoJornada estado;
    private final List<OrdenServicio> ordenes;
    private final Map<String, ResultadoVisita> resultados;

    public Jornada(String codigo, Cuadrilla cuadrilla) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El código de jornada no puede estar vacío."
            );
        }

        if (cuadrilla == null) {
            throw new IllegalArgumentException(
                "La cuadrilla no existe."
            );
        }

        this.codigo = codigo.trim();
        this.cuadrilla = cuadrilla;
        this.estado = EstadoJornada.EN_PREPARACION;
        this.ordenes = new ArrayList<>();
        this.resultados = new LinkedHashMap<>();
    }

    public void asignar(OrdenServicio orden) {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException(
                "Solo se asignan órdenes durante la preparación."
            );
        }

        if (orden == null || orden.getCodigo() == null) {
            throw new IllegalArgumentException(
                "La orden debe estar registrada."
            );
        }

        if (orden.getEstado() != EstadoOrden.PENDIENTE
                || orden.getVisitasRealizadas() >= orden.obtenerMaximoVisitas()) {

            throw new IllegalStateException(
                "La orden debe estar pendiente y tener visitas disponibles."
            );
        }

        for (OrdenServicio existente : ordenes) {
            if (existente.getCodigo().equals(orden.getCodigo())) {
                throw new IllegalStateException(
                    "La orden ya pertenece a esta jornada."
                );
            }
        }

        if (!orden.esCompatibleCon(cuadrilla)) {
            throw new IllegalArgumentException(
                "La orden no es compatible con la cuadrilla."
            );
        }

        BigDecimal total = BigDecimal.ZERO;

        for (OrdenServicio existente : ordenes) {
            total = total.add(
                BigDecimal.valueOf(existente.getHorasEstimadas())
            );
        }

        total = total.add(
            BigDecimal.valueOf(orden.getHorasEstimadas())
        );

        if (total.compareTo(
                BigDecimal.valueOf(cuadrilla.getCapacidadHoras())) > 0) {

            throw new IllegalArgumentException(
                "La asignación excede la capacidad de la cuadrilla."
            );
        }

        orden.marcarAsignada();
        ordenes.add(orden);
    }

    public void retirar(String codigoOrden) {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException(
                "Solo se retiran órdenes durante la preparación."
            );
        }

        OrdenServicio encontrada = null;

        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equals(codigoOrden)) {
                encontrada = orden;
                break;
            }
        }

        if (encontrada == null) {
            throw new IllegalArgumentException(
                "La orden no pertenece a esta jornada."
            );
        }

        encontrada.retirarAsignacion();
        ordenes.remove(encontrada);
    }

    public void validarInicio() {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException(
                "La jornada ya inició o está cerrada."
            );
        }

        if (ordenes.isEmpty()) {
            throw new IllegalStateException(
                "No se puede iniciar una jornada vacía."
            );
        }

        for (OrdenServicio orden : ordenes) {
            if (orden.getEstado() != EstadoOrden.ASIGNADA
                    || orden.getVisitasRealizadas() >= orden.obtenerMaximoVisitas()
                    || !orden.esCompatibleCon(cuadrilla)) {

                throw new IllegalStateException(
                    "Una orden no permite iniciar la jornada."
                );
            }
        }
    }

    public void iniciar() {
        validarInicio();

        for (OrdenServicio orden : ordenes) {
            orden.iniciarVisita();
        }

        estado = EstadoJornada.INICIADA;
    }

    public void registrarResultado(String codigoOrden,
                                   ResultadoVisita resultado) {

        if (estado != EstadoJornada.INICIADA) {
            throw new IllegalStateException(
                "La jornada debe estar iniciada."
            );
        }

        if (resultado == null) {
            throw new IllegalArgumentException(
                "Debe indicar un resultado válido."
            );
        }

        if (resultados.containsKey(codigoOrden)) {
            throw new IllegalStateException(
                "Esta visita ya tiene un resultado registrado."
            );
        }

        OrdenServicio encontrada = null;

        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equals(codigoOrden)) {
                encontrada = orden;
                break;
            }
        }

        if (encontrada == null) {
            throw new IllegalArgumentException(
                "La orden no pertenece a esta jornada."
            );
        }

        encontrada.registrarResultado(resultado);
        resultados.put(codigoOrden, resultado);

        if (resultados.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
        }
    }

    public double calcularHorasAsignadas() {
        BigDecimal horas = BigDecimal.ZERO;

        for (OrdenServicio orden : ordenes) {
            horas = horas.add(
                BigDecimal.valueOf(orden.getHorasEstimadas())
            );
        }

        return horas.doubleValue();
    }

    public double calcularCapacidadDisponible() {
        BigDecimal disponible = BigDecimal.valueOf(
            cuadrilla.getCapacidadHoras()
        );

        for (OrdenServicio orden : ordenes) {
            disponible = disponible.subtract(
                BigDecimal.valueOf(orden.getHorasEstimadas())
            );
        }

        return disponible.doubleValue();
    }

    public String getCodigo() {
        return codigo;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public List<OrdenServicio> getOrdenes() {
        return new ArrayList<>(ordenes);
    }

    public Map<String, ResultadoVisita> getResultados() {
        return new LinkedHashMap<>(resultados);
    }
}