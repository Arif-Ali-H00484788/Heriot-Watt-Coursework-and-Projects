package uk.ac.hw.macs.search.example;

import java.util.List;
import java.util.Set;
import uk.ac.hw.macs.search.ChildWithCost;
import uk.ac.hw.macs.search.FringeNode;
import uk.ac.hw.macs.search.SearchOrder;

import java.util.Collections;

public class AStarSearchOrder implements SearchOrder {
    @Override
    public void addToFringe(List<FringeNode> frontier, FringeNode parent, Set<ChildWithCost> children) {
        for (ChildWithCost child : children) {
            FringeNode newNode = new FringeNode(child.node, parent, child.cost);
            frontier.add(newNode);
        }
        // Sort frontier by f-values (g + h)
        Collections.sort(frontier, (a, b) -> a.getFValue() - b.getFValue());
    }
}
