package pigeon;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.DimacsOutputSolver;

import java.util.*;
import java.util.stream.LongStream;

public class Main {

    static long factCalculator(int n) {
        return LongStream.rangeClosed(1, n).reduce(1, (long num1, long num2) -> num1 * num2);
    }

    static int[] multiplyArrayByFactor(int[] array, int factor) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i] * (factor);
        }
        return array;
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

    public static void main(String[] args) throws ContradictionException, TimeoutException {
        ISolver solver = SolverFactory.newDefault();
        Scanner sc1 = new Scanner(System.in);
        Scanner sc2 = new Scanner(System.in);
        DimacsOutputSolver dm = new DimacsOutputSolver();
        System.out.println("Veuillez entrer un nombre de pigeons : ");
        int m = sc1.nextInt();
        System.out.println("Veuillez entrer un nombre de nids : ");
        int n = sc2.nextInt();
        solver.setTimeout(30);
        solver.newVar(n * m);
        solver.setExpectedNumberOfClauses((int) (m + (factCalculator(n) / (2 * (factCalculator(n - 2))))));
        IProblem iProblem = solver;
        int[][] pb = new int[m][n];
        int variables = 1;
        ArrayList<int[]> listeMagique = new ArrayList<>();
        for (int ligne = 0; ligne < m; ligne++) {
            for (int colonne = 0; colonne < n; colonne++) {
                pb[ligne][colonne] = variables;
                variables++;
            }
        } // On crée m * n variables différentes x(1,1), x(1,2)...x(m,n)
        for (int i = 0; i < m; i++) {
            solver.addClause(new VecInt(pb[i]));
            dm.addClause(new VecInt(pb[i]));
            listeMagique.add(pb[i]);
        }
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
                tmp = multiplyArrayByFactor(tmp, -1);
                solver.addClause(new VecInt(tmp));
                dm.addClause(new VecInt(tmp));
                listeMagique.add(tmp);
            }
            j++;
        }
        if (iProblem.isSatisfiable()) {
            //System.out.println(Arrays.deepToString(listeMagique.toArray()));
            int[] model = iProblem.model();
            System.out.println(Arrays.toString(model));
            //System.out.println(dm);
        } else {
            System.out.println("UNSAT");
        }
    }
}
