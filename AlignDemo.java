// David Powell 2001.
// Simple applet for demoing some 2d alignment algorirthms.

import java.lang.*;
import java.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

//-----------------------------------------------------------------------------

public class AlignDemo extends Applet 
{
    Matrix mat;
    AlignControls controls;

    public void init() {
	setLayout(new BorderLayout());
	setFont(new Font("Courier", Font.PLAIN, 12));

	mat = new Matrix(new String("foo"), new String("bar"));
	controls = new AlignControls(mat);

	add("North", controls);
	add("Center", mat);
    }//init

    public void processEvent(AWTEvent e) {
        if (e.getID() == Event.WINDOW_DESTROY) {
            System.exit(0);
        }
    }

    public static void main(String args[]) {
	Frame f = new Frame("Sequence Alignment");
	AlignDemo t = new AlignDemo();
	
	f.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {System.exit(0);}
	    });

	t.init();
	t.start();
	
	f.add("Center", t);
	f.setSize(500, 500);
	f.show();
    }
    

 }//class


class AlignControls extends Panel implements ActionListener, AdjustmentListener 
{
    TextField s1,s2;
    Choice alg;
    Scrollbar algSpeed;
    volatile TextArea txtStatus;
    Label lblPause;

    Matrix mat;
    AlignAlgorithm runner;

    AlignControls() { return; }

    AlignControls(Matrix m) {
	GridBagLayout gridBag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	
	setLayout(gridBag);

	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.gridwidth = 1;

	s1 = new TextField("ACGTGTACGT", 20);
	s2 = new TextField("AGTCTACCGT", 20);
	gridBag.setConstraints(s1, c);
	gridBag.setConstraints(s2, c);
	add(s1);
	add(s2);
	
	alg = new Choice();
	alg.add("Standard DPA");
	alg.add("Lazy DPA");
	alg.add("Ukkonen Algorithm");
	alg.add("Hirschberg");
	alg.add("Linear DPA");
	//alg.add("Lazy Linear DPA");
	alg.add("Ukkonen Linear");
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	gridBag.setConstraints(alg, c);
	add(alg);

	
	Button butGo = new Button("Go");
	butGo.addActionListener(this);
	c.gridwidth = 1;
	gridBag.setConstraints(butGo, c);
	add(butGo);

	Button butStop = new Button("Stop");
	butStop.addActionListener(this);
	gridBag.setConstraints(butStop, c);
	add(butStop);

	algSpeed = new Scrollbar(Scrollbar.HORIZONTAL, 100, 1, 0, 400);
	algSpeed.addAdjustmentListener(this);
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	gridBag.setConstraints(algSpeed, c);
	add(algSpeed);


	add(new Label("Status:"));
	txtStatus = new TextArea("",4,40,TextArea.SCROLLBARS_NONE);
	txtStatus.setEditable(false);
	c.gridwidth = 1;
	c.gridheight = 4;
	gridBag.setConstraints(txtStatus, c);
	add(txtStatus);

	lblPause = new Label("Pause " + algSpeed.getValue() + " ms");
	c.gridwidth = GridBagConstraints.REMAINDER; //end row;
	c.gridheight = 1;
	gridBag.setConstraints(lblPause, c);
	add(lblPause);

	mat = m;
	mat.setSeqs(s1.getText(), s2.getText());
    }

    public void stopAlg() {
	while( runner!=null && runner.isAlive() ) {
	    runner.quit = true;
	}
	runner = null;
    }

    public void adjustmentValueChanged(AdjustmentEvent ev) {
	lblPause.setText("Pause " + algSpeed.getValue() + " ms");
	if (runner!=null && runner.isAlive())
	    runner.setPause(ev.getValue());
    }

    public void actionPerformed(ActionEvent ev) {
	String label = ev.getActionCommand();

	if (label.equalsIgnoreCase("stop")) {
	    stopAlg();
	    return;
	}

	stopAlg();

	mat.setSeqs(s1.getText(), s2.getText());

	if (false) {
	    runner = new DPA(s1.getText(),s2.getText(),mat);
	    runner.setPause(0);
	    runner.go();
	    runner.traceBack();
	}

	mat.clearCells();

	switch (alg.getSelectedIndex()) {
	case 0:
	    runner = new DPA(s1.getText(),s2.getText(),mat);
	    break;
	case 1:
	    runner = new LazyDPA(s1.getText(),s2.getText(),mat);
	    break;
	case 2:
	    runner = new Ukkonen(s1.getText(),s2.getText(),mat);
	    break;
	case 3:
	    runner = new Hirschberg(s1.getText(),s2.getText(),mat);
	    break;
	case 4:
	    runner = new LinearDPA(s1.getText(),s2.getText(),mat);
	    break;
	case 5:
/*
	    runner = new LazyLinearDPA(s1.getText(),s2.getText(),mat);
	    break;
	case 6:
*/
	    runner = new UkkLinear(s1.getText(),s2.getText(),mat);
	    break;
	}

	runner.setStatusWin(txtStatus);
	runner.setPause(algSpeed.getValue());
	runner.start();

	//a.go();
	//System.out.println("editCost="+a.editCost());
	//System.out.println(a.traceBack());
    }
}

