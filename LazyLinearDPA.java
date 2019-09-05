
/** LazyLinearDPA attempts to implement an O(n.d) dpa style algorithm for 
    linear gap costs.  While this algorithm does compute O(n.d) cells of the matrix, 
    I don't think it is actually O(n.d).  The complexity is probably quite horrid, 
    although the actual matrix is not needed.
*/

import java.util.*;

class LazyLinearDPA extends AlignAlgorithm
{
    static final int col1 = 3;
    static final int col2 = 2;
    static final int diag = 0, vert=1, horz=2;
    static final int matchCost=0, misCost=1, startGap=3, contGap=1;    
    
    class CellInfo {
	int i,j;
	int matrix;
	int cost;
	CellInfo(int d, int i, int j, int c) {
	    this.matrix=d;
	    this.i=i;
	    this.j=j;
	    this.cost=c;
	}
    };

    int maxCost;
    Stack queue[];

    LazyLinearDPA(String str1, String str2, Matrix m) {
	super(str1,str2,m);
	m.setLinear(true);

	maxCost = min2(str1.length(),str2.length())*misCost + 
	    startGap + contGap * (Math.abs(str1.length()-str2.length()));

	queue = new Stack[maxCost+1];
    }

    public String algName() { return "LazyLinearDPA"; }
    
    public void go() {
	int s1Len = s1.length();
	int s2Len = s2.length();
	push(new CellInfo(diag, 0, 0, 0));
	CellInfo c;
	while (true) {
	    if (quit) return;

	    c = nextCell();

	    LinearMatrixCell mCell = (LinearMatrixCell)mat.getCell(c.i,c.j);

	    // Have we already calculated a value for this cell?  If so, it must be better than this one.
	    if (mCell.getVal(c.matrix) < MatrixCell.bigInt)
	      continue;

	    mCell.setVal(c.matrix, c.cost, col1);
	    cellsComputed++;
	    pause();
	    mCell.setVal(c.matrix, c.cost, col2);

	    if (c.i == s1Len && c.j == s2Len)
		break;

	    int mCost = misCost;
	    if (c.i<s1Len && c.j<s2Len && s1.charAt(c.i) == s2.charAt(c.j))
		mCost = matchCost;

	    if (c.i<s1Len && c.j<s2Len) push(new CellInfo(diag, c.i+1, c.j+1, c.cost+mCost));
	    if (c.j<s2Len) 
		push(new CellInfo(horz, c.i, c.j+1, c.cost + (c.matrix==horz ? 0 : startGap) + contGap));
	    if (c.i<s1Len) 
		push(new CellInfo(vert, c.i+1, c.j, c.cost + (c.matrix==vert ? 0 : startGap) + contGap));
	}

	editCost = c.cost;
    }

    void push(CellInfo c) {
	if (c.cost>maxCost) return; // Is this cost greater than maximum possible best cost?

	// Have we already calculated a value for this cell?  If so, it must be better than this one.
	if (((LinearMatrixCell)mat.getCell(c.i,c.j)).getVal(c.matrix) < MatrixCell.bigInt)
	    return;

	if (queue[c.cost] == null) 
	    queue[c.cost] = new Stack();
	queue[c.cost].push(c);
    }

    CellInfo nextCell() {
	for (int i=0; i<=maxCost; i++)
	    if (queue[i]!=null && !queue[i].empty())
		return (CellInfo)queue[i].pop();
	return null;
    }

    // traceBack the alignment from the 'mat' matrix.
    // return a string containing the alignment, 
    // and draw the alignment on the 'mat' matrix.
    // Identical to traceBack in LinearDPA class.
    public String traceBack() {
	if (editCost<0)
	    go();

	if (editCost<0)
	    return "Not completed";

	int i=s1.length();
	int j=s2.length();
	int dir=minDir(((LinearMatrixCell)mat.getCell(i,j)).getVal(diag),
		       ((LinearMatrixCell)mat.getCell(i,j)).getVal(horz),
		       ((LinearMatrixCell)mat.getCell(i,j)).getVal(vert));
	mat.clearAlignment();
	int lastI=i, lastJ=j;
	StringBuffer res1 = new StringBuffer(),res2=new StringBuffer();

	while (i!=0 || j!=0 || dir!=diag) {
	    int d=0,h=0,v=0;
	    LinearMatrixCell c;
	    switch (dir) {
	    case diag:
		res1.append(s1.charAt(i-1));
		res2.append(s2.charAt(j-1));
		i--; j--;
		c = (LinearMatrixCell)mat.getCell(i,j);
		d = c.getVal(diag);
		h = c.getVal(horz);
		v = c.getVal(vert);
		break;
	    case horz:
		res1.append("-");
		res2.append(s2.charAt(j-1));
		j--;
		c = (LinearMatrixCell)mat.getCell(i,j);
		d = c.getVal(diag) + startGap + contGap;
		h = c.getVal(horz) +            contGap;
		v = c.getVal(vert) + startGap + contGap;
		break;
	    case vert:
		res1.append(s1.charAt(i-1));
		res2.append("-");
		i--;
		c = (LinearMatrixCell)mat.getCell(i,j);
		d = c.getVal(diag) + startGap + contGap;
		h = c.getVal(horz) + startGap + contGap;
		v = c.getVal(vert) +            contGap;
		break;
	    }
	    dir = minDir(d,h,v);

	    mat.addAlignment(lastI, lastJ, i,j);
	    lastI = i;
	    lastJ = j;
	}
	res1.reverse();
	res2.reverse();
	res1.append("\n"+res2);
	return res1.toString();
    }


