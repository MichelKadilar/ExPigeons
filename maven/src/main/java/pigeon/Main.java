package pigeon;


import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws ContradictionException, TimeoutException {
        Pigeon pigeon = new Pigeon();
        int m, n;
        /* Scanner sc1 = new Scanner(System.in);
        Scanner sc2 = new Scanner(System.in);
        System.out.println("Veuillez entrer un nombre de pigeons : ");
        m = sc1.nextInt();
        System.out.println("Veuillez entrer un nombre de nids : ");
        n = sc2.nextInt(); */
        m = 8;
        n = 7;
        pigeon.satSolver(m, n);
    }
}
