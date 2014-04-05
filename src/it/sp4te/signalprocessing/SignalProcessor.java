/**
 * Classe statica che definisce le operazioni fondamentali da compiere sui segnali discreti come 
 * le operazioni di convoluzione e filtraggio
 * 
 * Nota: la convoluzione � un operazione sovraccarica, invocarla opportunamente
 * @author Antonio Tedeschi
 *
 */
package it.sp4te.signalprocessing;

import it.sp4te.domain.*;


public class SignalProcessor {

	/**
	 * NB La funzione length degli array restuisce la lunghezza dell'array che non 
	 * coincide con la numerazione degli indici!
	 * pertanto per applicare correttamente la formula della convoluzione è necessario prestare un po' 
	 * di attenzione agli indici con cui andiamo a lavorare per definire l'indice j per il ciclo for 
	 * della traslazione temporale in maniera dinamica in modo da saltare i casi in cui l'indice 
	 * j sia maggiore delle dimensione delle sequenze
	 *
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double[] convoluzione(double[] v1, double[] v2){
		
		//definizione della lunghezza del vettore finale contenente i risultati della convoluzione
		int finalLength = v1.length + v2.length-1;
		double[] result = new double[finalLength];
		
		//inizializzazione delle variabili temporali
		int upperBound = 0;
		int lowerBound = 0;
		
		//calcolo della convoluzione
		for(int k=0; k<finalLength; k++) {
			upperBound = Math.min(k, v2.length-1);
			lowerBound = Math.max(0, k - v1.length+1);
			for(int j=lowerBound; j<=upperBound; j++)
				result[k] += (v1[k-j]*v2[j]);
		}
		return result;
		
	}
	
	/**
	 * Convoluzione tra due vettori di numeri complessi
	 *  
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Complex[] convoluzione(Complex[] v1, Complex[] v2){
		
		//definizione della lunghezza del vettore finale contenente i risultati della convoluzione
		int finalLength = v1.length + v2.length-1;
		Complex[] result = new Complex[finalLength];
		
		//inizializzazione delle variabili temporali
		int upperBound = 0;
		int lowerBound = 0;
		
		//calcolo della convoluzione
		for(int k=0; k<finalLength; k++) {
			upperBound = Math.min(k, v2.length-1);
			lowerBound = Math.max(0, k - v1.length+1);
			result[k] = new Complex(0,0);
			for(int j=lowerBound; j<=upperBound; j++)
				result[k] = result[k].somma(v1[k-j].prodotto(v2[j]));			
		}
		return result;
		
	}
	
	/**
	 * Calcola il valore della sinc nel punto x.
	 * Se x=0 allora torna 1
	 * Nota: sinc(x) = (sin(pi*x))/(pi*x)
	 * @param x
	 * @return il valore della sinc per il punto x
	 */
	public static double sinc(double n, double band){
		double res = 0;
		if(n==0)
			res = 1;
		else if(n % (1/band) == 0)
			return 0;
		else 
			res = (Math.sin(Math.PI*band*n))/(Math.PI*band*n);
			
			return res;
	}
	
	/**
	 * Crea un nuovo segnale rappresentante il filtro passa-basso
	 * NOTA BENE: il numero di campioni che deve essere passato deve essere dispari
	 * Ottimizzare il metodo come richiesto nell'homework
	 * 
	 * @param band
	 * @param numCampioni
	 * @return Segnale discreto
	 */
	private static Signal lowPassFilter(double band) {
		
		//int numCampioni = 9;
		double fs = 2 * band;
		double tc = 1/fs;
		int numCampioni;
		int fattore = (int)(tc*10);
		if(fattore%2 == 0)
			numCampioni = fattore - 1;
		else numCampioni = fattore;
		Complex[] values = new Complex[numCampioni];
		int simmetria = numCampioni / 2;
		
		for(int n = - simmetria; n <= simmetria; n++){
			double realval = 2* band * sinc(n, 2 * band);
			values[n + simmetria] = new Complex(realval, 0);
		}
		
		Signal lpf = new Signal(values);
		System.out.println(numCampioni);
		return lpf;
	}
	/**
	 * filtro passa-banda -> rect di banda band centrata nell'origine convoluta 
	 * con due delta centrate in -freq e +freq, nel dominio del tempo 
	 * equivale alla sinc moltiplicata per un coseno
	 * @param freq frequenza centrale
	 * @param band larghezza di banda centrata in freq
	 * @return segnale filtrato all'interno di band
	 */
	private static Signal bandPassFilter(double freq, double band) {
		double fs = 2 * band;
		double tc = 1/fs;
		int numCampioni;
		int fattore = (int)(tc*10);
		if(fattore%2 == 0)
			numCampioni = fattore - 1;
		else numCampioni = fattore;
		Complex values[] = new Complex[numCampioni];
		int simmetria = numCampioni/2;
		for (int n = 0; n < numCampioni; n++) {
			double res = 2*band*sinc(n,2*band) * Math.cos(2*Math.PI*freq*n);
			Complex c = new Complex(res,0);
			values[n] = c;
		}
		return new Signal(values);
	}
	
	/**
	 * Operazione di convoluzione fra segnali:
	 * implementa un'operazione di convoluzione discreta fra due segnali passati come parametro.
	 * Presuppone che il segnale d'ingresso abbia parte reale e immaginaria non nulle 
	 * e che il filtro abbia solo parte reale.
	 * @param segnaleIn
	 * @param rispImpulsivaFiltro
	 * @return
	 */
	public static Signal convoluzione(Signal segnaleIn, Signal rispImpulsivaFiltro){
		
		Complex[] values = convoluzione(segnaleIn.getValues(), rispImpulsivaFiltro.getValues());
		Signal signal = new Signal(values);
		
		return signal;
	}
	
	public static void main(String[] args){

		// Esempio convoluzione tra reali
		double[] v1 = {3,2,1};
		double[] v2 = {1,1,2,1};
		
		double[] v3 = SignalProcessor.convoluzione(v1, v2);
		for(int i= 0; i< v3.length;i++)
			System.out.println(v3[i]);

		// esempio convoluzione tra Complessi		
		Complex[] vet1 = {new Complex(3,0), new Complex(2,0), new Complex(1,0)};
		Complex[] vet2 = {new Complex(1,0), new Complex(2,0), new Complex(1,0), new Complex(1,0)};
		
		Complex[] vet3 = SignalProcessor.convoluzione(vet1, vet2);
		System.out.println("\n----Convoluzione vet1 vet2\n");
		for(int i= 0; i< vet3.length;i++)
			System.out.println(vet3[i].toString());
		
		//esempio di filtraggio (convoluzione tra un segnale e il filtro passa-basso)
		Signal lpf = lowPassFilter(0.5);
		Signal s = new Signal(vet1);
		Signal filtrato = SignalProcessor.convoluzione(s, lpf);
		System.out.println("\n----PassaBasso\n");
		System.out.println(filtrato.toString());
		
		System.out.println("\n----PassaBanda\n");
		Signal bpf = bandPassFilter(100, 1);
		Signal filtratoBPF = SignalProcessor.convoluzione(s, bpf);
		System.out.println(filtratoBPF.toString());
		
	}
}