    int minDir(int dCost, int hCost, int vCost) {
	if (dCost<hCost) {
	    if (dCost<vCost)
		return diag;
	    else
		return vert;
	} else if (vCost<hCost)
	    return vert;
	else
	    return horz;
    }
}


/*
class LazyLinearDPA extends AlignAlgorithm
{
    static final int col1 = 3;
    static final int col2 = 2;

    static final int diag = 0, vert=1, horz=2;
    boolean computed[][][];

    final int matchCost=0, misCost=1, startGap=3, contGap=1;

    LazyLinearDPA(String str1, String str2, Matrix m) {
	super(str1,str2,m);
	m.setLinear(true);
	
	computed = new boolean[str1.length()+1][str2.length()+1][3];
    }
    
    public String algName() { return "LazyLinearDPA"; }

    public void go() {
	int d = calcCell(s1.length(), s2.length(), diag);
	int h = calcCell(s1.length(), s2.length(), horz);
	int v = calcCell(s1.length(), s2.length(), vert);
	if (!quit)
	    editCost = min3(d,h,v);
    }

    public int calcCell(int i, int j, int m) {
	//System.out.println("lazy(i="+i+","+j+")");
	if (quit) return infinity;
	if (i<0 || j<0 || i>s1.length() || j>s2.length()) return infinity;
	if (m<0 || m>2) { System.err.println("Bad matrix in LinearDPA ="+m); return infinity; };

	LinearMatrixCell thisCell = (LinearMatrixCell)mat.getCell(i,j);

	if (computed[i][j][m]) return thisCell.getVal(m);

	if (i==0 && j==0 && m==diag) {
	    // Start condition
	    computed[i][j][diag]=true;
	    thisCell.setVal(diag, 0, col2);
	    cellsComputed++;
	    pause();
	    return 0;
	}

	thisCell.setCol(col1);	// We are about to compute this cell

	int val = infinity;
	switch (m) {
	case diag:
	    {
		int mCost;
		if (i<=0 || j<=0) mCost=misCost;
		else mCost = (s1.charAt(i-1) == s2.charAt(j-1) ? matchCost : misCost);
		
		int m1,m2;
		if (i-j>0) {
		    m1=vert; m2=horz;
		} else {
		    m1=horz; m2=vert;
		}
		
		int diagVal = calcCell(i-1, j-1, diag);
		val = min2( diagVal + mCost,
			    calcCell(i-1,j-1,m1)   + mCost);
		
		int lowerbound = diagVal - misCost + mCost;
		if (val > lowerbound)
		val = min2(val, calcCell(i-1,j-1,m2)+mCost);
		break;
	    }

	case horz:
	    //val = min2( calcCell(i,j-1,diag) + startGap + contGap,
	    //		calcCell(i,j-1,horz) +            contGap);
	    if (i-j > 0) {
		int diagVal = calcCell(i,j-1,diag);
		val = min2( diagVal              + startGap + contGap,
			    calcCell(i,j-1,vert) + startGap + contGap);
		int lowerbound = diagVal - misCost + contGap;
		if (val>lowerbound)
		    val = min2(val, calcCell(i,j-1,horz)+contGap);
	    } else {
		val = min2( calcCell(i,j-1,diag) + startGap + contGap,
			    calcCell(i,j-1,horz) +            contGap);
	    }
	    break;
	case vert:
	    //val = min2( calcCell(i-1,j,diag) + startGap + contGap,
	    //		calcCell(i-1,j,vert) +            contGap);
	    if (i-j < 0) {
		int diagVal = calcCell(i-1,j,diag);
		val = min2( diagVal              + startGap + contGap,
			    calcCell(i-1,j,horz) + startGap + contGap);
		int lowerbound = diagVal - misCost + contGap;
		if (val>lowerbound)
		    val = min2(val, calcCell(i-1,j,vert) + contGap);
	    } else {
		val = min2( calcCell(i-1,j,diag) + startGap + contGap,
			    calcCell(i-1,j,vert) +            contGap);
	    }
	    break;
	}

	computed[i][j][m] = true;
	thisCell.setVal(m,val,col2);
	cellsComputed++;
	pause();

	return val;
    }
}

*/
