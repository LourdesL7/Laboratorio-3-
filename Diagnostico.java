import java.math.BigDecimal;

public class Diagnostico extends OrdenServicio {

    public Diagnostico(String cliente, String direccion,
                       double horasEstimadas, Zona zona) {

        super(cliente, direccion, horasEstimadas, zona);

        if (horasEstimadas > 1.5) {
            throw new IllegalArgumentException(
                "El diagnóstico admite como máximo 1.5 horas."
            );
        }
    }

    @Override
    public BigDecimal calcularCosto() {
        if (getZona() == Zona.METROPOLITANA) {
            return new BigDecimal("150.00");
        }

        return new BigDecimal("225.00");
    }

    @Override
    public int estimarDiasAtencion() {
        if (getZona() == Zona.METROPOLITANA) {
            return 1;
        }

        return 2;
    }

    @Override
    public int obtenerMaximoVisitas() {
        return 3;
    }

    @Override
    public String obtenerCategoria() {
        return "Diagnóstico";
    }
}