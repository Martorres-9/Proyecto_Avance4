package simuladororbita2;
import java.io.*;
public class SimuladorOrbita2 {

	private final BufferedReader bufEntrada = new BufferedReader(new InputStreamReader(System.in));
	private final String[] nombres = new String[15];
	private final String[] cuerposCentrales = new String[15];
	private final double[] masas = new double[15];
	private final double[] velocidades = new double[15];
	private final double[] distancias = new double[15];
	private final double[] angulos = new double[15];
	private final double[] combustibles = new double[15];
	private final String[] trayectorias = new String[15];
	private final String[] misiones = new String[15];
	private int contador = 0;
	private int circulares = 0;
	private int elipticas = 0;
	private int parabolicas = 0;
	private int hiperbolicas = 0;
	private double sumaVelocidad = 0;
	private double sumaDistancia = 0;
	private double sumaCombustible = 0;

	// Pequena clase auxiliar para poder "devolver" 3 valores desde asignarCuerpoCeleste
	// (Java no permite pasar primitivos/String por referencia como hace PSeInt).
	private static class CuerpoCeleste {
		String nombre;
		double masa;
		double radio;
		CuerpoCeleste(String nombre, double masa, double radio) {
			this.nombre = nombre;
			this.masa = masa;
			this.radio = radio;
		}
	}

	public static void main(String[] args) throws IOException {
		new SimuladorOrbita2().ejecutar();
	}

	private void ejecutar() throws IOException {
		boolean salir = false;
		do {
			System.out.println("");
			mostrarMenu();
			int opcionMenu = leerEntero();
			switch (opcionMenu) {
				case 1 -> registrarSimulacion();
				case 2 -> mostrarReporte();
				case 3 -> salir = true;
				default -> {
                                    System.out.println("Opcion no valida. Intente nuevamente.");
                                    esperarTecla();
                        }
			}
		} while (!salir);

		System.out.println("");
		System.out.println("Programa finalizado.");
		System.out.println("Gracias por usar el simulador.");
		esperarTecla();
	}

	private void mostrarMenu() {
		System.out.println("==========================================");
		System.out.println("     SIMULADOR DE ORBITAS Y TRAYECTORIAS  ");
		System.out.println("==========================================");
		System.out.println("1. Registrar nueva simulacion");
		System.out.println("2. Ver reporte general");
		System.out.println("3. Salir");
		System.out.println("Seleccione una opcion:");
	}

	private void mostrarCuerposCelestes() {
		System.out.println("===== CUERPOS CELESTES DISPONIBLES =====");
		System.out.println("1. Tierra");
		System.out.println("2. Luna");
		System.out.println("3. Marte");
		System.out.println("4. Venus");
		System.out.println("5. Mercurio");
		System.out.println("6. Jupiter");
		System.out.println("7. Saturno");
		System.out.println("8. Urano");
		System.out.println("9. Neptuno");
		System.out.println("10. Sol");
		System.out.println("11. Ingresar datos manualmente");
		System.out.println("Seleccione el cuerpo central:");
	}

	private CuerpoCeleste asignarCuerpoCeleste(int opcionCuerpo) {
		switch (opcionCuerpo) {
			case 1:  return new CuerpoCeleste("Tierra",   5.972 * Math.pow(10, 24), 6371000);
			case 2:  return new CuerpoCeleste("Luna",     7.342 * Math.pow(10, 22), 1737400);
			case 3:  return new CuerpoCeleste("Marte",    6.39  * Math.pow(10, 23), 3389500);
			case 4:  return new CuerpoCeleste("Venus",    4.867 * Math.pow(10, 24), 6051800);
			case 5:  return new CuerpoCeleste("Mercurio", 3.301 * Math.pow(10, 23), 2439700);
			case 6:  return new CuerpoCeleste("Jupiter",  1.898 * Math.pow(10, 27), 69911000);
			case 7:  return new CuerpoCeleste("Saturno",  5.683 * Math.pow(10, 26), 58232000);
			case 8:  return new CuerpoCeleste("Urano",    8.681 * Math.pow(10, 25), 25362000);
			case 9:  return new CuerpoCeleste("Neptuno",  1.024 * Math.pow(10, 26), 24622000);
			case 10: return new CuerpoCeleste("Sol",      1.989 * Math.pow(10, 30), 696340000);
			default: return new CuerpoCeleste("Manual", 0, 0);
		}
	}

	private void mostrarMisiones() {
		System.out.println("Seleccione la mision del satelite:");
		System.out.println("1. Comunicaciones");
		System.out.println("2. Navegacion (GPS)");
		System.out.println("3. Observacion terrestre");
		System.out.println("4. Investigacion cientifica");
		System.out.println("5. Meteorologia");
	}

