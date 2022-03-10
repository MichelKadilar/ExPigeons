package pigeon;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Pigeon {

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

    public void creerFichier() {
        try {
            File myObj = new File("resultat.txt");
            myObj.createNewFile();
        } catch (IOException e) {
            System.out.println("Erreur durant la création du fichier.");
            e.printStackTrace();
        }
    }

    public void writeInFichier(String model) {
        try {
            FileWriter myWriter = new FileWriter("resultat.txt");
            myWriter.write(model);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Erreur durant l'écriture dans le fichier.");
            e.printStackTrace();
        }
    }

    public void satSolver(int m, int n) throws ContradictionException, TimeoutException {
        ISolver solver = SolverFactory.newDefault();
        StringBuilder stringBuilder = new StringBuilder("p cnf ");
        creerFichier();
        solver.setTimeout(60);
        solver.newVar(n * m);
        int deuxParmiM = m + (((m * (m - 1)) * n) / 2);
        stringBuilder.append(n * m).append(" ").append(deuxParmiM).append("\n");
        solver.setExpectedNumberOfClauses(deuxParmiM);
        int[][] pb = new int[m][n];
        int variable = 1;
        for (int ligne = 0; ligne < m; ligne++) {
            for (int colonne = 0; colonne < n; colonne++) {
                pb[ligne][colonne] = variable;
                stringBuilder.append(variable).append(" ");
                variable++;
            }
            solver.addClause(new VecInt(pb[ligne]));
            stringBuilder.append("0\n");
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
                    tmp[b] = -1*l.get(b);
                    stringBuilder.append(-1 * l.get(b)).append(" ");
                }
                solver.addClause(new VecInt(tmp));
                stringBuilder.append("0\n");
            }
            j++;
        }
        if (solver.isSatisfiable()) {
            int[] model = solver.model();
            System.out.println(Arrays.toString(model));
            writeInFichier(stringBuilder.toString());
        } else {
            System.out.println("UNSAT");
        }
    }
}
