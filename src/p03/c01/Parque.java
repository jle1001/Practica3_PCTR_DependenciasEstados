package src.p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

public class Parque implements IParque{

	private int aforo;
	private int contadorPersonasTotales;
	private Hashtable<String, Integer> contadoresPersonasPuerta;
	
	
	public Parque(int aforo) {	// Añadido parámetro aforo para especificar la máxima cantidad de personas en el parque.
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String, Integer>();
		this.aforo = aforo;
	}


	@Override
	/**
	 * Método sincronizado que indica la entrada en el parque por una puerta concreta.
	 * Es un método sincronizado para poder ser usado de forma segura en un entorno concurrente.
	 * 
	 * @param puerta puerta usada por la que se entra al parque.
	 */
	public synchronized void entrarAlParque(String puerta){		// Cambiada la cabecera de la función, ahora el método es synchronized.
		
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		// Llamada al método de comprobación del estado del parque antes de permitir entrar.
		comprobarAntesDeEntrar();	
		
		// Aumentamos el contador total y el individual
		contadorPersonasTotales++;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)+1);
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");
		
		// Notificar la entrada al parque a los demás hilos para desbloquearlos de una posible espera.
		notifyAll();
		
		// Comprobar invariantes
		checkInvariante();
		
	}
	
	@Override
	/**
	 * Método sincronizado que indica la salida del parque por una puerta concreta.
	 * Es un método sincronizado para poder ser usado de forma segura en un entorno concurrente.
	 * 
	 * @param puerta puerta usada por la que se sale del parque.
	 */
	public synchronized void salirDelParque(String puerta) {
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		// Llamada al método de comprobación del estado del parque antes de permitir entrar.
		comprobarAntesDeSalir();	
		
		// Aumentamos el contador total y el individual
		contadorPersonasTotales--;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)-1);
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");
		
		// Notificar la entrada al parque a los demás hilos para desbloquearlos de una posible espera (wait())
		notifyAll();
		
		// Comprobar invariantes
		checkInvariante();
	}
	
	
	private void imprimirInfo (String puerta, String movimiento){
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); //+ " tiempo medio de estancia: "  + tmedio);
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuerta.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}
	
	/**
	 * Aumenta el número de personas en cada puerta.
	 * @return sumaContadoresPuerta
	 */
	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	/**
	 * Método que comprueba los invariantes en el parque para evitar situaciones de conflicto.
	 * Tiene cierta relación con la tabla de dependencias del parque.
	 */
	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parte";
		assert contadorPersonasTotales > aforo : "INV: El número de personas en el parque es superior al aforo permitido.";
		assert contadorPersonasTotales < 0 : "Error en las salidas del parque.";
	}

	/**
	 * Método que comprueba el estado del parque antes de permitir entrar a más personas.
	 * Si el contador de personas totales del parque es igual al máximo aforo permitido, deja el hilo en estado de espera hasta que le notifiquen ha salido alguien.
	 */
	protected synchronized void comprobarAntesDeEntrar(){	// Añadido el synchronized en la cabecera.
		while(contadorPersonasTotales == aforo) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Método que comprueba el estado del parque antes de permitir salir a más personas.
	 * Si el contador de personas totales del parque es 0 (parque vacío), se deja al hilo en estado de espera hasta que le notifiquen que ha entrado alguien.
	 */
	protected synchronized void comprobarAntesDeSalir(){	// Añadido el synchronized en la cabecera.
		while(contadorPersonasTotales == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


}
