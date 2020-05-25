import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MiClaseSax extends DefaultHandler {

	// CONSTANTES
	private static final String FICHERO_CREAR = "universidad.txt";
	private static final String VALOR_INICIAL_CADENAS = "";
	private static final int VALOR_INICIAL_NUMEROS = 0;

	// Atributos
	private String etiquetaActual, departamento, empleadoActual, empleadoMayorSueldo, mediaSalarioFormateado;
	private double mediaSalarios, sumaSalarios, salario, salarioMayor;
	private boolean esNombreEmpleado, esMismoDepartamento;
	private int contadorEmpleados; // Para la media
	private BufferedWriter filtroEscritura;

	@Override
	public void startDocument() throws SAXException {
		System.out.println("Empiezo");
		reiniciarValores();
		if (!Files.isRegularFile(Paths.get(FICHERO_CREAR))) {
			try {
				filtroEscritura = Files.newBufferedWriter(Paths.get(FICHERO_CREAR));
			} catch (IOException e) {
				System.err.println("No se ha podido crear el BW");
			}
		} else {
			throw new SAXException("Error. Ya existe el fichero");
		}

	}

	@Override
	public void endDocument() throws SAXException {
		System.out.println("Acabo");
		try {
			filtroEscritura.close();
		} catch (IOException e) {
			System.err.println("Error, no se ha podido cerrar");
		}

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		this.etiquetaActual = qName;

		if (qName.equalsIgnoreCase("departamento")) {
			esMismoDepartamento = true;
		}
		if (qName.equalsIgnoreCase("empleado")) {
			salario = Double.parseDouble(attributes.getValue("salario"));
			if (esMismoDepartamento) {
				sumaSalarios = sumaSalarios + salario; // Si son del mismo departamento, los suma
				contadorEmpleados++;
			}
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("puesto")) {
			this.esNombreEmpleado = true; // Cuando cierra puesto, viene el nombre de empleado
		}
		if (qName.equalsIgnoreCase("empleado")) {
			this.esNombreEmpleado = false; // Cuando cierra empleado, la pone a falso
		}
		if (qName.equalsIgnoreCase("departamento")) {
			esMismoDepartamento = false;
		}
		if (qName.equalsIgnoreCase("departamento")) {
			mediaSalarios = sumaSalarios / contadorEmpleados;
			mediaSalarioFormateado = String.format("%5.2f%n", mediaSalarios);
			// AQUÍ ESCRIBIR
			try {
				filtroEscritura.write("DEPARTAMENTO " + departamento + "\n");
				filtroEscritura.write("\tMEDIA DE SUELDO: " + mediaSalarioFormateado);
				filtroEscritura.write("\tEMPLEADO CON MAYOR SUELDO: " + empleadoMayorSueldo + "\n\n");

			} catch (IOException e) {
				System.err.println("No se ha podido escribir");
			}

			reiniciarValores(); // Creo este método porque en el 3er dep me repetia pedro paniagua
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		String contenido = new String(ch, start, length);
		contenido = contenido.replaceAll("[\t\n]", "");

		if (contenido.length() != 0) {
			switch (etiquetaActual) {
			case "nombre":
				if (esNombreEmpleado) {
					this.empleadoActual = contenido;

					if (salario > salarioMayor) {
						empleadoMayorSueldo = empleadoActual;
						salarioMayor = salario;
					}
				} else {
					// Es el nombre del departamento
					this.departamento = contenido;
				}
				break;
			}
		}
	}

	public void reiniciarValores() {
		reiniciarCadenas();
		reiniciarNumeros();
	}

	private void reiniciarNumeros() {

		salarioMayor = VALOR_INICIAL_NUMEROS;
		sumaSalarios = VALOR_INICIAL_NUMEROS;
		mediaSalarios = VALOR_INICIAL_NUMEROS;
		contadorEmpleados = VALOR_INICIAL_NUMEROS;
	}

	private void reiniciarCadenas() {

		mediaSalarioFormateado = VALOR_INICIAL_CADENAS;
		empleadoMayorSueldo = VALOR_INICIAL_CADENAS;
	}
}
