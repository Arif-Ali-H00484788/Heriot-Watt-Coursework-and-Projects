package uk.ac.hw.macs.search.example;
import java.util.Scanner;
import uk.ac.hw.macs.search.Node;
import uk.ac.hw.macs.search.SearchProblem;
import uk.ac.hw.macs.search.SearchOrder;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueProgram = true;

        while (continueProgram) {
            try {
                // Grid selection with error handling
                int gridChoice = getValidInput(scanner, 1, 2,
                        "\nChoose the grid (1 for Grid 1, 2 for Grid 2): ",
                        "Invalid grid choice! Please enter 1 or 2.");

                Node startNode = (gridChoice == 1) ? createGrid1() : createGrid2();

                // Algorithm selection with error handling
                int algorithmChoice = getValidInput(scanner, 1, 3,
                        "Choose the search algorithm (1 for A*, 2 for BFS, 3 for DFS): ",
                        "Invalid algorithm choice! Please enter 1, 2, or 3.");

                SearchOrder searchOrder;
                switch (algorithmChoice) {
                    case 1:
                        System.out.println("Running A* search...");
                        searchOrder = new AStarSearchOrder();
                        break;
                    case 2:
                        System.out.println("Running BFS...");
                        searchOrder = new BreadthFirstSearchOrder();
                        break;
                    case 3:
                        System.out.println("Running DFS...");
                        searchOrder = new DepthFirstSearchOrder();
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid algorithm choice!");
                }

                runSearch(startNode, searchOrder);

                // Continue prompt with error handling
                continueProgram = getContinueResponse(scanner);

            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
                // Clear scanner buffer
                scanner.nextLine();
            }
        }

        System.out.println("\nThank you for using the search program!");
        scanner.close();
    }

    /**
     * Gets valid integer input within a specified range
     */
    private static int getValidInput(Scanner scanner, int min, int max, String prompt, String errorMessage) {
        while (true) {
            try {
                System.out.println(prompt);
                if (!scanner.hasNextInt()) {
                    scanner.nextLine(); // Clear invalid input
                    throw new IllegalArgumentException(errorMessage);
                }

                int input = scanner.nextInt();
                scanner.nextLine(); // Clear buffer

                if (input < min || input > max) {
                    throw new IllegalArgumentException(errorMessage);
                }

                return input;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Gets valid yes/no response for continuing the program
     */
    private static boolean getContinueResponse(Scanner scanner) {
        while (true) {
            try {
                System.out.println("\nDo you want to run another search? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();

                if (response.equals("y") || response.equals("yes")) {
                    return true;
                } else if (response.equals("n") || response.equals("no")) {
                    return false;
                } else {
                    throw new IllegalArgumentException("Invalid input! Please enter 'y' or 'n'");
                }

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Runs the search with the specified start node and search order
     */
    private static void runSearch(Node start, SearchOrder searchOrder) {
        try {
            SearchProblem problem = new SearchProblem(searchOrder);
            boolean foundGoal = problem.doSearch(start);
            if (!foundGoal) {
                System.out.println("No path to the goal found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during the search: " + e.getMessage());
        }
    }

    private static Node createGrid1() {
        IntState.setGoal(2, 5);

        Node start1 = new Node(new IntState(0, 0, "Start"));
        Node A1 = new Node(new IntState(0, 1, "A"));
        Node B1 = new Node(new IntState(0, 3, "B"));
        Node C1 = new Node(new IntState(0, 4, "C"));
        Node D1 = new Node(new IntState(0, 5, "D"));
        Node E1 = new Node(new IntState(1, 0, "E"));
        Node F1 = new Node(new IntState(1, 1, "F"));
        Node H1 = new Node(new IntState(1, 2, "H"));
        Node I1 = new Node(new IntState(1, 3, "I"));
        Node J1 = new Node(new IntState(1, 4, "J"));
        Node K1 = new Node(new IntState(1, 5, "K"));
        Node L1 = new Node(new IntState(2, 0, "L"));
        Node M1 = new Node(new IntState(2, 1, "M"));
        Node N1 = new Node(new IntState(2, 3, "N"));
        Node O1 = new Node(new IntState(2, 4, "O"));
        Node P1 = new Node(new IntState(3, 0, "P"));
        Node Q1 = new Node(new IntState (3, 3, "Q"));
        Node R1 = new Node(new IntState(3, 4, "R"));
        Node T1 = new Node(new IntState(3, 5, "T"));
        Node goal1 = new Node(new IntState(2, 5, "Goal"));

        start1.addChild(A1, 1);
        start1.addChild(E1, 2);
        A1.addChild(F1, 2);
        B1.addChild(I1, 2);
        B1.addChild(C1, 1);
        C1.addChild(D1, 1);
        C1.addChild(J1, 1);
        E1.addChild(F1, 2);
        E1.addChild(L1, 1);
        F1.addChild(H1, 1);
        F1.addChild(M1, 1);
        H1.addChild(I1, 2);
        I1.addChild(J1, 1);
        I1.addChild(N1, 2);
        J1.addChild(K1, 1);
        J1.addChild(O1, 2);
        K1.addChild(goal1, 1);
        L1.addChild(M1, 1);
        L1.addChild(P1, 2);
        N1.addChild(O1, 2);
        N1.addChild(Q1, 1);
        O1.addChild(goal1, 1);
        O1.addChild(R1, 2);
        goal1.addChild(T1, 2);

        return start1;
    }

    private static Node createGrid2() {
        IntState.setGoal(4, 3);

        Node start2 = new Node(new IntState(0, 0, "Start"));
        Node A2 = new Node(new IntState(0, 1, "A"));
        Node B2 = new Node(new IntState(0, 3, "B"));
        Node C2 = new Node(new IntState(0, 4, "C"));
        Node D2 = new Node(new IntState(1, 0, "D"));
        Node E2 = new Node(new IntState(1, 1, "E"));
        Node F2 = new Node(new IntState(1, 2, "F"));
        Node H2 = new Node(new IntState(1, 3, "H"));
        Node I2 = new Node(new IntState(1, 4, "I"));
        Node J2 = new Node(new IntState(2, 0, "J"));
        Node K2 = new Node(new IntState(2, 1, "K"));
        Node L2 = new Node(new IntState(2, 3, "L"));
        Node M2 = new Node(new IntState(2, 4, "M"));
        Node N2 = new Node(new IntState(3, 0, "N"));
        Node O2 = new Node(new IntState(3, 1, "O"));
        Node P2 = new Node(new IntState(3, 2, "P"));
        Node Q2 = new Node(new IntState(3, 3, "Q"));
        Node R2 = new Node(new IntState(3, 4, "R"));
        Node T2 = new Node(new IntState(4, 0, "T"));
        Node U2 = new Node(new IntState(4, 4, "U"));
        Node goal2 = new Node(new IntState(4, 3, "Goal"));

        start2.addChild(A2, 1);
        start2.addChild(D2, 2);
        A2.addChild(E2, 1);
        B2.addChild(C2, 1);
        B2.addChild(H2, 2);
        C2.addChild(I2, 1);
        E2.addChild(F2, 1);
        E2.addChild(K2, 1);
        D2.addChild(E2, 1);
        D2.addChild(J2, 1);
        F2.addChild(H2, 2);
        H2.addChild(I2, 1);
        H2.addChild(L2, 1);
        I2.addChild(M2, 1);
        J2.addChild(K2, 1);
        J2.addChild(N2, 2);
        K2.addChild(O2, 1);
        L2.addChild(M2, 1);
        L2.addChild(Q2, 1);
        M2.addChild(R2, 2);
        N2.addChild(O2, 1);
        N2.addChild(T2, 2);
        O2.addChild(P2, 2);
        P2.addChild(Q2, 1);
        Q2.addChild(R2, 2);
        Q2.addChild(goal2, 1);
        R2.addChild(U2, 2);

        return start2;
    }
}
