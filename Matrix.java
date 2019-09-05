
import java.util.*;
import java.lang.*;
import java.awt.*;

class Matrix extends Canvas
{
    String s1,s2;
    int dimX,dimY;
    MatrixCell cells[][];
    Vector alignPairs = new Vector();
    boolean linear = false;

    public Matrix(String str1, String str2) {
      s1 = str1; s2=str2;
      allocStructs();
    }

    public void setLinear(boolean lin) {
	if (lin == linear) return;
	linear = lin;
	allocStructs();
	repaint();
    }

    public void setSeqs(String str1, String str2) {
	if (str1.equals(s1) && str2.equals(s2))
	    return;

	s1 = str1; s2=str2;
	allocStructs();
	repaint();
    }

    protected void allocStructs() {
	dimX=s1.length()+1;
	dimY=s2.length()+1;

	if (linear)
	    cells = new LinearMatrixCell[dimX][dimY];
	else
	    cells = new MatrixCell[dimX][dimY];

	alignPairs.removeAllElements();

	for (int i=0; i<dimX; i++) {
	    for (int j=0; j<dimY; j++) {
		if (linear)
		    cells[i][j] = new LinearMatrixCell(this,i,j);
		else
		    cells[i][j] = new MatrixCell(this,i,j);
	    }
	}
    }

    public void clearCells() {
	for (int i=0; i<dimX; i++)
	    for (int j=0; j<dimY; j++)
		cells[i][j].reset();
    }

    public MatrixCell getCell(int i,int j) { return cells[i][j]; };
    public int getCol(int i, int j) { return cells[i][j].getCol(); };
    public void setCol(int i, int j, int c) { cells[i][j].setCol(c); };

    public int getVal(int i, int j) { return cells[i][j].getVal(); };
    public void setVal(int i, int j, int v, int c) { cells[i][j].setVal(v,c); };

    public void clearAlignment() { 
	//	alignPairs.clear(); 
	alignPairs.removeAllElements();
    }
    public void addAlignment(int i1, int j1, int i2, int j2) {
	alignPairs.addElement(new Rectangle(i1,j1,i2,j2));
    }

    FontMetrics fm;
    double fHeight, fWidth;
    int row1=0, col1=0;
    int cellSpacing = 1;

    Dimension getCellSize() {
	int width  = getWidth();
	int height = getHeight();
	double Cw = (width -col1)/dimY - cellSpacing;
	double Ch = (height-row1)/dimX - cellSpacing;
	return new Dimension((int)Cw, (int)Ch);
    }

    public void changed(int i, int j) {
	Dimension cdim = getCellSize();
	int x = col1 + j * (cdim.width +cellSpacing);
	int y = row1 + i * (cdim.height+cellSpacing);
	repaint(x,y,cdim.width,cdim.height);
    }

    public void paint(Graphics g) {
	if (fm == null) {
	    fm = g.getFontMetrics();
	    fHeight = fm.getHeight();
	    fWidth  = fm.stringWidth("X");
	    row1 = (int)(fHeight*2);
	    col1 = (int)(fWidth*2);
	}

	Dimension cdim = getCellSize();
	Rectangle clip = g.getClipBounds();

	// Draw any cells that need to be redrawn
	for (int i=0; i<dimX; i++)
	    for (int j=0; j<dimY; j++) {
		int x = col1 + j * (cdim.width +cellSpacing);
		int y = row1 + i * (cdim.height+cellSpacing);
		if (clip==null || clip.intersects(new Rectangle(x,y,cdim.width,cdim.height)))
		    cells[i][j].paint(g,x,y,cdim.width,cdim.height);
	    }

	/*
	// Draw the alignment
	for (int i=0; i<alignPairs.size(); i++) {
	    Rectangle r = (Rectangle)alignPairs.elementAt(i);
	    g.drawLine(col1 + r.y * (cdim.width +cellSpacing)      + (int)(cdim.width/2),
		       row1 + r.x * (cdim.height+cellSpacing)      + (int)(cdim.height/2),
		       col1 + r.height * (cdim.width +cellSpacing) + (int)(cdim.width/2),
		       row1 + r.width  * (cdim.height+cellSpacing) + (int)(cdim.height/2)
		       );
	}
	*/

	// Draw the sequences
	g.setColor(Color.black);
	for (int i=0; i<s1.length(); i++) {
	    g.drawString(""+s1.charAt(i), 0, (int)(row1 + fHeight/2 + (i+1)*(cdim.height+cellSpacing)));
	}

	for (int j=0; j<s2.length(); j++) {
	    g.drawString(""+s2.charAt(j), (int)(col1 - fWidth/2 + (j+1)*(cdim.width+cellSpacing)), (int)(fHeight*1.5));
	}
	    
    }

    //public void update(Graphics g) {
	//paint(g);
    //}
    


    //    public void paint(Graphics g) { 
    //	System.out.println("Paint matrix.  w="+getWidth()+" h="+getHeight());
	//for (int i=0; i<dimX; i++)
	//    for (int j=0; j<dimY; j++)
	//	cells[i][j].paint(g);
    //    }

    // getWidth/getHeight methods here so works with IBM JIT which is for 1.1.8 not 1.2
    public int getWidth()  { return getSize().width; };
    public int getHeight() { return getSize().height; };
}

