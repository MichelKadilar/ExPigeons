package pigeon;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.DimacsOutputSolver;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.LongStream;

public class Pigeon {

    static long factCalculator(int n) {
        return LongStream.rangeClosed(1, n).reduce(1, (long num1, long num2) -> num1 * num2);
    }

    public static BigInteger factorial(int number) {
        BigInteger factorial = BigInteger.ONE;
        for (int i = number; i > 0; i--) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        return factorial;
    }

    static void multiplyArrayByFactor(int[] array, int factor) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i] * (factor);
        }
    }

    public static void findCombinations(int[] A, int i, int k,
                                        Set<List<Integer>> subarrays,
                                        List<Integer> out) {
        // invalid input
        if (A.length == 0 || k > A.length) {
            return;
        }
        // base case: combination size is `k`
        if (k == 0) {
            subarrays.add(new ArrayList<>(out));
            return;
        }
        // start from the next index till the last index
        for (int j = i; j < A.length; j++) {
            // add current element `A[j]` to the solution and recur for next index
            // `j+1` with one less element `k-1`
            out.add(A[j]);
            findCombinations(A, j + 1, k - 1, subarrays, out);
            out.remove(out.size() - 1);        // backtrack
        }
    }

    public static Set<List<Integer>> findCombinations(int[] A, int k) {
        Set<List<Integer>> subarrays = new HashSet<>();
        findCombinations(A, 0, k, subarrays, new ArrayList<>());
        return subarrays;
    }

    public void satSolver(int m, int n) throws ContradictionException, TimeoutException {
        ISolver solver = SolverFactory.newDefault();
        DimacsOutputSolver dm = new DimacsOutputSolver();
        solver.setTimeout(60);
        dm.setTimeout(60);
        solver.newVar(n * m);
        dm.newVar(n * m);
        solver.setExpectedNumberOfClauses(((factorial(m).multiply(BigInteger.valueOf(n)).divide((factorial(m - 2).multiply(BigInteger.valueOf(2))))).add(BigInteger.valueOf(m))).intValue());
        dm.setExpectedNumberOfClauses(((factorial(m).multiply(BigInteger.valueOf(n)).divide((factorial(m - 2).multiply(BigInteger.valueOf(2))))).add(BigInteger.valueOf(m))).intValue());
        int[][] pb = new int[m][n];
        int variables = 1;
        for (int ligne = 0; ligne < m; ligne++) {
            for (int colonne = 0; colonne < n; colonne++) {
                pb[ligne][colonne] = variables;
                variables++;
            }
            solver.addClause(new VecInt(pb[ligne]));
            dm.addClause(new VecInt(pb[ligne]));
        } // On crée m * n variables différentes x(1,1), x(1,2)...x(m,n)

        int j = 0;
        int[] temp = new int[m];
        while (j < n) {
            for (int i = 0; i < m; i++) {
                temp[i] = pb[i][j];
            }
            for (List<Integer> l : findCombinations(temp, 2)) {
                int[] tmp = new int[2];
                for (int b = 0; b < l.size(); b++) {
                    tmp[b] = l.get(b);
                }
                multiplyArrayByFactor(tmp, -1);
                solver.addClause(new VecInt(tmp));
                dm.addClause(new VecInt(tmp));
            }
            j++;
        }
        if (solver.isSatisfiable()) {
            int[] model = solver.model();
            System.out.println(Arrays.toString(model));
        } else {
            System.out.println("UNSAT");
        }
    }


}
