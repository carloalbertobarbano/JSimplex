public class SimplexSolver {
  static boolean debug = false;
  
  /**
    Print messages in debug mode
    @param s string to be printed
  */
  public static void Print(String s) {
    if(debug)System.out.print(s);
  }
  
  /**
    Print messages in debug mode - double overload.
    @param d double to be printed
  */
  public static void Print(double d) {
    Print(""+d);
  }
  
  /**
    Print messages in debug mode
    @param s string to be printed
  */
  public static void Println(String s) {
    Print(s + "\n");
  }
  
  /**
   Check if the current solution is the best one, by checking if coefficients in C are negative.
   @param C is the objective function coefficients vector, with the explicit term in last position.
   @return true if C contains the best solution, false otherwise 
  */
  public static boolean IsBestSolution(double[] C) {
    for(int i = 0;i < C.length - 1; i++)
        if(C[i] > 0)
          return false;

    return true;
  }
  
  /** 
    Copies one matrix to another one
    @param src source matrix
    @param dst destination matrix. dst dimensions must be the same as src
  */
  public static void CopyMat(double[][] src, double[][] dst) {
    for(int i = 0; i < src.length; i++) {
      for(int j = 0;j < src[i].length; j++) {
        dst[i][j] = src[i][j];
      }
    }
  }

  /** 
    Transform the element at (i, j) in A to the value of target, by calculating a transform factor to multiply the
    i-th line with.
    @param A is the constraints coefficients matrix
    @param B is the explicit terms vector
    @param newB is the resultant explicit terms vector after PivotTo() execution
    @param i index of the row in A
    @param j index of the column in A
    @param target value that the element in (i, j) in A must be transformed to 
    @return double[] - the transformed row in A
  */
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
  
  /** 
   Performs a Gauss-Jordan reduction using the element in A at index (i, j) as pivot.
   @param A is the constraints coefficients matrix
   @param B is the explicit terms vector
   @param i index of the row in A
   @param j index of the column in A
  */ 
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

  /** 
    Finds the next variable to enter base.
    @param C is the objective function coefficients vector, with the explicit term in last position.
    @return int the index of the column corresponding to the entering variable. 
  */
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

  /**
    @param A is the constraints coefficients matrix
    @param i is the index of the column to check
    @return true if all the elements of the i-th column in the matrix A are positive.
  */
  public static boolean IsColumnPositive(double[][] A, int i) {
    for(int r = 0; r < A.length; r++)
      if(A[r][i] < 0)return false;
    return true;
  }
  
  /**
    Finds the line where GJ reduction must be applied.
    @param A is the constraints coefficients matrix
    @param B is the explicit terms vector
    @param entering is the index of the entering variable, returned by FindEnteringVar(double[] C) 
    @return int the index of the row in the A matrix, which is the line where Gauss-Jordan reduction has to be applied.
  */
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
  
  /**
    Check if a variable is a base variable, by checking if the corresponding column in the constraints matrix is an identity
    column.
    
    @param A is the constraints coefficients matrix.
    @param i is the variable (column in the matrix) to check.
    @return true if the variable at the i-th column is in base, false oterwhise.
  */
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

  /**
    Generate new coefficents for the objective functions from the updated constraints matrix.
    @param A is the constraints coefficients matrix.
    @param B is the explicit terms vector.
    @param C is the objective function coefficients vector, with the explicit term in last position.
  */
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

  /**
    Finds a new objective function by swapping base variables, and performing 
    a Gauss-Jordan reduction on constraints matrix.
  
    @param A is the constraints coefficients matrix.
    @param B is the explicit terms vector.
    @param C is the objective function coefficients vector, with the explicit term in last position.
  */
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
  /**
    Perform Simplex algorithm until the best solution is found.
    @param A is the constraints coefficients matrix.
    @param B is the explicit terms vector.
    @param C is the objective function coefficients vector, with the explicit term in last position.
  */
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
}
