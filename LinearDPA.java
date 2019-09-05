class LinearDPA extends AlignAlgorithm
{
    static final int col1 = 3;
    static final int col2 = 1;

    static final int diag = 0, vert=1, horz=2;
    boolean computed[][][];

    final int matchCost=0, misCost=1, startGap=3, contGap=1;

    LinearDPA(String str1, String str2, Matrix m) {
	super(str1,str2,m);
	m.setLinear(true);

	computed = new boolean[str1.length()+1][str2.length()+1][3];
    }

    public String algName() { return "LinearDPA"; }

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
	    int mCost;
	    if (i<=0 || j<=0) mCost=misCost;
	    else mCost = (s1.charAt(i-1) == s2.charAt(j-1) ? matchCost : misCost);
	    val = min3( calcCell(i-1,j-1,diag) + mCost,
			calcCell(i-1,j-1,horz) + mCost,
			calcCell(i-1,j-1,vert) + mCost);
	    break;
	case horz:
	    val = min3( calcCell(i,j-1,diag) + startGap + contGap,
			calcCell(i,j-1,horz) +            contGap,
			calcCell(i,j-1,vert) + startGap + contGap);
	    break;
	case vert:
	    val = min3( calcCell(i-1,j,diag) + startGap + contGap,
			calcCell(i-1,j,horz) + startGap + contGap,
			calcCell(i-1,j,vert) +            contGap);
	    break;
	}
	if (quit) return infinity;

	computed[i][j][m] = true;
	thisCell.setVal(m,val,col2);
	cellsComputed++;
	pause();

	return val;
    }

    // traceBack the alignment from the 'mat' matrix.
    // return a string containing the alignment, 
    // and draw the alignment on the 'mat' matrix.
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
