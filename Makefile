
JAVAC = javac

all: AlignDemo.class AlignAlgorithm.class \
     Matrix.class MatrixCell.class LinearMatrixCell.class \
     LazyDPA.class Hirschberg.class LinearDPA.class \
     LazyLinearDPA.class UkkLinear.class

%.class : %.java
	${JAVAC} -g $<

clean:
	rm -f *.class
