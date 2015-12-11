class JSimplex {
	public static void main(String[] args) {
    if(args.length >= 1)
      if(args[0].equals("-d") || args[0].equals("-D") || args[0].equals("-debug"))
        SimplexSolver.debug = true;
      else
        System.out.println("Unknown option: " + args[1]);


    double[][] A = { {  2, -1, 1, 0, 0 },
                     { -1,  1, 0, 1, 0 },
                     {  1,  0, 0, 0, 1 } };

		double[] B =  { 3,
                    1,
                    8 };

    //             x1  x2  x3 x4 x5 t
		double[] C =  { 3, -1, 0, 0, 0, 0 };


    SimplexSolver.Simplex(A, B, C);

    System.out.println("\n\nBest Solution found: ");
    System.out.print("z = ");
    for(int i = 0;i < C.length; i++) {
      if(C[i] == 0)continue;

      if(i < C.length - 1)  System.out.print(C[i] + "x" + (i+1) + " + ");
      else System.out.print(C[i]);
    }
    System.out.print("\n");
  }
}