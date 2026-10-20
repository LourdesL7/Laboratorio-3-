# Laboratorio 3 - Herencia

Aplicación de consola en Java para gestionar los servicios técnicos de TecniHogar mediante herencia y polimorfismo.

## Requisitos

Tener instalado JDK 11 o posterior. No se necesitan bibliotecas externas.

## Compilación

Abrir una terminal en la carpeta que contiene todos los archivos .java y ejecutar:

javac -encoding UTF-8 *.java

## Ejecución

Después de compilar, ejecutar:

java Aplicacion

La clase principal es Aplicacion. Para cerrar el programa, seleccionar la opción 0 del menú.

## Funciones

- Cotizar y registrar órdenes de las cuatro categorías.
- Registrar y consultar cuadrillas.
- Crear jornadas, asignar y retirar órdenes.
- Iniciar jornadas y registrar resultados de visitas.
- Consultar órdenes y generar reportes de estados, ingresos y jornadas sin cerrar.

Ingresar los decimales con punto y los montos sin el símbolo Q. Los códigos distinguen mayúsculas de minúsculas.

## Datos iniciales

El programa inicia con ocho órdenes pendientes, dos por categoría, y tres cuadrillas. C01 está certificada en refrigeración; C02 y C03 no lo están.

Las órdenes iniciales tienen cero visitas y no generan ingresos de registro. Los datos se conservan únicamente en memoria y se reinician al cerrar y volver a ejecutar el programa.
