public class Cuadrilla {
    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificadaRefrigeracion;

    public Cuadrilla(String codigo, double capacidadHoras,
                     boolean certificadaRefrigeracion) {

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El código de cuadrilla no puede estar vacío."
            );
        }

        if (!Double.isFinite(capacidadHoras) || capacidadHoras <= 0) {
            throw new IllegalArgumentException(
                "La capacidad debe ser positiva y finita."
            );
        }

        this.codigo = codigo.trim();
        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadHoras() {
        return capacidadHoras;
    }

    public boolean isCertificadaRefrigeracion() {
        return certificadaRefrigeracion;
    }

    @Override
    public String toString() {
        return "Código: " + codigo
                + " | Capacidad: " + capacidadHoras + " horas"
                + " | Certificada en refrigeración: "
                + (certificadaRefrigeracion ? "Sí" : "No");
    }
}