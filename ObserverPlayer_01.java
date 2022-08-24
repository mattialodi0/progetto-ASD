package mnkgame;

import java.util.Random;

public class ObserverPlayer_01  implements MNKPlayer {
	private Random rand;
    private int M;
    private int N;
    private int K;
	private MNKBoard B;
	private MNKGameState myWin;
	private MNKGameState yourWin;
	private int TIMEOUT;

	public ObserverPlayer_01() {
	}


	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		// New random seed for each game
		rand    = new Random(System.currentTimeMillis()); 
		// Save the timeout for testing purposes
        this.M = M;
        this.N = N;
        this.K = K;
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
            MNKCell c = new MNKCell(m, n);
			B.markCell(c.i,c.j);
            return c;
        }

        evaluation(B);

		if(FC.length == 1) {
            return FC[0];
        }
        else if(MC.length == 1) {
            //se è la seconda mossa mette il simbolo accanto a quello dell'avversario
			int m; int n;
			MNKCell c = MC[0];
			if(c.i < M-1) {
				m = c.i+1;
			}
			else {
				m = c.i-1;
			}
			if(c.j < N-1) {
				n = c.j+1;
			}
			else {
				n = c.j-1;
			}
			MNKCell f = new MNKCell(m, n);
			B.markCell(f.i,f.j);
            return f;
        }

		//se può vincere lo fa
		for(MNKCell d : FC) {
			if(B.markCell(d.i,d.j) == myWin) {
				return d;  
			} else {
				B.unmarkCell();
			}
		}
	

        //se l'altro giocatore può vincere alla prossima mossa lo ferma
		int pos = rand.nextInt(FC.length); 
		MNKCell c = FC[pos]; // mossa a caso
		B.markCell(c.i,c.j); 
		for(int k = 0; k < FC.length; k++) {    
			if(k != pos) {     
			    MNKCell d = FC[k];
			    if(B.markCell(d.i,d.j) == yourWin) {
			    	B.unmarkCell();        
			    	B.unmarkCell();	       
			    	B.markCell(d.i,d.j);   
			    	return d;							 
			    } else {
			    	B.unmarkCell();	       
			    }	
			}
		}	
		return c;
	}

	public String playerName() {
		return "5m4rt";
	}

    private void evaluation(MNKBoard B) {     //verifica solo la sequenza più lunga non se è stata già bloccata o no (futura implementazione)
        int max_p1 = 0;
        int max_p2 = 0;

        max_p1 = evaluate(B.B, MNKCellState.P1);
        max_p2 = evaluate(B.B, MNKCellState.P2);

        if(max_p1 > max_p2) { System.out.println("p1 is winning"); }
        else if(max_p1 < max_p2) { System.out.println("p2 is winning"); }
        else if(max_p1 == max_p2) { System.out.println("draw"); }

    }
    
    private int evaluate(MNKCellState[][] B, MNKCellState state) {
        int u_max = 0;
        int u = 0;

        //controllo delle colonne
        for(int k=0; k < N; k++) {
            for(int h=0; h < M; h++) {
                if(B[h][k] == state) {
                    u++;
		    if (u_max < u) u_max=u;	
                }
                else {
		    //if (u_max < u) u_max=u;                 

		    //u_max = (u_max > u ? u_max : u);
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
		    if (u_max < u) u_max=u;
                }
                else {
		    //if (u_max < u) u_max=u;

                    //u_max = (u_max > u ? u_max : u);
                    u = 0;
                }
            }
            u = 0;
        }
/*
        //controllo delle diagonali 
        for(int k=0; k < M; k++) {
            for(int h=0; h < (M < N ? M : N); h++) {
                if(k+h < N) {
                    if(B[k+h][h] == state) {
                        u++;
                    }
                    else {
                        u_max = (u_max > u ? u_max : u);
                        u = 0;
                    }
                }
            }
            u = 0;
        }
        for(int k=1; k < N; k++) {
            for(int h=0; h < (M < N ? M : N); h++) {
                if(k+h < N-k+1) {
                    if(B[h][h+k] == state) {
                        u++;
                    }
                    else {
                        u_max = (u_max > u ? u_max : u);
                        u = 0;
                    }
                }
            }
            u = 0;
        }*/
        //controllo delle antidiagonali

        return u_max;
    }
}