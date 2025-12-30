package uk.ac.hw.macs.search.example;

import uk.ac.hw.macs.search.State;

/**
 * A state representing a position in a 2D grid with coordinates (x, y).
 * It also includes goal-checking and heuristic (Manhattan distance).
 */
public class IntState implements State {
    private int x, y;
    private static int goalX, goalY;
    private String nodeLabel;

    public IntState(int x, int y, String nodeLabel) {
        this.x = x;
        this.y = y;
        this.nodeLabel = nodeLabel;
    }

    public static void setGoal(int goalX, int goalY) {
        IntState.goalX = goalX;
        IntState.goalY = goalY;
    }

    @Override
    public boolean isGoal() {
        return this.x == goalX && this.y == goalY;
    }

    @Override
    public int getHeuristic() {
        return Math.abs(this.x - goalX) + Math.abs(this.y - goalY);
    }

    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }

    @Override
    public String toString() {
        return nodeLabel;
    }
}
