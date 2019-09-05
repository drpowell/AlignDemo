
# Sequence Alignment Algorithm Demo

Demos:
* [Simple](https://www.youtube.com/watch?v=lo5RtxLsj7I)
* [Lazy](https://www.youtube.com/watch?v=NKzLNRrruug)
* [Hirschberg's](https://www.youtube.com/watch?v=cPQeJt-2Y1Q)

This code gives a demonstration of several different alignment algorithms.
Some algorithms determine an optimal alignment, some only
the edit cost.  The values in each cell are only
displayed if there is room.

The green cells indicate a cell which has been computed by
the algorithm.  A blue cell indicates that this cell is currently being
computed.

## Build

Clone this repository.  To build (tested with `java 12`)

    make

To run:

    java AlignDemo


## Implemented algorithms

### Standard DPA
This is the simple DPA for point mutation costs,
match=0, mismatch=1, insert/delete=1.
Each cell of the matrix, `D[i][j]`, contains the edit cost for the sequences
`s1[1..i]` and `s2[1..j]`.  An optimal alignment is drawn through the matrix

### Lazy DPA
This is a modified version of the standard DPA which computes a smaller region
of the D matrix (runs in `O(nd)` time), where `d` is the edit distance.


### Ukkonen's Algorithm
Ukkonen's algorithm runs in `O(d*d + n)` time.  This algorithm does not use the
D matrix like the other DPA algorithms.  However, Ukkonen's algorithm is shown
here operating of the standard matrix to give an idea as to which cells of the
DPA matrix it computes.  Blue cells indicate a cell currently being computed.
Green cells indicate cells that have already been computed.

### Hirschberg
Hirschberg presented a modification of the DPA that allows an optimal
alignment to be found in `O(n)` space.  Green cells are cells that have been
computed and are currently stored.  Yellow cells indicate cells that are known
to lie on the optimal alignment.

### Linear DPA
This algorithm is the `O(n*n)` DPA for linear gap
costs, with gaps costed as a+b*k where a=3, b=1, matches=0,
mismatches=0.


[comment]: # (Lazy Linear DPA)
[comment]: # (An attempt at a lazy evaluation for linear gap costs.  It is wrong, incorrect and don't work!)

### Ukkonen Linear
This is Ukkonen's algorithm for linear gap costs (same costs
as for the Linear DPA case).
