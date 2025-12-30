package uk.ac.hw.macs.search;

import java.util.*;
	public class SearchProblem {
		private SearchOrder searchOrder;
		private List<Node> expandedStatesOrder;

		public SearchProblem(SearchOrder searchOrder) {
			this.searchOrder = searchOrder;
			this.expandedStatesOrder = new ArrayList<>();
		}

		public boolean doSearch(Node root) {
			List<FringeNode> fringe = new LinkedList<>();
			fringe.add(new FringeNode(root, null, 0));
			Set<Node> visitedStates = new HashSet<>();
			FringeNode goalNode = null;
			int step = 1;

			System.out.println("\n=== Detailed Search Process ===");

			while (true) {
				if (fringe.isEmpty()) {
					break;
				}

				FringeNode searchNode = fringe.remove(0);
				// Original output
				System.out.println("Current node: " + searchNode);

				// Detailed calculations
				System.out.println("\n--- Step " + step + " Detailed Analysis ---");
				System.out.println("Expanding State: " + searchNode.node.getValue());
				System.out.println("Current g-value (cost from start): " + searchNode.gValue);
				System.out.println("Current h-value (heuristic to goal): " +
						searchNode.node.getValue().getHeuristic());
				System.out.println("Current f-value (g + h): " + searchNode.getFValue());

				// Show current fringe state
				System.out.println("\nCurrent Fringe before expansion:");
				printFringe(fringe);

				if (visitedStates.contains(searchNode.node)) {
					System.out.println("\nState already visited - skipping expansion");
					continue;
				}

				// Add to expanded states list when actually expanding
				expandedStatesOrder.add(searchNode.node);

				if (searchNode.node.isGoal()) {
					goalNode = searchNode;
					System.out.println("\nGoal state reached!");
					break;
				}

				// Show children and their evaluations
				System.out.println("\nExpanding current state. Available moves:");
				for (ChildWithCost child : searchNode.node.getChildren()) {
					int newGValue = searchNode.gValue + child.cost;
					int hValue = child.node.getValue().getHeuristic();
					System.out.println(String.format(
							"Move to %s: g=%d, h=%d, f=%d, Cost=%d",
							child.node.getValue(),
							newGValue,
							hValue,
							newGValue + hValue,
							child.cost
					));
				}

				searchOrder.addToFringe(fringe, searchNode, searchNode.node.getChildren());
				visitedStates.add(searchNode.node);

				// Show updated fringe
				System.out.println("\nFringe after expansion (sorted by f-value):");
				printFringe(fringe);

				// Show expanded states in order
				System.out.println("\nStates expanded so far (in order):");
				printExpandedStatesInOrder();

				System.out.println("\n" + "=".repeat(50));
				step++;
			}

			if (goalNode == null) {
				System.out.println("No goal found");
				return false;
			} else {
				printSolution(goalNode);
				return true;
			}
		}

		private void printFringe(List<FringeNode> fringe) {
			if (fringe.isEmpty()) {
				System.out.println("Empty fringe");
				return;
			}
			for (int i = 0; i < fringe.size(); i++) {
				FringeNode node = fringe.get(i);
				System.out.println(String.format(
						"[%d] State=%s, g=% d, h=%d, f=%d",
						i + 1,
						node.node.getValue(),
						node.gValue,
						node.node.getValue().getHeuristic(),
						node.getFValue()
				));
			}
		}

		private void printExpandedStatesInOrder() {
			for (int i = 0; i < expandedStatesOrder.size(); i++) {
				Node node = expandedStatesOrder.get(i);
				System.out.println(String.format(
						"[%d] State=%s",
						i + 1,
						node.getValue()
				));
			}
		}

		private void printSolution(FringeNode goalNode) {
			System.out.println("Found goal node: " + goalNode.node.getValue());
			System.out.println("Cost: " + goalNode.gValue);
			System.out.println("Nodes expanded: " + expandedStatesOrder.size());

			// Create a list to store the path
			List<FringeNode> path = new ArrayList<>();
			FringeNode current = goalNode;

			// Build the path from goal to start
			while (current != null) {
				path.add(current);
				current = current.parent;
			}

			// Print the path from start to goal
			System.out.println("Path from start to goal:");
			for (int i = path.size() - 1; i >= 0; i--) {
				FringeNode node = path.get(i);
				System.out.println(String.format(
						"Step %d: node=%s (g=%d)",
						path.size() - i,
						node.node.getValue(),
						node.gValue
				));
			}
		}
	}