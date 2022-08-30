package mnkgame;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;

//usa un array dinamico per BC


public class CompetitivePlayerTest  implements MNKPlayer {
	private Random rand;
    private int M;
    private int N;
    private int K;
	private MNKBoard B;
	private MNKGameState myWin;
	private MNKGameState yourWin;
	private int TIMEOUT;

	public CompetitivePlayerTest() {
	}

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {

        rand    = new Random(System.currentTimeMillis()); 
		this.M  = M;
        this.N  = N;
        this.K  = K;
		B       = new MNKBoard(M,N,K);
		myWin   = first ? MNKGameState.WINP1 : MNKGameState.WINP2; 
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
		TIMEOUT = timeout_in_secs;

	}

    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		if(MC.length > 0) {
			MNKCell c = MC[MC.length-1]; 
			B.markCell(c.i,c.j);         
		}
        else if(MC.length == 0) {
            //se è la prima mossa mette il simbolo al centro
            int m = M/2;
            int n = N/2;
            MNKCell s = new MNKCell(m, n);
			B.markCell(s.i,s.j);
            return s;
        }

        if(FC.length == 1) {
            return FC[0];
		}

        //iterative deepening
        final int max_depth = 4;
        double min_a = -1.0;
        double max_b = 1.0;
        int index = 0;

        for(int i=0; i < max_depth; i++) {                      
            double[] results = new double[FC.length];
            for(int j=0; j < FC.length; j++) {          //il numero di iterazioni può essere ridotto per migliorare le prestazioni;
                B.markCell(FC[j].i, FC[j].j);
                results[j] = AlphaBeta(B, min_a, max_b, i);
                B.unmarkCell();
            }
            if(B.currentPlayer() == 0) {            //*********************************************************
                double max = -1000;
                for(int j=0; j < FC.length; j++) {
                    if(results[j] > max) {
                       max = results[j];
                      index = j;
                    }
                }
            }
            else {
                double min = 1000;
                for(int j=0; j < FC.length; j++) {
                    if(results[j] < min) {
                       min = results[j];
                      index = j;
                    }
                }
            }
        }

        B.markCell(FC[index].i, FC[index].j);
        return FC[index];
	}

    public String playerName() {
		return "C0mp3t1t1v3Pl4y3r";
	}


//---------------------------


    private double AlphaBeta(MNKBoard B, double a, double b, int depth) {
        double eval = 0.0;

        if(MNKGameState.WINP1 == B.gameState() ) {          //ho invertito 1 e -1 perchè il giocatore 1 dovrebbe vincere con 1
            eval = 1;
        }
        else if(MNKGameState.WINP2 == B.gameState()) {
            eval = -1;
        }
        else if(MNKGameState.DRAW == B.gameState()) {
            eval = 0;
        }
        else if(depth == 0 || B.getFreeCells().length == 0) {
            eval = evaluation(B);
        }
        else if(B.currentPlayer() == 0) {
            eval = -10;
            //crea l'array di mosse migliori
            ArrayList<MNKCell> BC = bestCells(B);
            //va per ricorsione
            for(MNKCell c : BC) { 
                B.markCell(c.i, c.j);
                eval = Math.max(eval, AlphaBeta(B, a, b, depth-1));
                a = Math.max(a, eval);
                B.unmarkCell();
                if(b <= a) break;
            }
        }
        else if(B.currentPlayer() == 1) {
            eval = 10;
            //crea l'array di mosse migliori
            ArrayList<MNKCell> BC = bestCells(B);
            //va per ricorsione
            for(MNKCell c : BC) { 
                B.markCell(c.i, c.j);
                eval = Math.min(eval, AlphaBeta(B, a, b, depth-1));
                b = Math.min(b, eval);
                B.unmarkCell();
                if(b <= a) break;
            }
        }

        return eval;
    }

    
    private double evaluation(MNKBoard B) {     //verifica solo la sequenza più lunga non se è stata già bloccata o no (futura implementazione)
        int max_p1 = 0;
        int max_p2 = 0;

        max_p1 = evaluate(B.B, MNKCellState.P1);
        max_p2 = evaluate(B.B, MNKCellState.P2);

        //if(max_p1 > max_p2) { System.out.println("p1 is winning " + max_p1 + " a " + max_p2); }
        //else if(max_p1 < max_p2) { System.out.println("p2 is winning " + max_p2 + " a " + max_p1); }
        //else if(max_p1 == max_p2) { System.out.println("draw"); }

        return (Math.atan(max_p2) - Math.atan(max_p1)) / (Math.PI / 2);  //funzione arcotangente
    }
    
    private int evaluate(MNKCellState[][] B, MNKCellState state) {
        int u_max = 0;
        int u = 0;

        //controllo delle colonne
        for(int k=0; k < N; k++) {
            for(int h=0; h < M; h++) {
                if(B[h][k] == state) {
                    u++;
                    u_max = Math.max(u, u_max);
                }
                else {
                    u = 0;
                }
            }
            u = 0;
        }

        //controllo delle righe
        for(int k=0; k < M; k++) {
            for(int h=0; h < N; h++) {
                if(B[k][h] == state) {
                    u++;
                    u_max = Math.max(u, u_max);
                    }
                else {
                    u = 0;
                }
            }
            u = 0;
        }
            
        //CONTROLLO DIAGONALE 
        for (int j = 0; j<N; j++) { // MATRICE TRIANGOLARE SUPERIORE
        	int l = 0;
        	while (l<M && j+l<N) {
        		if (l==0) {
        			u = 0;
        		}
        		if (B[l][j+l] == state) {
        			u++;
        			u_max = Math.max(u, u_max);
        		}
        		else {
        			u=0;
        		}
        		l++;
        	}
        }
        for (int i = 1; i<M; i++) { // MATRICE TRIANGOLARE INFERIORE
        	int l=0;
        	while (l<N && i+l<M) {
        		if (l==0) {
        			u=0;
        		}
        		if (B[i+l][l] == state) {
        			u++;
        			u_max = Math.max(u, u_max);
        		}
        		else {
        			u=0;
        		}
        		l++;
        	}
        }

     	// CONTROLLO ANTI-DIAGONALE
        for (int j = N; j>0; j--) { // MATRICE TRIANGOLARE SUPERIORE (ANTI)
        	int l = 0;
        	while (l<M && j-l>0) {
        		if (l==0) {
        			u=0;
        		}
        		if (B[l][j-l-1] == state) {
        			u++;
        			u_max = Math.max(u, u_max);
        		}
        		else {
        			u=0;
        		}
        		l++;
        	}
        }
        for (int i = 1; i<M; i++) { // MATRICE TRIANGOLARE INFERIORE (ANTI)
        	int l = 0;
        	while (l+i<M && N-l>0) {
        		if (l==0) {
        			u = 0;
        		}
        		if (B[i+l][N-l-1] == state) {
        			u++;
        			u_max = Math.max(u, u_max);
        		}
        		else {
        			u=0;
        		}
        		l++;
        	}
        }
        

        return u_max;
    }

    private ArrayList<MNKCell> bestCells(MNKBoard B) {
        ArrayList<MNKCell> BC = new ArrayList<>();

        
            BC.add(B.getFreeCells()[rand.nextInt(B.getFreeCells().length)]);
            BC.add(B.getFreeCells()[rand.nextInt(B.getFreeCells().length)]);
            BC.add(B.getFreeCells()[rand.nextInt(B.getFreeCells().length)]);
            BC.add(B.getFreeCells()[rand.nextInt(B.getFreeCells().length)]);
            BC.add(B.getFreeCells()[rand.nextInt(B.getFreeCells().length)]);

        return BC;
    }

}
