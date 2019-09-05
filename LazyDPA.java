
class LazyDPA extends AlignAlgorithm
{
    static final int col1 = 3;
    static final int col2 = 2;

    int finalDiag;
    boolean computed[][];

    LazyDPA(String str1, String str2, Matrix m) {
	super(str1,str2,m);
	m.setLinear(false);

	finalDiag = s1.length() - s2.length();
	computed = new boolean[s1.length()+1][s2.length()+1];
    }

    public String algName() { return "Lazy DPA"; }

    public void go() {
	calcCell(s1.length(), s2.length());
	if (!quit)
	    editCost = mat.getVal(s1.length(),s2.length());
    }

    public int calcCell(int i, int j) {
	//System.out.println("lazy(i="+i+","+j+")");
	if (quit) return infinity;
	if (i<0 || j<0 || i>s1.length() || j>s2.length()) return infinity;

	if (computed[i][j]) return mat.getVal(i,j);

	if (i==0 && j==0) {
	    // Start condition
	    computed[i][j]=true;
	    mat.setVal(i,j, 0, col2);
	    cellsComputed++;
	    pause();
	    return 0;
	}

	mat.setCol(i,j,col1);	// We are about to compute this cell

	// Is it an edge of the D matrix?
	if (i==0) {
	    mat.setVal(i,j, calcCell(i,j-1)+1, col2);
	    computed[i][j]=true;
	    cellsComputed++;
	    pause();
	    return mat.getVal(i,j);
	} else if (j==0) {
	    mat.setVal(i,j, calcCell(i-1,j)+1, col2);
	    computed[i][j]=true;
	    cellsComputed++;
	    pause();
	    return mat.getVal(i,j);
	}


	int diagVal =  calcCell(i-1,j-1);

	int i1,j1,i2,j2;
	if (i-j < 0) {
	    i1 = i;   j1 = j-1;
	    i2 = i-1; j2 = j;
	} else {
	    i1 = i-1; j1 = j;
	    i2 = i;   j2 = j-1;
	}

	//At this point, (i1,j1) is cell horz or vert which is closer to the final diag
	//cell (i2,j2) is the other cell

	int val = calcCell(i1,j1);
	if (val>=diagVal) {
	    val = calcCell(i2,j2);
	}
	if (quit) return infinity;

	int finalVal;

	if (diagVal <= val)
	    finalVal = diagVal + (s1.charAt(i-1) == s2.charAt(j-1) ? 0 : 1);
	else
	    finalVal = val+1;

	computed[i][j]=true;
	mat.setVal(i,j, finalVal, col2);
	cellsComputed++;
	pause();

	return mat.getVal(i,j);
    }

    // traceBack the alignment from the 'mat' matrix.
    // return a string containing the alignment, 
    // and draw the alignment on the 'mat' matrix.
    // This routine is identical to the one in the standard DPA class
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
	    int v=mat.getVal(i,j);
	    if (i>0 && v == mat.getVal(i-1,j)+1) {
		//delete
		res1.append(s1.charAt(i-1));
		res2.append("-");
		i--;
	    } else if (j>0 && v == mat.getVal(i,j-1)+1) {
		//insert
		res1.append("-");
		res2.append(s2.charAt(j-1));
		j--;
	    } else if (i>0 && j>0 && (v == mat.getVal(i-1,j-1)+
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