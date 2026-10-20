import java.math.BigDecimal;
import java.math.RoundingMode;

public class MantenimientoAireAcondicionado extends OrdenServicio {
    private final int btu;

    public MantenimientoAireAcondicionado(
            String cliente,
            String direccion,
            double horasEstimadas,
            Zona zona,
            int btu) {

        super(cliente, direccion, horasEstimadas, zona);

        if (zona != Zona.METROPOLITANA) {
            throw new IllegalArgumentException(
                "El mantenimiento solo se acepta en zona metropolitana."
            );
        }

        if (btu < 9000 || btu > 60000) {
            throw new IllegalArgumentException(
                "Los BTU deben estar entre 9000 y 60000."
            );
        }

        this.btu = btu;
    }

    @Override
    public BigDecimal calcularCosto() {
        BigDecimal costoHoras = BigDecimal.valueOf(getHorasEstimadas())
                .multiply(new BigDecimal("110.00"));

        return calcularTarifaBase()
                .add(costoHoras)
                .add(new BigDecimal("80.00"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int estimarDiasAtencion() {
        return 2;
    }

    @Override
    public int obtenerMaximoVisitas() {
        return 1;
    }

    @Override
    public String obtenerCategoria() {
        return "Mantenimiento de aire acondicionado";
    }

    @Override
    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return cuadrilla != null
                && cuadrilla.isCertificadaRefrigeracion();
    }

    public int getBtu() {
        return btu;
    }

    @Override
    public String toString() {
        return super.toString() + " | BTU: " + btu;
    }
}