class Simplex {
  static boolean debug = false;
  public static void Print(String s) {
    if(debug)System.out.print(s);
  }

  public static void Print(double d) {
    Print(""+d);
  }
  public static void Println(String s) {
    Print(s + "\n");
  }

  public static boolean IsBestSolution(double[] C) {
    for(int i = 0;i < C.length - 1; i++)
        if(C[i] > 0)
          return false;

    return true;
  }

  public static void CopyMat(double[][] src, double[][] dst) {
    for(int i = 0; i < src.length; i++) {
      for(int j = 0;j < src[i].length; j++) {
        dst[i][j] = src[i][j];
      }
    }
  }

  public static double[] PivotTo(double[][] A, double[] B, double[] newB, int i, int j, int target) {
      double[] res_row = new double[A[i].length];
      int row = 0;

      while(row < A.length-1) {
        if(A[row][j] != 0.0 && row != i)
          break;

          row++;
      }

      if(row == A.length-1)
        return A[i];


      //Println("Pivot line: " + i + ", row chosen: " + row);

      double transformFactor = 0;

      if(target == 1) {
        transformFactor = A[i][j];
        //Println("Target: " + target + ", transorm factor: " + transformFactor);

        for(int c = 0; c < A[i].length; c++)
          res_row[c] = A[i][c] / transformFactor;

        newB[i] = B[i] / transformFactor;
      } else {

        transformFactor = (target - A[i][j])/A[row][j];
        //Println("Target: " + target + ", transorm factor: " + transformFactor);

        for(int c = 0; c < A[i].length; c++) {
          res_row[c] = A[i][c] + transformFactor * A[row][c];
          //Print(res_row[c] + " ");
        }
        newB[i] = B[i] + transformFactor * B[row];
     }

      return res_row;
  }

  public static void GaussJordan(double[][] A, double[] B, int i, int j) {
    double[][] tmpMat = new double[A.length][];
    double[]   tmpB = new double[B.length];


    tmpMat[i] = PivotTo(A, B, tmpB, i, j, 1);

    for(int r = 0; r < A.length; r++) {
      if(r != i)
        tmpMat[r] = PivotTo(A, B, tmpB, r, j, 0);
    }

    CopyMat(tmpMat, A);

    for(int t = 0;t < B.length; t++)
      B[t] = tmpB[t];
  }

  public static int FindEnteringVar(double[] C) {
    double max = 0.0;
    int entering = 0;
    for(int i = 0;i < C.length - 1; i++) {
      if(C[i] > max) {
        max = C[i];
        entering = i;
      }
    }

    return entering;
  }

  public static boolean IsColumnPositive(double[][] A, int i) {
    for(int r = 0; r < A.length; r++)
      if(A[r][i] < 0)return false;
    return true;
  }
  public static int FindExittingVarLine(double[][] A, double[] B, int entering) {
    int lineExitting = 0;
    double min = 0.0;


    for(int row = 0;row < A.length; row++) {
      //if(IsColumnPositive(A, entering)) {
        if(A[row][entering] > 0) {
          double quotient = -B[row]/A[row][entering];
          quotient = 1 / quotient;

          if(quotient < min) {
            min = quotient;
            lineExitting = row;
          }
        }
      /*} else {
        double quotient = -B[row]/A[row][entering];
        quotient = 1 / quotient;

        if(quotient > min) {
          min = quotient;
          lineExitting = row;
        }
      }*/
    }

    return lineExitting;
  }

  public static boolean IsBase(double[][] A, int i) {
      boolean one_found = false;

      for(int r = 0; r < A.length; r++) {
        if(A[r][i] != 0 && A[r][i] != 1)
            return false;

        if(A[r][i] == 1 && !one_found)
            one_found = true;
        else if(A[r][i] == 1 && one_found)
            return false;
      }

      return true && one_found;
  }

  public static int GetBaseRow(double[][] A, int i) {
    for(int r = 0; r < A.length; r++) {
      if(A[r][i] == 1)
        return r;
    }
    return 0;
  }

  public static void GenerateSolution(double[][] A, double[] B, double[] C) {
    /*double[] updatedC = new double[C.length];
    for(int i = 0;i < C.length; i++)
      updatedC[i] = 0.0;*/

    int numBaseVar = 0;
    for(int i = 0;i < C.length - 1; i++) {
      if(IsBase(A, i))numBaseVar ++;
    }

    int[] baseVar = new int[numBaseVar];
    int j = 0;
    for(int i = 0;i < C.length - 1; i++) {
      if(IsBase(A, i))baseVar[j++] = i;
    }

    Print("Number of base variables: " + numBaseVar +  " { ");
    for(int i = 0;i < numBaseVar; i++)
      Print("x" + (baseVar[i]+1) + " ");
    Println("}");


    for(int i = 0;i < C.length - 1; i++) {
      Println("Testing x" + (i+1));

        if(IsBase(A, i)) {
          int row = GetBaseRow(A, i);
          double coeff = C[i];
          Println("\tx" + (i+1) + " is base");
          Println("\tRow of base var: " + row);
          Println("\tCoeff in objective f: " + coeff);

          for(int c = 0; c < C.length; c++) {
            if(c != i) {
              if(c < C.length - 1)
               C[c] += -coeff*A[row][c];
              else
               C[c] += coeff*B[row];
            }
          }
          C[i] = 0;
        }
    }
  }


  public static void FindNewSolution(double[][] A, double[] B, double[] C) {
    /* Find entering variable */
    int entering = FindEnteringVar(C);

    /* Find exitting variable */
    int lineExitting = FindExittingVarLine(A, B, entering);

    Println("Pivot at: " + lineExitting + ", " + entering);
    /* GaussJordan on line of exitting variable with entering variable as pivot */
    GaussJordan(A, B, lineExitting, entering);

    PrintMatrix(A, B);
    /* Find new z function */
    GenerateSolution(A, B, C);

    Print("Solution found: z = ");
    for(int i = 0;i < C.length; i++) {
      if(C[i] == 0)continue;

      if(i < C.length - 1)  Print(C[i] + "x" + (i+1) + " + ");
      else Print(C[i]);
    }
    Print("\n");

  }


  static int iteration = 0;
  public static void Simplex(double[][] A, double[] B, double C[]) {

    if(!IsBestSolution(C)) {
      Println("---------- SIMPLEX ITERATION " + iteration++ + " ----------");

      Println("Applying simplex to: ");
      PrintMatrix(A, B);

      /* Find new solution */
      FindNewSolution(A, B, C);

      try {
        if(debug) {
          System.out.println("Press enter to continue..");
          System.in.read();
          while((char)System.in.read() != '\n');
        }
      } catch(Exception e) {
        e.printStackTrace();
      }

      /* Test new solution */
      Simplex(A, B, C);
    }
  }

  public static void PrintMatrix(double[][] A, double[] B){
    Print("\n\n");

    for(int i = 0;i < A.length; i++) {
      for(int j = 0;j < A[i].length; j++) {
        Print("\t" + A[i][j]);
      }
      Print("  |\t" + B[i]);
      Print("\n");
    }
    Print("\n");
  }

  public static void main(String[] args) {
    if(args.length >= 1)
      if(args[0].equals("-d") || args[0].equals("-D") || args[0].equals("-debug"))
        debug = true;
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


    Simplex(A, B, C);

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
