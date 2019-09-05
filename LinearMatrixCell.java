
import java.lang.*;
import java.awt.*;

class LinearMatrixCell extends MatrixCell
{
    int vals[] = {bigInt,bigInt,bigInt};
    int fWidth;
    int width=-1,height=-1;
    double xpos=0,ypos=0;

    public LinearMatrixCell(Matrix m, int i,int j) {
	super(m,i,j);
    };

    public void reset() {
	boolean repaint=false;
	if (col!=0 || vals[0]!=bigInt || vals[1]!=bigInt || vals[2]!=bigInt)
	    repaint=true;
	col=0;
	vals[0]=bigInt;
	vals[1]=bigInt;
	vals[2]=bigInt;
	if (repaint && !need_repaint) { need_repaint=true; matrix.changed(i,j); }
    }

    public int  getVal()             { System.err.println("ERROR: Called bad getVal in LinearMatrixCell"); return 0;};
    public void setVal(int a, int b) { System.err.println("ERROR: Called bad setVal in LinearMatrixCell"); };

    public int getVal(int d) { return vals[d]; };
    public void setVal(int d, int v, int c) {
	//if (vals[d] != 0 && vals[d] != v) {
	//    System.err.println("Changing value in cell! old val="+val+"  new val="+v);
	//}
	boolean repaint = ((vals[d]!=v || col!=c) ? true : false);
	vals[d] = v;
	col = c;
	if (repaint && !need_repaint) { need_repaint=true; matrix.changed(i,j); }
    }

    public void paint(Graphics g, int x, int y, int w, int h) {
	//System.out.println("paint("+i+","+j+")="+vals+"  width="+getWidth()+" height="+getHeight());

	need_repaint=false;

	if (w != width || h != height) {
	    // Changed since we last calculted these...
	    width  = w;
	    height = h;
	    xpos = (width/2  - matrix.fWidth)/2;
	    ypos = (height/2 - matrix.fHeight)/2 + matrix.fHeight;
	}

	g.setColor(cols[Math.min(col,maxCol-1)]);
	g.fillRect(x, y, width, height);

	if (width<matrix.fWidth*2 || height<2*matrix.fHeight) return; // Don't draw number if too small

	//System.out.println("fw="+fWidth+" fh="+fHeight+" w="+width+" h="+height+" xpos="+xpos+" ypos="+ypos);

	g.setColor(Color.white);
	String v1 = (vals[0]>1000 ? "" : vals[0]+"");
	String v2 = (vals[1]>1000 ? "" : vals[1]+"");
	String v3 = (vals[2]>1000 ? "" : vals[2]+"");
	g.drawString(v1+"", (int)(x+xpos), (int)(y+ypos));
	g.drawString(v2+"", (int)(x+xpos + width/2), (int)(y+ypos));
	g.drawString(v3+"", (int)(x+xpos), (int)(y+ypos + height/2));
    }
}
