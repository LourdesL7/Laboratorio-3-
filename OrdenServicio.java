import java.math.BigDecimal;

public abstract class OrdenServicio {
    private String codigo;
    private final String cliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int visitasRealizadas;

    protected OrdenServicio(String cliente, String direccion,
                            double horasEstimadas, Zona zona) {

        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El cliente no puede estar vacío."
            );
        }

        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "La dirección no puede estar vacía."
            );
        }

        if (!Double.isFinite(horasEstimadas) || horasEstimadas <= 0) {
            throw new IllegalArgumentException(
                "Las horas deben ser positivas y finitas."
            );
        }

        if (zona == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar una zona válida."
            );
        }

        this.cliente = cliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
        this.codigo = null;
        this.estado = EstadoOrden.PENDIENTE;
        this.visitasRealizadas = 0;
    }

    public abstract BigDecimal calcularCosto();

    public abstract int estimarDiasAtencion();

    public abstract int obtenerMaximoVisitas();

    public abstract String obtenerCategoria();

    protected BigDecimal calcularTarifaBase() {
        if (zona == Zona.METROPOLITANA) {
            return new BigDecimal("100.00");
        }

        return new BigDecimal("175.00");
    }

    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return cuadrilla != null;
    }

    public void asignarCodigo(String codigo) {
        if (this.codigo != null) {
            throw new IllegalStateException(
                "La orden ya tiene un código."
            );
        }

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El código no puede estar vacío."
            );
        }

        this.codigo = codigo.trim();
    }

    public void marcarAsignada() {
        if (codigo == null
                || estado != EstadoOrden.PENDIENTE
                || visitasRealizadas >= obtenerMaximoVisitas()) {

            throw new IllegalStateException(
                "La orden no se puede asignar."
            );
        }

        estado = EstadoOrden.ASIGNADA;
    }

    public void retirarAsignacion() {
        if (estado != EstadoOrden.ASIGNADA) {
            throw new IllegalStateException(
                "La orden no está asignada."
            );
        }

        estado = EstadoOrden.PENDIENTE;
    }

    public void iniciarVisita() {
        if (estado != EstadoOrden.ASIGNADA
                || visitasRealizadas >= obtenerMaximoVisitas()) {

            throw new IllegalStateException(
                "No se puede iniciar la visita de esta orden."
            );
        }

        visitasRealizadas++;
        estado = EstadoOrden.EN_ATENCION;
    }

    public void registrarResultado(ResultadoVisita resultado) {
        if (estado != EstadoOrden.EN_ATENCION) {
            throw new IllegalStateException(
                "La orden no está en atención."
            );
        }

        if (resultado == null) {
            throw new IllegalArgumentException(
                "El resultado no es válido."
            );
        }

        if (resultado == ResultadoVisita.COMPLETADA) {
            estado = EstadoOrden.COMPLETADA;

        } else if (visitasRealizadas >= obtenerMaximoVisitas()) {
            estado = EstadoOrden.CANCELADA;

        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public Zona getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getVisitasRealizadas() {
        return visitasRealizadas;
    }

    @Override
    public String toString() {
        return "Código: " + codigo
                + " | Categoría: " + obtenerCategoria()
                + " | Cliente: " + cliente
                + " | Dirección: " + direccion
                + " | Horas: " + horasEstimadas
                + " | Zona: " + zona
                + " | Estado: " + estado
                + " | Visitas: " + visitasRealizadas
                + "/" + obtenerMaximoVisitas()
                + " | Costo: Q" + calcularCosto()
                + " | Atención: " + estimarDiasAtencion()
                + " día(s) hábil(es)";
    }
}