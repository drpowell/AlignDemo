
import java.awt.*;
import java.lang.*;

abstract class AlignAlgorithm extends Thread
{
    static final int infinity = 10000000;
    Matrix mat;
    String s1,s2;
    int editCost;
    int cellsComputed;
    int pause=0;
    volatile boolean quit=false;
    TextArea status;

    AlignAlgorithm(String str1, String str2, Matrix m) {
	mat = m;
	s1 = str1;
	s2 = str2;
	editCost = -1;
	cellsComputed = 0;
	
	if (s1.length() != m.dimX-1 || 
	    s2.length() != m.dimY-1) {
	    System.err.println("Bad matrix size");
	    System.exit(911);
	}
    }

    public abstract String algName();

    public void setStatusWin(TextArea l) {
	status = l;
	status.setText(algName() + " not started.");
    }

    public synchronized void setPause(int p) { pause=p; }

    protected void pause() {
	if (quit) return;
	if (pause==0) return;

	try {
	    Thread.sleep(pause);
	} catch (InterruptedException e) {
	}
    }

    public void run() {
	status.setText(algName() + " running.");
	go();

	if (quit) {
	    status.setText(algName() + " stopped.");
	} else {
	    status.setText(algName() + " done. Cost="+editCost+"\n");
	    status.append(traceBack());
	    status.append("\n"+"Cells computed = "+cellsComputed);
	}
    }

    final int min2(int a, int b) { return (a<b ? a : b); };
    final int max2(int a, int b) { return (a>b ? a : b); };

    final int min3(int a, int b, int c) { return (a<b ? (a<c ? a : c) : (b<c ? b : c)); }
    final int max3(int a, int b, int c) { return (a>b ? (a>c ? a : c) : (b>c ? b : c)); }

    public int editCost() {
	if (editCost<0)
	    go();
	return editCost;
    }

    public String traceBack() {
	return "traceBack not implemented yet for this algorithm";
    }

    abstract public void go();

}

