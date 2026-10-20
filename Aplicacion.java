import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Aplicacion {
    private final SistemaTecniHogar sistema;
    private final Scanner entrada;

    public Aplicacion() {
        sistema = new SistemaTecniHogar();
        entrada = new Scanner(System.in, "UTF-8");
    }

    public static void main(String[] args) {
        new Aplicacion().ejecutarMenu();
    }

    public void ejecutarMenu() {
        boolean salir = false;

        while (!salir) {
            try {
                System.out.println("\nTECNIHOGAR");
                System.out.println("1. Cotizar un servicio");
                System.out.println("2. Registrar una orden");
                System.out.println("3. Registrar una cuadrilla");
                System.out.println("4. Crear una jornada");
                System.out.println("5. Asignar una orden a una jornada");
                System.out.println("6. Retirar una orden de una jornada");
                System.out.println("7. Iniciar una jornada");
                System.out.println("8. Registrar resultado de una visita");
                System.out.println("9. Consultar órdenes");
                System.out.println("10. Consultar cuadrillas");
                System.out.println("11. Reporte de órdenes por categoría y estado");
                System.out.println("12. Reporte de ingresos");
                System.out.println("13. Reporte de jornadas sin cerrar");
                System.out.println("0. Salir");

                int opcion = leerEntero("Opción: ");

                if (opcion == 0) {
                    salir = true;
                    System.out.println("Programa finalizado.");
                } else {
                    ejecutarOpcion(opcion);
                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(
                    "Operación rechazada: " + e.getMessage()
                );

            } catch (NoSuchElementException e) {
                salir = true;
                System.out.println(
                    "\nEntrada finalizada. Programa cerrado."
                );
            }
        }
    }

    private void ejecutarOpcion(int opcion) {
        String codigoJornada;
        String codigoOrden;

        switch (opcion) {
            case 1:
                OrdenServicio cotizacion = leerServicio();

                System.out.println(
                    sistema.cotizar(cotizacion)
                );

                int aceptar;

                do {
                    aceptar = leerEntero(
                        "¿Acepta y desea registrar? 1. Sí / 2. No: "
                    );

                    if (aceptar != 1 && aceptar != 2) {
                        System.out.println("Seleccione 1 o 2.");
                    }

                } while (aceptar != 1 && aceptar != 2);

                if (aceptar == 1) {
                    codigoOrden = leerTexto(
                        "Código de la nueva orden: "
                    );

                    sistema.registrarOrden(
                        codigoOrden,
                        cotizacion
                    );

                    System.out.println(
                        "Orden registrada. Cobro: Q"
                        + cotizacion.calcularCosto()
                    );

                } else {
                    System.out.println(
                        "Cotización finalizada sin registrar ni cobrar."
                    );
                }

                break;

            case 2:
                codigoOrden = leerTexto(
                    "Código de la nueva orden: "
                );

                OrdenServicio nueva = leerServicio();

                sistema.registrarOrden(
                    codigoOrden,
                    nueva
                );

                System.out.println(
                    "Orden registrada pendiente. Cobro: Q"
                    + nueva.calcularCosto()
                );

                break;

            case 3:
                String codigoCuadrilla = leerTexto(
                    "Código de cuadrilla: "
                );

                double capacidad = leerDecimal(
                    "Capacidad máxima de horas: "
                );

                int certificacion;

                do {
                    certificacion = leerEntero(
                        "Certificada en refrigeración: 1. Sí / 2. No: "
                    );

                    if (certificacion != 1 && certificacion != 2) {
                        System.out.println("Seleccione 1 o 2.");
                    }

                } while (certificacion != 1 && certificacion != 2);

                sistema.registrarCuadrilla(
                    new Cuadrilla(
                        codigoCuadrilla,
                        capacidad,
                        certificacion == 1
                    )
                );

                System.out.println("Cuadrilla registrada.");

                break;

            case 4:
                codigoJornada = leerTexto(
                    "Código de la nueva jornada: "
                );

                String cuadrilla = leerTexto(
                    "Código de cuadrilla: "
                );

                sistema.crearJornada(
                    codigoJornada,
                    cuadrilla
                );

                System.out.println(
                    "Jornada creada en preparación."
                );

                break;

            case 5:
                codigoJornada = leerTexto(
                    "Código de jornada: "
                );

                codigoOrden = leerTexto(
                    "Código de orden pendiente: "
                );

                sistema.asignarOrden(
                    codigoJornada,
                    codigoOrden
                );

                System.out.println(
                    "Orden asignada. No se generó ningún cobro."
                );

                break;

            case 6:
                codigoJornada = leerTexto(
                    "Código de jornada: "
                );

                codigoOrden = leerTexto(
                    "Código de orden a retirar: "
                );

                sistema.retirarOrden(
                    codigoJornada,
                    codigoOrden
                );

                System.out.println(
                    "Orden retirada y pendiente. No se generó ningún cobro."
                );

                break;

            case 7:
                codigoJornada = leerTexto(
                    "Código de jornada: "
                );

                sistema.iniciarJornada(codigoJornada);

                System.out.println(
                    "Jornada iniciada. Se cobraron Q40.00 por cada "
                    + "visita adicional, si corresponde."
                );

                break;

            case 8:
                codigoJornada = leerTexto(
                    "Código de jornada: "
                );

                codigoOrden = leerTexto(
                    "Código de orden: "
                );

                ResultadoVisita resultado = leerResultado();

                sistema.registrarResultado(
                    codigoJornada,
                    codigoOrden,
                    resultado
                );

                System.out.println(
                    "Resultado registrado. La jornada cierra si todas "
                    + "sus órdenes tienen resultado."
                );

                break;

            case 9:
                for (OrdenServicio orden : sistema.consultarOrdenes()) {
                    System.out.println(orden);
                }

                break;

            case 10:
                for (Cuadrilla equipo : sistema.consultarCuadrillas()) {
                    boolean disponible = true;

                    for (Jornada jornada : sistema.consultarJornadas()) {
                        if (jornada.getCuadrilla() == equipo
                                && jornada.getEstado()
                                    != EstadoJornada.CERRADA) {

                            disponible = false;
                        }
                    }

                    System.out.println(
                        equipo + " | Disponible: "
                        + (disponible ? "Sí" : "No")
                    );
                }

                break;

            case 11:
                System.out.println(
                    sistema.generarReporteOrdenes()
                );

                break;

            case 12:
                System.out.println(
                    sistema.generarReporteIngresos()
                );

                break;

            case 13:
                System.out.println(
                    sistema.generarReporteJornadas()
                );

                break;

            default:
                System.out.println(
                    "Opción no válida. Seleccione una opción del menú."
                );
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);

        return entrada.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(
                    leerTexto(mensaje)
                );

            } catch (NumberFormatException e) {
                System.out.println(
                    "Formato incorrecto. Ingrese un número entero."
                );
            }
        }
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            try {
                double valor = Double.parseDouble(
                    leerTexto(mensaje)
                );

                if (!Double.isFinite(valor)) {
                    System.out.println(
                        "Ingrese un número decimal finito."
                    );
                } else {
                    return valor;
                }

            } catch (NumberFormatException e) {
                System.out.println(
                    "Formato incorrecto. Use punto para los decimales, "
                    + "por ejemplo 2.5."
                );
            }
        }
    }

    private BigDecimal leerMonto(String mensaje) {
        while (true) {
            try {
                return new BigDecimal(
                    leerTexto(mensaje)
                );

            } catch (NumberFormatException e) {
                System.out.println(
                    "Monto incorrecto. Ingrese el número sin Q "
                    + "y con punto decimal."
                );
            }
        }
    }

    private Zona leerZona() {
        while (true) {
            int zona = leerEntero(
                "Zona: 1. Metropolitana / 2. Periférica: "
            );

            if (zona == 1) {
                return Zona.METROPOLITANA;
            }

            if (zona == 2) {
                return Zona.PERIFERICA;
            }

            System.out.println("Seleccione 1 o 2.");
        }
    }

    private ResultadoVisita leerResultado() {
        while (true) {
            int resultado = leerEntero(
                "Resultado: 1. Completada / 2. Visita fallida: "
            );

            if (resultado == 1) {
                return ResultadoVisita.COMPLETADA;
            }

            if (resultado == 2) {
                return ResultadoVisita.FALLIDA;
            }

            System.out.println("Seleccione 1 o 2.");
        }
    }

    private OrdenServicio leerServicio() {
        int categoria;

        do {
            System.out.println("1. Diagnóstico");
            System.out.println("2. Instalación");
            System.out.println("3. Instalación eléctrica certificada");
            System.out.println("4. Mantenimiento de aire acondicionado");

            categoria = leerEntero("Categoría: ");

            if (categoria < 1 || categoria > 4) {
                System.out.println(
                    "Seleccione una categoría entre 1 y 4."
                );
            }

        } while (categoria < 1 || categoria > 4);

        String cliente = leerTexto(
            "Nombre del cliente: "
        );

        String direccion = leerTexto(
            "Dirección: "
        );

        double horas = leerDecimal(
            "Horas estimadas: "
        );

        Zona zona = leerZona();

        switch (categoria) {
            case 1:
                return new Diagnostico(
                    cliente,
                    direccion,
                    horas,
                    zona
                );

            case 2:
                int puntos = leerEntero(
                    "Cantidad de puntos: "
                );

                return new Instalacion(
                    cliente,
                    direccion,
                    horas,
                    zona,
                    puntos
                );

            case 3:
                int puntosCertificados = leerEntero(
                    "Cantidad de puntos: "
                );

                BigDecimal materiales = leerMonto(
                    "Valor declarado de materiales: Q"
                );

                return new InstalacionElectricaCertificada(
                    cliente,
                    direccion,
                    horas,
                    zona,
                    puntosCertificados,
                    materiales
                );

            case 4:
                int btu = leerEntero(
                    "Capacidad del equipo en BTU: "
                );

                return new MantenimientoAireAcondicionado(
                    cliente,
                    direccion,
                    horas,
                    zona,
                    btu
                );

            default:
                throw new IllegalArgumentException(
                    "Categoría no válida."
                );
        }
    }
}