	private String asignarMision(int opcionMision) {
		switch (opcionMision) {
			case 1:  return "Comunicaciones";
			case 2:  return "Navegacion (GPS)";
			case 3:  return "Observacion terrestre";
			case 4:  return "Investigacion cientifica";
			case 5:  return "Meteorologia";
			default: return "";
		}
	}

	private static double calcularPromedio(double suma, double cantidad) {
		if (cantidad > 0) {
			return suma / cantidad;
		} else {
			return 0;
		}
	}

	private static double calcularConsumoCombustible(double velocidadInicial, double numeroPasos, double pasoTiempo) {
		return (velocidadInicial / 1000) + (numeroPasos * pasoTiempo * 0.001);
	}

	private void registrarSimulacion() throws IOException {
		if (contador >= 15) {
			System.out.println("Capacidad maxima alcanzada. No se pueden registrar mas simulaciones.");
			esperarTecla();
			return;
		}

		System.out.println("");
		System.out.println("===== REGISTRO DE NUEVA SIMULACION =====");
		double constanteGravitacion = 0.00000000006674;
		System.out.println("Constante G usada: " + constanteGravitacion);

		int opcionCuerpo;
		do {
			mostrarCuerposCelestes();
			opcionCuerpo = leerEntero();
			if (opcionCuerpo < 1 || opcionCuerpo > 11) {
				System.out.println("ERROR: Debe seleccionar una opcion entre 1 y 11.");
			}
		} while (!(opcionCuerpo >= 1 && opcionCuerpo <= 11));

		String nombreCuerpoCentral;
		double masaCuerpoCentral;
		double radioPlaneta;
		if (opcionCuerpo == 11) {
			System.out.println("Ingrese el nombre del cuerpo central:");
			nombreCuerpoCentral = bufEntrada.readLine();
			System.out.println("Ingrese la masa del cuerpo central M, en kg:");
			masaCuerpoCentral = leerReal();
			System.out.println("Ingrese el radio del cuerpo central en metros:");
			radioPlaneta = leerReal();
		} else {
			CuerpoCeleste cuerpo = asignarCuerpoCeleste(opcionCuerpo);
			nombreCuerpoCentral = cuerpo.nombre;
			masaCuerpoCentral = cuerpo.masa;
			radioPlaneta = cuerpo.radio;
			System.out.println("Cuerpo central seleccionado: " + nombreCuerpoCentral);
			System.out.println("Masa cargada automaticamente: " + masaCuerpoCentral + " kg");
			System.out.println("Radio cargado automaticamente: " + radioPlaneta + " m");
		}

		System.out.println("Ingrese la masa del cuerpo en orbita m, en kg (ej: satelite):");
		double masaCuerpoOrbita = leerReal();
		System.out.println("Ingrese la distancia inicial desde el centro del cuerpo central, en metros:");
		double distanciaInicial = leerReal();
		System.out.println("Ingrese la velocidad inicial del cuerpo en orbita, en m/s:");
		double velocidadInicial = leerReal();
		System.out.println("Ingrese el angulo de la velocidad inicial respecto a la linea que une ambos cuerpos (en grados, 90 = perpendicular):");
		double anguloGrados = leerReal();
		System.out.println("Ingrese el nombre del satelite:");
		String nombreSatelite = bufEntrada.readLine();
		System.out.println("Ingrese el combustible disponible (kg):");
		double combustibleDisponible = leerReal();

		int opcionMision;
		do {
			mostrarMisiones();
			opcionMision = leerEntero();
			if (opcionMision < 1 || opcionMision > 5) {
				System.out.println("ERROR: Debe seleccionar una opcion entre 1 y 5.");
			}
		} while (!(opcionMision >= 1 && opcionMision <= 5));
		String misionSatelite = asignarMision(opcionMision);

		if (masaCuerpoCentral <= 0 || masaCuerpoOrbita <= 0 || distanciaInicial <= 0
				|| radioPlaneta <= 0 || distanciaInicial <= radioPlaneta) {
			System.out.println("ERROR:");
			System.out.println("Las masas, el radio y la distancia deben ser valores positivos.");
			System.out.println("La distancia inicial debe ser mayor que el radio del cuerpo central.");
			System.out.println("La simulacion no sera registrada.");
			esperarTecla();
			return;
		}

		double anguloRadianes = anguloGrados * Math.PI / 180;
		double fuerzaGravitatoria = constanteGravitacion * masaCuerpoCentral * masaCuerpoOrbita / Math.pow(distanciaInicial, 2);
		double aceleracionGravitatoria = constanteGravitacion * masaCuerpoCentral / Math.pow(distanciaInicial, 2);
		double aceleracionCentripeta = Math.pow(velocidadInicial, 2) / distanciaInicial;
		double velocidadOrbitalIdeal = Math.sqrt(constanteGravitacion * masaCuerpoCentral / distanciaInicial);
		double velocidadEscape = Math.sqrt(2 * constanteGravitacion * masaCuerpoCentral / distanciaInicial);
		double periodoOrbitalSegundos = Math.sqrt((4 * Math.pow(Math.PI, 2) * Math.pow(distanciaInicial, 3)) / (constanteGravitacion * masaCuerpoCentral));
		double periodoOrbitalDias = periodoOrbitalSegundos / 86400;
		double energiaCinetica = 0.5 * masaCuerpoOrbita * Math.pow(velocidadInicial, 2);
		double energiaPotencial = -1 * (constanteGravitacion * masaCuerpoCentral * masaCuerpoOrbita / distanciaInicial);
		double energiaTotal = energiaCinetica + energiaPotencial;

		String tipoTrayectoria;
		if (energiaTotal < 0) {
			tipoTrayectoria = (velocidadInicial == velocidadOrbitalIdeal) ? "CIRCULAR" : "ELIPTICA";
		} else {
			tipoTrayectoria = (energiaTotal == 0) ? "PARABOLICA" : "HIPERBOLICA";
		}

		System.out.println("===== RESULTADOS DEL CALCULO =====");
		System.out.println("Cuerpo central: " + nombreCuerpoCentral);
		System.out.println("Fuerza gravitatoria = " + fuerzaGravitatoria + " N");
		System.out.println("Aceleracion gravitatoria = " + aceleracionGravitatoria + " m/s^2");
		System.out.println("Aceleracion centripeta = " + aceleracionCentripeta + " m/s^2");
		System.out.println("Velocidad orbital ideal = " + velocidadOrbitalIdeal + " m/s");
		System.out.println("Velocidad de escape = " + velocidadEscape + " m/s");
		System.out.println("Periodo orbital = " + periodoOrbitalSegundos + " s  (aprox. " + periodoOrbitalDias + " dias)");
		System.out.println("Energia cinetica = " + energiaCinetica + " J");
		System.out.println("Energia potencial = " + energiaPotencial + " J");
		System.out.println("Energia total del sistema = " + energiaTotal + " J");
		System.out.println("TIPO DE TRAYECTORIA OBTENIDA: " + tipoTrayectoria);

		System.out.println("===== SISTEMA DE ALERTAS =====");
		if (velocidadInicial < velocidadOrbitalIdeal) {
			System.out.println("ALERTA: La velocidad ingresada es menor que la velocidad orbital ideal.");
			System.out.println("La orbita podria no mantenerse estable.");
		}
		if (velocidadInicial >= velocidadEscape) {
			System.out.println("ALERTA: La nave ha alcanzado o superado la velocidad de escape.");
			System.out.println("Podria abandonar el campo gravitatorio.");
		}
		if (combustibleDisponible < 100) {
			System.out.println("ALERTA: Combustible insuficiente para una mision prolongada.");
		}
		if (distanciaInicial < radioPlaneta + 100000) {
			System.out.println("ALERTA: La distancia al cuerpo central es muy baja.");
			System.out.println("Existe riesgo de colision o impacto con el cuerpo celeste.");
		}

		double velocidadX = velocidadInicial * Math.cos(anguloRadianes);
		double velocidadY = velocidadInicial * Math.sin(anguloRadianes);
		System.out.println("Direccion del movimiento inicial:");
		System.out.println("Componente en X = " + velocidadX + " m/s");
		System.out.println("Componente en Y = " + velocidadY + " m/s");

		System.out.println("===== EVOLUCION APROXIMADA DE LA TRAYECTORIA =====");
		System.out.println("Ingrese el numero de pasos a simular (ej: 5):");
		int numeroPasos = leerEntero();
		System.out.println("Ingrese el paso de tiempo en segundos (ej: 60):");
		double pasoTiempo = leerReal();

		double posicionX = distanciaInicial;
		double posicionY = 0;
		double tiempoTotalSimulado = 0;
		int contadorPaso = 1;
		boolean colision = false;
		while (contadorPaso <= numeroPasos && !colision) {
			double distanciaActual = Math.sqrt(Math.pow(posicionX, 2) + Math.pow(posicionY, 2));
			if (distanciaActual <= radioPlaneta) {
				System.out.println("COLISION DETECTADA:");
				System.out.println("La distancia actual es menor o igual al radio del cuerpo central.");
				colision = true;
			} else {
				double aceleracionX = (-1 * constanteGravitacion * masaCuerpoCentral * posicionX) / Math.pow(distanciaActual, 3);
				double aceleracionY = (-1 * constanteGravitacion * masaCuerpoCentral * posicionY) / Math.pow(distanciaActual, 3);
				velocidadX = velocidadX + (aceleracionX * pasoTiempo);
				velocidadY = velocidadY + (aceleracionY * pasoTiempo);
				posicionX = posicionX + (velocidadX * pasoTiempo);
				posicionY = posicionY + (velocidadY * pasoTiempo);
				tiempoTotalSimulado = tiempoTotalSimulado + pasoTiempo;
				System.out.println("Paso " + contadorPaso + "  (t = " + tiempoTotalSimulado + " s)");
				System.out.println("Posicion -> x = " + posicionX + " m   y = " + posicionY + " m");
				System.out.println("Distancia al cuerpo central = " + distanciaActual + " m");
				System.out.println("Velocidad -> vx = " + velocidadX + " m/s   vy = " + velocidadY + " m/s");
				contadorPaso = contadorPaso + 1;
			}
		}

		double consumoCombustible = calcularConsumoCombustible(velocidadInicial, numeroPasos, pasoTiempo);
		double combustibleRestante = combustibleDisponible - consumoCombustible;
		if (combustibleRestante < 0) {
			combustibleRestante = 0;
		}
		System.out.println("Combustible consumido aproximado: " + consumoCombustible + " kg");
		System.out.println("Combustible restante aproximado: " + combustibleRestante + " kg");
		System.out.println("Simulacion finalizada.");

		// Se guarda en la posicion "contador" (0-based), no "contador+1" como en el pseudocodigo original (1-based)
		nombres[contador] = nombreSatelite;
		cuerposCentrales[contador] = nombreCuerpoCentral;
		misiones[contador] = misionSatelite;
		masas[contador] = masaCuerpoOrbita;
		velocidades[contador] = velocidadInicial;
		distancias[contador] = distanciaInicial;
		angulos[contador] = anguloGrados;
		combustibles[contador] = combustibleRestante;
		trayectorias[contador] = tipoTrayectoria;

		sumaVelocidad = sumaVelocidad + velocidadInicial;
		sumaDistancia = sumaDistancia + distanciaInicial;
		sumaCombustible = sumaCombustible + combustibleRestante;

		switch (tipoTrayectoria) {
			case "CIRCULAR":   circulares = circulares + 1;   break;
			case "ELIPTICA":   elipticas = elipticas + 1;     break;
			case "PARABOLICA": parabolicas = parabolicas + 1; break;
			case "HIPERBOLICA": hiperbolicas = hiperbolicas + 1; break;
		}
		contador = contador + 1;
		esperarTecla();
	}

