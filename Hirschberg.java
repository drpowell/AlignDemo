// Hirschberg - implement the Hirschberg O(n) space algorithm
// for finding an alignment.  Code translated from Lloyd's javascript
// version: http://www.csse.monash.edu.au/~lloyd/tildeAlgDS/Dynamic/Hirsch.html
class Hirschberg extends AlignAlgorithm
{
    static final int unusedCol = 0;
    static final int storeCol = 2;
    static final int alignCol = 4;

    int fwdMat[][], revMat[][];
    StringBuffer align1,align2;

    Hirschberg(String str1, String str2, Matrix m) {
	super(str1,str2,m);
	m.setLinear(false);

	fwdMat = new int[2][s2.length()+1];
	revMat = new int[2][s2.length()+1];
	align1 = new StringBuffer();
	align2 = new StringBuffer();
    }

    public String algName() { return "Hirschberg"; }

    public void go() {
	mat.clearAlignment();
	mat.setCol(0, 0, alignCol);
	mat.setCol(s1.length(),s2.length(), alignCol);
	align(0, s1.length(), 0, s2.length());
	if (quit) editCost=-1;
    }

    void align(int p1,int p2, int q1, int q2) {
	if (quit) return;
	// Align s1[p1..p2) with p2[q1..q2)
	//System.out.println("s1["+p1+".."+(p2-1)+"] : s2["+q1+".."+(q2-1)+"]");

	// First the base cases...

	if (p2<=p1) {		// s1 is empty
	    for(int j=q1; j<q2; j++) {
		if (quit) return;
		align1.append("-");
		align2.append(s2.charAt(j));
		mat.setCol(p1,j+1, alignCol);
		mat.addAlignment(p1,j,p1,j+1);
		pause();
	    }
	    return;
	}

	if (q2<=q1) {		// s2 is empty
	    for(int i=p1; i<p2; i++) {
		if (quit) return;
		align1.append(s1.charAt(i));
		align2.append("-");
		mat.setCol(i+1,q2, alignCol);
		mat.addAlignment(i,q2,i+1,q2);
		pause();
	    }
	    return;
	}

	if ( p1+1 == p2) {	// s1 is exactly one character
	    char ch = s1.charAt(p1);
	    int memo = q1;
	    for (int j=q1+1; j<q2; j++) if (s2.charAt(j) == ch) memo=j;
	    // memo = an optimal cross point 
	    for (int j=q1; j<q2; j++) {
		if (quit) return;

		if (j==memo) align1.append(ch); else align1.append("-");
		align2.append(s2.charAt(j));

		if (j<memo)
		    mat.setCol(p1, j+1, alignCol);
		else
		    mat.setCol(p1+1, j+1, alignCol);

		mat.addAlignment((j<=memo ? p1 : p1+1),j,
				 (j<memo? p1 : p1+1),j+1);
		pause();
	    }

	    return;
	}

	// Done with the base cases.  
	// Now the general case. Divide and conquer!
	int mid = (int)Math.floor((p1+p2)/2);
	fwdDPA(p1, mid, q1, q2);
	revDPA(mid, p2, q1, q2);
	if (quit) return;

	int s2mid=q1, best=infinity;
	// Find the cheapest split
	for (int j=q1; j<=q2; j++) {
	    int sum = fwdMat[mid%2][j] + revMat[mid%2][j];
	    if (sum<best) { best=sum; s2mid=j; };
	}
	if (editCost==-1) editCost=best;

	// Mark the matrices unused...
	for (int j=q1; j<=q2; j++) {
	    unusedCell(mid,j); 
	    unusedCell(mid-1,j); 
	    unusedCell(mid+1,j); 
	}
	    
	mat.setCol(mid,s2mid, alignCol);
	//mat.addAlignment(mid,s2mid, mid+1, s2mid+1);

	// Recurse on the two halves...
	align(p1, mid,  q1, s2mid);
	align(mid,p2,   s2mid, q2);
    }

    void fwdDPA(int p1, int p2, int q1, int q2) {
	fwdMat[p1%2][q1] = 0;
	storeCell(p1,q1,0);
	cellsComputed++;
	pause();

	// Setup the first row
	for (int j=q1+1; j<=q2; j++) {
	    if (quit) return;
	    fwdMat[p1%2][j] = fwdMat[p1%2][j-1]+1;
	    storeCell(p1,j,fwdMat[p1%2][j]);
	    cellsComputed++;
	    pause();
	}
	    
	for (int i=p1+1; i<=p2; i++) {
	    // Mark the row to be calculated as unused.
	    if (i>=p1+2)
		for (int j=q1; j<=q2; j++)
		    unusedCell(i-2,j);

	    fwdMat[i%2][q1] = fwdMat[(i-1)%2][q1]+1;
	    storeCell(i, q1, fwdMat[i%2][q1]);
	    cellsComputed++;
	    pause();
	    for (int j=q1+1; j<=q2; j++) {
		if (quit) return;
		fwdMat[i%2][j] = min3( fwdMat[(i-1)%2][j] + 1,       //delete
				       fwdMat[i%2][j-1]   + 1,       //insert
				       fwdMat[(i-1)%2][j-1] +        //match/mismatch
  				          (s1.charAt(i-1) == s2.charAt(j-1) ? 0 : 1));
		storeCell(i,j, fwdMat[i%2][j]);
		cellsComputed++;
		pause();
	    }
	}
    }

    void revDPA(int p1, int p2, int q1, int q2) {
	revMat[p2%2][q2] = 0;
	storeCell(p2,q2,0);
	cellsComputed++;
	pause();

	// Setup the first row
	for (int j=q2-1; j>=q1; j--) {
	    if (quit) return;
	    revMat[p2%2][j] = revMat[p2%2][j+1]+1;
	    storeCell(p2,j,revMat[p2%2][j]);
	    cellsComputed++;
	    pause();
	}
	    
	for (int i=p2-1; i>=p1; i--) {
	    // Mark the row to be calculated as unused.
	    if (i<=p2-2)
		for (int j=q2; j>=q1; j--)
		    unusedCell(i+2,j);

	    revMat[i%2][q2] = revMat[(i+1)%2][q2]+1;
	    storeCell(i, q2, revMat[i%2][q2]);
	    cellsComputed++;
	    pause();
	    for (int j=q2-1; j>=q1; j--) {
		if (quit) return;
		revMat[i%2][j] = min3( revMat[(i+1)%2][j] + 1,       //delete
				       revMat[i%2][j+1]   + 1,       //insert
				       revMat[(i+1)%2][j+1] +        //match/mismatch
  				          (s1.charAt(i) == s2.charAt(j) ? 0 : 1));
		storeCell(i,j, revMat[i%2][j]);
		cellsComputed++;
		pause();
	    }
	}
    }

    void unusedCell(int i, int j) {
	if (mat.getCol(i,j) != alignCol)
	    mat.getCell(i,j).reset();
    }

    void storeCell(int i, int j,int v) {
	MatrixCell c = mat.getCell(i,j);
	int col = storeCol;
	if (c.getCol() == alignCol) col=alignCol;
	c.setVal(v,col);
    }

    
    public String traceBack() {
	return align1.toString()+"\n"+align2.toString();
    }
}

