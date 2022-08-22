
package mnkgame;

import java.util.Random;

public class martPlayer  implements MNKPlayer {
	private Random rand;
	private int TIMEOUT;

	public RandomPlayer() {
	}

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		// New random seed for each game
		rand    = new Random(System.currentTimeMillis()); 
		// Save the timeout for testing purposes
		TIMEOUT = timeout_in_secs;
		
		// Uncomment to chech the initialization timeout
		/* 
		try {
			Thread.sleep(1000*2*TIMEOUT);
		} 
		catch(Exception e) {
		}
		*/
	}

	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        
        //se è la prima mossa mette il simbolo al centro

        //se è la seconda mossa mette il simbolo accanto a quello dell'avversario

        //se resta una sola mossa la fa

        //se può vincere lo fa

        //se l'altro giocatore può vincere alla prossima mossa lo ferma

        /*
        * vede quanti simboli allineati ha al massimo l'avversario (>=2) e quanti ne ha lui
        * se ne ha più lui ne aggiunge uno altrimenti blocca l'avversario
        */

        



		// Uncomment to chech the move timeout
		/* 
		try {
			Thread.sleep(1000*2*TIMEOUT);
		} 
		catch(Exception e) {
		}
		*/
		return FC[rand.nextInt(FC.length)];
	}

	public String playerName() {
		return "5m4rt";
	}
}
