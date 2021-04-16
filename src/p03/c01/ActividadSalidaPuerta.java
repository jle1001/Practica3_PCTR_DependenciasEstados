package src.p03.c01;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Clase que lanzará los hilos de salida de cada puerta, de forma concurrente.
 * Se implementa la interfaz Runnable ya que esta clase, va a ser usada para lanzar hilos de forma concurrente (run()).
 * 
 * @author José Ángel López
 *
 */
public class ActividadSalidaPuerta implements Runnable {

	// Generación del tiempo aleatorio de espera para entrar al parque. (rango 0-1000)
	private Random randomInt = new Random();
	public int r = randomInt.nextInt(1000);
	
	/**
	 * Constante que índica el número máximo de salidas por puerta.
	 */
	private static final int NSalidas = 20;
	
	/**
	 * Variable String donde se almacenará la puerta utilizada.
	 */
	private String puerta;
	
	/**
	 * Parque con el cual trabajaremos. El tipo es la interfaz IParque.
	 */
	private IParque parque;
	
	/**
	 * Constructor público que usaremos para inicializar la actividad de salida por cada puerta.
	 * @param puerta puerta que usaremos.
	 * @param parque parque en el que trabajaremos continuamente.
	 */
	public ActividadSalidaPuerta(String puerta, IParque parque) {
		this.puerta = puerta;
		this.parque = parque;
	}
	
	@Override
	/**
	 * Método lanzador. 
	 */
	public void run() {
		for(int i = 0; i < NSalidas; i++) {
			try {
				parque.salirDelParque(puerta);
				TimeUnit.MILLISECONDS.sleep(r);
			} catch(InterruptedException e) {
				System.out.println(e);
			}
			
		}	
	}
	
}