	private void mostrarReporte() throws IOException {
		System.out.println("");
		System.out.println("===== REPORTE GENERAL =====");
		if (contador > 0) {
			for (int i = 0; i < contador; i++) {
				System.out.println("------------------------------------------");
				System.out.println("Satelite " + (i + 1));
				System.out.println("Nombre: " + nombres[i]);
				System.out.println("Cuerpo central: " + cuerposCentrales[i]);
				System.out.println("Mision: " + misiones[i]);
				System.out.println("Velocidad inicial: " + velocidades[i] + " m/s");
				System.out.println("Distancia inicial: " + distancias[i] + " m");
				System.out.println("Combustible restante: " + combustibles[i] + " kg");
				System.out.println("Trayectoria: " + trayectorias[i]);
			}
			System.out.println("===== RESUMEN DE TRAYECTORIAS =====");
			System.out.println("Orbitas circulares: " + circulares);
			System.out.println("Orbitas elipticas: " + elipticas);
			System.out.println("Orbitas parabolicas: " + parabolicas);
			System.out.println("Orbitas hiperbolicas: " + hiperbolicas);

			double promedioVelocidad = calcularPromedio(sumaVelocidad, contador);
			double promedioDistancia = calcularPromedio(sumaDistancia, contador);
			double promedioCombustible = calcularPromedio(sumaCombustible, contador);

			System.out.println("===== PROMEDIOS GENERALES =====");
			System.out.println("Velocidad promedio: " + promedioVelocidad + " m/s");
			System.out.println("Distancia promedio: " + promedioDistancia + " m");
			System.out.println("Combustible restante promedio: " + promedioCombustible + " kg");
		} else {
			System.out.println("Aun no existen simulaciones registradas.");
		}
		esperarTecla();
	}

	// ===== Utilidades de lectura/espera =====

	private int leerEntero() throws IOException {
		return Integer.parseInt(bufEntrada.readLine().trim());
	}

	private double leerReal() throws IOException {
		// Acepta tanto "." como "," como separador decimal para mayor tolerancia
		String linea = bufEntrada.readLine().trim().replace(",", ".");
		return Double.parseDouble(linea);
	}

	private void esperarTecla() throws IOException {
		System.out.println("(Presione Enter para continuar)");
		bufEntrada.readLine();
	}
}