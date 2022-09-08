package mnkgame;

import java.util.Random;

import javax.swing.text.html.HTMLDocument.BlockElement;

import java.util.ArrayList;
import java.util.Collections;

public class ObserverPlayer  implements MNKPlayer {
	private Random rand;
    private int M;
    private int N;
    private int K;
	private MNKBoard B;
	private MNKGameState myWin;
	private MNKGameState yourWin;
	private int TIMEOUT;
	private boolean first;

	public ObserverPlayer() {
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
		this.first = first;
	}

	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        
		if(MC.length > 0) {
			MNKCell c = MC[MC.length-1]; 
			B.markCell(c.i,c.j);         
		}

        //evaluate(B.B, !first ? MNKCellState.P1 : MNKCellState.P2);
        System.out.println(evaluate(B.B, !first ? MNKCellState.P1 : MNKCellState.P2));  //osserva l'avversario

		MNKCell c = FC[rand.nextInt(FC.length)];
		B.markCell(c.i,c.j);
		return c;
	}

	public String playerName() {
		return "observer";
	}

/*
    private void evaluation(MNKBoard B) {     //verifica solo la sequenza più lunga non se è stata già bloccata o no (futura implementazione)
        int max_p1 = 0;
        int max_p2 = 0;

        max_p1 = evaluate(B.B, MNKCellState.P1);
        max_p2 = evaluate(B.B, MNKCellState.P2);

        if(max_p1 > max_p2) { System.out.println("p1 is winning " + max_p1 + " a " + max_p2); }
        else if(max_p1 < max_p2) { System.out.println("p2 is winning " + max_p2 + " a " + max_p1); }
        else if(max_p1 == max_p2) { System.out.println("draw"); }

    }
*/   
   
    private double evaluate(MNKCellState[][] B, MNKCellState state) {
        double max_rows = evaluateRows(B, state);
        double max_cols = evaluateCols(B, state);
        double max_diags = evaluateDiags(B, state);
        double max_antidiags = evaluateAntiDiags(B, state);

		return Math.max(Math.max(max_rows, max_cols),Math.max(max_diags, max_antidiags));
    }

    private double evaluateRows(MNKCellState[][] B, MNKCellState state) {
        ArrayList<Streak> streak_max = new ArrayList<Streak>();
        Streak s = new Streak();
        streak_max.add(s);
        int h = 0;  //contatore per streak_max
        boolean flag;

        for(int i=0; i < M; i++) {
            flag = false;
            for(int j=0; j < N; j++) {
                if(B[i][j] == state) {
                    if(flag) 
                        flag = false;
                    if(streak_max.get(h).count == 0) {
                        if(j-1 >= 0 && B[i][j-1] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m1 = 1;
                            streak_max.set(h, z);
                        }
                    }
                    Streak z = streak_max.get(h);
                    z.count++;
                    streak_max.set(h, z);
                }
                else if(B[i][j] == MNKCellState.FREE) {
                    if(!flag){
                        flag = true;
                        if(streak_max.get(h).count > 0) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        if(j+1 < N && B[i][j+1] == state) {
                            Streak z = streak_max.get(h);
                            z.hole = true;
                            streak_max.set(h, z);
                        }
                    } 
                    else {
                        if(streak_max.get(h).count > 0) {
                            Streak w = new Streak();
                            streak_max.add(w);
                            h += 1;
                        }
                    }
                }
                else {
                    if(flag) 
                        flag = false;
                    if(streak_max.get(h).count > 0) {
                        if(j+1 < N && B[i][j+1] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        Streak w = new Streak();
                        streak_max.add(w);
                        h += 1;
                    }
                }
            }
            if(streak_max.get(h).count > 0) {
                Streak w = new Streak();
                streak_max.add(w);
                h += 1;
            }
        }

        double max = 0;
        for(int k = 0; k < streak_max.size(); k++){
            if(streak_max.get(k).count >= K)
                max = streak_max.get(k).count;
            else if(streak_max.get(k).m1 != 0.7 || streak_max.get(k).m2 != 0.7) {
                if(max < streak_max.get(k).count*streak_max.get(k).m1*streak_max.get(k).m2) 
                    max = streak_max.get(k).count*streak_max.get(k).m1*streak_max.get(k).m2;
            }
        }
        return max;
    }

    private double evaluateCols(MNKCellState[][] B, MNKCellState state) {
        ArrayList<Streak> streak_max = new ArrayList<Streak>();
        Streak s = new Streak();
        streak_max.add(s);
        int h = 0;  //contatore per streak_max
        boolean flag;

        for(int j=0; j < N; j++) {
            flag = false;
            for(int i=0; i < M; i++) {
                if(B[i][j] == state) {
                    if(flag) 
                    flag = false;
                    if(streak_max.get(h).count == 0) {
                        if(i-1 >= 0 && B[i-1][j] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m1 = 1;
                            streak_max.set(h, z);
                        }
                    }
                    Streak z = streak_max.get(h);
                    z.count++;
                    streak_max.set(h, z);
                }
                else if(B[i][j] == MNKCellState.FREE) {
                    if(!flag){
                        flag = true;
                        if(streak_max.get(h).count > 0) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        if(i+1 < M && B[i+1][j] == state) {
                            Streak z = streak_max.get(h);
                            z.hole = true;
                            streak_max.set(h, z);
                        }
                    } 
                    else {
                        if(streak_max.get(h).count > 0) {
                            Streak w = new Streak();
                            streak_max.add(w);
                            h += 1;
                        }
                    }
                }
                else {
                    if(flag) 
                        flag = false;
                    if(streak_max.get(h).count > 0) {
                        if(i+1 < M && B[i+1][j] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        Streak w = new Streak();
                        streak_max.add(w);
                        h += 1;
                    }
                }
            }
            if(streak_max.get(h).count > 0) {
                Streak w = new Streak();
                streak_max.add(w);
                h += 1;
            }
        }
    
        double max = 0;
        for(int k = 0; k < streak_max.size(); k++){
            if(streak_max.get(k).count >= K)
                max = streak_max.get(k).count;
            else if(streak_max.get(k).m1 != 0.7 || streak_max.get(k).m2 != 0.7) {
                if(max < streak_max.get(k).count*streak_max.get(k).m1*streak_max.get(k).m2) 
                    max = streak_max.get(k).count*streak_max.get(k).m1*streak_max.get(k).m2;
            }
        }
        return max;
    }

    private double evaluateDiags(MNKCellState[][] B, MNKCellState state) {
        ArrayList<Streak> streak_max = new ArrayList<Streak>();
        Streak s = new Streak();
        streak_max.add(s);
        int h = 0;  //contatore per streak_max
        boolean flag;

        //triangolare superiore
        for(int j=0; j < N; j++) {
            flag = false;
            for(int l=0; (l<M && j+l<N); l++) {
                if(B[l][j+l] == state) {
                    if(flag) 
                    flag = false;
                    if(streak_max.get(h).count == 0) {
                        if((l-1>=0 && j+l-1>=0) && B[l-1][j+l-1] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m1 = 1;
                            streak_max.set(h, z);
                        }
                    }
                    Streak z = streak_max.get(h);
                    z.count++;
                    streak_max.set(h, z);
                }
                else if(B[l][j+l] == MNKCellState.FREE) {
                    if(!flag){
                        flag = true;
                        if(streak_max.get(h).count > 0) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        if((l+1<M && j+l+1<N) && B[l+1][j+l+1] == state) {
                            Streak z = streak_max.get(h);
                            z.hole = true;
                            streak_max.set(h, z);
                        }
                    } 
                    else {
                        if(streak_max.get(h).count > 0) {
                            Streak w = new Streak();
                            streak_max.add(w);
                            h += 1;
                        }
                    }
                }
                else {
                    if(flag) 
                        flag = false;
                    if(streak_max.get(h).count > 0) {
                        if((l+1<M && j+l+1<N) && B[l+1][j+l+1] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        Streak w = new Streak();
                        streak_max.add(w);
                        h += 1;
                    }
                }
            }
            if(streak_max.get(h).count > 0) {
                Streak w = new Streak();
                streak_max.add(w);
                h += 1;
            }
        }
        //triangolare inferiore
        for(int i=1; i < M; i++) {
            flag = false;
            for(int l=0; (l<N && i+l<M); l++) {
                if(B[i+l][l] == state) {
                    if(flag) 
                    flag = false;
                    if(streak_max.get(h).count == 0) {
                        if((i+l-1>=0 && l-1>=0) && B[i+l-1][l-1] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m1 = 1;
                            streak_max.set(h, z);
                        }
                    }
                    Streak z = streak_max.get(h);
                    z.count++;
                    streak_max.set(h, z);
                }
                else if(B[i+l][l] == MNKCellState.FREE) {
                    if(!flag){
                        flag = true;
                        if(streak_max.get(h).count > 0) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        if((i+l+1<M && l+1<N) && B[i+l+1][l+1] == state) {
                            Streak z = streak_max.get(h);
                            z.hole = true;
                            streak_max.set(h, z);
                        }
                    } 
                    else {
                        if(streak_max.get(h).count > 0) {
                            Streak w = new Streak();
                            streak_max.add(w);
                            h += 1;
                        }
                    }
                }
                else {
                    if(flag) 
                        flag = false;
                    if(streak_max.get(h).count > 0) {
                        if((i+l+1<M && l+1<N) && B[i+l+1][l+1] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        Streak w = new Streak();
                        streak_max.add(w);
                        h += 1;
                    }
                }
            }
            if(streak_max.get(h).count > 0) {
                Streak w = new Streak();
                streak_max.add(w);
                h += 1;
            }
        }

        double max = 0;
        for(int k = 0; k < streak_max.size(); k++){
            if(streak_max.get(k).count >= K)
                max = streak_max.get(k).count;
            else if(streak_max.get(k).m1 != 0.7 || streak_max.get(k).m2 != 0.7) {
                if(max < streak_max.get(k).count*streak_max.get(k).m1*streak_max.get(k).m2) 
                    max = streak_max.get(k).count*streak_max.get(k).m1*streak_max.get(k).m2;
            }
        }
        return max;
    }

    private double evaluateAntiDiags(MNKCellState[][] B, MNKCellState state) {
        ArrayList<Streak> streak_max = new ArrayList<Streak>();
        Streak s = new Streak();
        streak_max.add(s);
        int h = 0;  //contatore per streak_max
        boolean flag;

        //triangolare superiore
        for(int j=N; j > 0; j--) {
            flag = false;
            for(int l=0; (l < M && j-l > 0); l++) {
                if(B[l][j-l-1] == state) {
                    if(flag) 
                    flag = false;
                    if(streak_max.get(h).count == 0) {
                        if((l-1>=0 && j-l<N) && B[l-1][j-l] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m1 = 1;
                            streak_max.set(h, z);
                        }
                    }
                    Streak z = streak_max.get(h);
                    z.count++;
                    streak_max.set(h, z);
                }
                else if(B[l][j-l-1] == MNKCellState.FREE) {
                    if(!flag){
                        flag = true;
                        if(streak_max.get(h).count > 0) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        if((l+1<M && j-l-2>0) && B[l+1][j-l-2] == state) {
                            Streak z = streak_max.get(h);
                            z.hole = true;
                            streak_max.set(h, z);
                        }
                    } 
                    else {
                        if(streak_max.get(h).count > 0) {
                            Streak w = new Streak();
                            streak_max.add(w);
                            h += 1;
                        }
                    }
                }
                else {
                    if(flag) 
                        flag = false;
                    if(streak_max.get(h).count > 0) {
                        if((l+1<M && j-l-2>0) && B[l+1][j-l-2] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        Streak w = new Streak();
                        streak_max.add(w);
                        h += 1;
                    }
                }
            }
            if(streak_max.get(h).count > 0) {
                Streak w = new Streak();
                streak_max.add(w);
                h += 1;
            }
        }
        //triangolare inferiore
        for(int i=1; i < M; i++) {
            flag = false;
            for(int l=0; (l+i < M && N-l > 0); l++) {
                if(B[i+l][N-l-1] == state) {
                    if(flag) 
                    flag = false;
                    if(streak_max.get(h).count == 0) {
                        if((i+l-1>=0 && N-l<N) && B[i+l-1][N-l] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m1 = 1;
                            streak_max.set(h, z);
                        }
                    }
                    Streak z = streak_max.get(h);
                    z.count++;
                    streak_max.set(h, z);
                }
                else if(B[i+l][N-l-1] == MNKCellState.FREE) {
                    if(!flag){
                        flag = true;
                        if(streak_max.get(h).count > 0) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        if((i+l+1<M && N-l-2>=0) && B[i+l+1][N-l-2] == state) {
                            Streak z = streak_max.get(h);
                            z.hole = true;
                            streak_max.set(h, z);
                        }
                    } 
                    else {
                        if(streak_max.get(h).count > 0) {
                            Streak w = new Streak();
                            streak_max.add(w);
                            h += 1;
                        }
                    }
                }
                else {
                    if(flag) 
                        flag = false;
                    if(streak_max.get(h).count > 0) {
                        if((i+l+1<M && N-l-2>=0) && B[i+l+1][N-l-2] == MNKCellState.FREE) {
                            Streak z = streak_max.get(h);
                            z.m2 = 1;
                            streak_max.set(h, z);
                        }
                        Streak w = new Streak();
                        streak_max.add(w);
                        h += 1;
                    }
                }
            }
            if(streak_max.get(h).count > 0) {
                Streak w = new Streak();
                streak_max.add(w);
                h += 1;
            }
        }

        double max = 0;
        for(int k = 0; k < streak_max.size(); k++){
            if(streak_max.get(k).count >= K)
                max = streak_max.get(k).count;
            else if(streak_max.get(k).m1 != 0.7 || streak_max.get(k).m2 != 0.7) {
                if(max < streak_max.get(k).count*streak_max.get(k).m1*streak_max.get(k).m2) 
                    max = streak_max.get(k).count*streak_max.get(k).m1*streak_max.get(k).m2;
            }
        }
        return max;
    }

}
