
class UkkLinear extends AlignAlgorithm
{
    static final int col1 = 3;
    static final int col2 = 2;
    static final int diag = 0, vert=1, horz=2;
    static final int matchCost=0, misCost=1, startGap=3, contGap=1;    

    int finalDiag;
    int U[][][];
    boolean computed[][][];
    int abOff;
    StringBuffer indent;

    UkkLinear(String str1, String str2, Matrix m) {
	super(str1,str2,m);
	m.setLinear(true);

	finalDiag = s1.length()-s2.length();

	int maxCost = min2(str1.length(),str2.length())*misCost + 
	    startGap + contGap * (Math.abs(str1.length()-str2.length()));

	U        = new int    [3][2*maxCost][maxCost+1];
	computed = new boolean[3][2*maxCost][maxCost+1];

	abOff = maxCost;
    }

    public String algName() { return "UkkLinear"; }
    
    public void go() {
	
	((LinearMatrixCell)mat.getCell(0,0)).setVal(diag, 0, col2);
	cellsComputed++;
	pause();

	// Setup base case 
	int dist = extend(0,0,0);
	U[diag][0+abOff][0] = dist;
	computed[diag][0+abOff][0] = true;

	indent = new StringBuffer();
	int cost = -1;
	do {
	    if (quit) return;
	    cost++;
	    dist = max3(calcCell(diag, finalDiag, cost),
			calcCell(horz, finalDiag, cost),
			calcCell(vert, finalDiag, cost));
	} while (dist < s1.length());

	if (!quit)
	    editCost = cost;
    }

    int calcCell(int dir, int ab, int cost) {
	if (quit) return -infinity;
	if (cost<0 || Math.abs(ab)>cost) return -infinity; // Outside range

	if (computed[dir][ab+abOff][cost])
	    return U[dir][ab+abOff][cost]; // Already computed

	//System.out.println(indent+"To compute U[dir="+dir+"][ab="+ab+"][cost="+cost+"]");
	
	indent.append("  ");
	int dist = -1;
	switch (dir) {
	case diag:
	    dist = max3(calcCell(diag, ab, cost-misCost)+1,
			calcCell(horz, ab, cost),
			calcCell(vert, ab, cost));
	    break;

	case horz:
	    dist = max3(calcCell(diag, ab+1, cost-startGap-contGap),
			calcCell(horz, ab+1, cost-contGap),
			calcCell(vert, ab+1, cost-startGap-contGap));
	    break;

	case vert:
	    dist = max3(calcCell(diag, ab-1, cost-startGap-contGap)+1,
			calcCell(horz, ab-1, cost-startGap-contGap)+1,
			calcCell(vert, ab-1, cost-contGap)+1);
	    break;
	}
	indent.setLength(indent.length()-2);

	//System.out.println(indent+"calced a base dist of "+dist);
	if (dist>=ab) {
	    ((LinearMatrixCell)mat.getCell(dist,dist-ab)).setVal(dir, cost, col1);
	    cellsComputed++;
	    pause();
	    ((LinearMatrixCell)mat.getCell(dist,dist-ab)).setVal(dir, cost, col2);
	    
	    // Try and move along a run of matches if possible
	    if (dir==diag)
		dist = extend(ab,dist,cost);
	}

	U[dir][ab+abOff][cost] = dist;
	computed[dir][ab+abOff][cost] = true;
 	//System.out.println(indent+"U[dir="+dir+"][ab="+ab+"][cost="+cost+"]="+dist);
	return dist;
    }

    // extend - attempt to extend along a diagonal
    int extend(int ab, int dist, int cost) {
	while (dist<s1.length() && dist-ab<s2.length() &&
	       s1.charAt(dist) == s2.charAt(dist-ab)) {
	    dist++;
	    ((LinearMatrixCell)mat.getCell(dist,dist-ab)).setVal(diag, cost, col1);
	    cellsComputed++;
	    pause();
	    ((LinearMatrixCell)mat.getCell(dist,dist-ab)).setVal(diag, cost, col2);
	    if (quit) return -infinity;
	}
	return dist;
    }
}
