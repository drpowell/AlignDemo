
import java.lang.*;
import java.awt.*;

class MatrixCell
{
    static final int bigInt = 100000;
    static final int maxCol = 5;
    static final Color cols[] = {
	new Color((float)0,  (float)0,  (float)0),
	new Color((float)0.4,(float)0,  (float)0),
	new Color((float)0,  (float)0.4,(float)0),
	new Color((float)0,  (float)0,  (float)0.4),
	new Color((float)0.7,  (float)0.7,  (float)0),
    };
    protected int col;
    protected int val;

    int i,j;
    Matrix matrix;
    boolean need_repaint;

    public MatrixCell(Matrix m, int i,int j) {
	col = 0;
	val = bigInt;
	this.i = i;
	this.j = j;
	matrix = m;
    };

    public void reset() { 
	boolean repaint = ((col!=0 || val!=bigInt) ? true : false);
	col=0;
	val=bigInt; 
	if (repaint && !need_repaint) { need_repaint=true; matrix.changed(i,j); }
    }

    public int getCol() { return col; };
    public void setCol(int c) { 
	boolean repaint = (c!=col ? true : false);
	col=c; 
	if (repaint && !need_repaint) { need_repaint=true; matrix.changed(i,j); }
    };

    public int getVal() { return val; };
    public void setVal(int v, int c) {
	if (val != bigInt && val != v) {
	    System.err.println("Changing value in cell! old val="+val+"  new val="+v);
	}
	boolean repaint = ((val!=v || col!=c) ? true : false);
	val = v;
	col = c;
	if (repaint && !need_repaint) { need_repaint=true; matrix.changed(i,j); }
    }

    public void paint(Graphics g, int x, int y, int width, int height) {
	//System.out.println("paint("+i+","+j+")="+val+"  width="+getWidth()+" height="+getHeight());

	need_repaint=false;

	g.setColor(cols[Math.min(col,maxCol-1)]);
	g.fillRect(x, y, width, height);

	if (width<10 || height<matrix.fHeight-4) return; // Don't draw number if too small

	g.setColor(Color.white);
	String s = (val>1000 ? "" : ""+val);
	int w = matrix.fm.stringWidth(s);
	g.drawString(s, (int)(x + (width-w)/2), (int)(y + height/2 + matrix.fHeight/4));
    }
}