//-----------------------------------------------------------------------------

class DPA extends AlignAlgorithm
{
    static final int colour = 1;
    DPA(String str1, String str2, Matrix m) {
	super(str1,str2,m);
	m.setLinear(false);
    }

    public String algName() { return "DPA"; }

    public void go() {
	mat.getCell(0,0).setVal(0,colour);
	cellsComputed++;
	pause();

	for (int j=1; j<s2.length()+1; j++) {
	    if (quit) return;
	    mat.getCell(0,j).setVal( mat.getCell(0,j-1).getVal()+1, colour);
	    cellsComputed++;
	    pause();
	}
	    
	for (int i=1; i<s1.length()+1; i++) {
	    mat.setVal(i, 0, mat.getVal(i-1,0)+1, colour);
	    cellsComputed++;
	    pause();
	    for (int j=1; j<s2.length()+1; j++) {
		if (quit) return;
		mat.getCell(i,j).setVal( min3( mat.getCell(i-1,j).getVal() + 1,       //delete
					       mat.getCell(i,j-1).getVal() + 1,       //insert
					       mat.getCell(i-1,j-1).getVal() +        //match/mismatch
					       (s1.charAt(i-1) == s2.charAt(j-1) ? 0 : 1)),
			   colour);
		cellsComputed++;
		pause();
	    }
	}
	editCost = mat.getCell(s1.length(),s2.length()).getVal();
    }

    // traceBack the alignment from the 'mat' matrix.
    // return a string containing the alignment, 
    // and draw the alignment on the 'mat' matrix.
    public String traceBack() {
	if (editCost<0)
	    go();

	int i=s1.length();
	int j=s2.length();
	mat.clearAlignment();
	int lastI=i, lastJ=j;
	StringBuffer res1 = new StringBuffer(),res2=new StringBuffer();

	while (i!=0 || j!=0) {
	    if (quit) return "";
	    int v=mat.getCell(i,j).getVal();
	    if (i>0 && v == mat.getCell(i-1,j).getVal()+1) {
		//delete
		res1.append(s1.charAt(i-1));
		res2.append("-");
		i--;
	    } else if (j>0 && v == mat.getCell(i,j-1).getVal()+1) {
		//insert
		res1.append("-");
		res2.append(s2.charAt(j-1));
		j--;
	    } else if (i>0 && j>0 && (v == mat.getCell(i-1,j-1).getVal()+
			       (s1.charAt(i-1) == s2.charAt(j-1) ? 0 : 1))) {
		// Diagonal
		res1.append(s1.charAt(i-1));
		res2.append(s2.charAt(j-1));
		i--;
		j--;
	    } else {
		System.err.println("DPA trace back failed! at ("+i+","+j+")");
		System.exit(-1);
	    }
	    mat.addAlignment(lastI, lastJ, i,j);
	    lastI=i;
	    lastJ=j;
	    pause();
	}
	res1.reverse();
	res2.reverse();
	res1.append("\n"+res2);
	return res1.toString();
    }
}


class Ukkonen extends AlignAlgorithm
{
    static final int col1 = 3;
    static final int col2 = 2;

    int finalDiag;
    int U[][];
    int computed[][];
    int abOff;

    Ukkonen(String str1, String str2, Matrix m) {
	super(str1,str2,m);
	m.setLinear(false);

	finalDiag = s1.length()-s2.length();
	abOff = s2.length();

	U        = new int[s1.length()+s2.length()+1][s1.length()+s2.length()+1];
	computed = new int[s1.length()+s2.length()+1][s1.length()+s2.length()+1];
    }

    public String algName() { return "Ukkonen"; }

    public void go() {
	editCost=0;
	while( calcCell(finalDiag, editCost) < s1.length()) {
	    editCost++;
	    if (quit) {
		editCost=-1;
		return;
	    }
	}
    }

    public int calcCell(int ab, int d) {
	if (quit) return -infinity;
	if (ab==0 && d==-1) return -1; // Base case
	if (d<0 || Math.abs(ab)>d) return -infinity; // Outside range
	if (computed[ab+abOff][d]==1) return U[ab+abOff][d]; // Already computed it
	
	int p = max3(calcCell(ab+1, d-1),
		     calcCell(ab,   d-1)+1,
		     calcCell(ab-1, d-1)+1);

	if (quit) return -infinity;

	mat.getCell(p,p-ab).setVal(d, col2);
	cellsComputed++;
	pause();
	
	while (p<s1.length() && p-ab<s2.length() &&
	       s1.charAt(p) == s2.charAt(p-ab)) {
	    p++;
	    mat.getCell(p,p-ab).setVal(d,col2);
	    cellsComputed++;
	    pause();
	}

	U[ab+abOff][d] = p;
	computed[ab+abOff][d] = 1;
	//System.out.println("U[ab="+ab+"][d="+d+"]="+p);
	return p;
    }
}
