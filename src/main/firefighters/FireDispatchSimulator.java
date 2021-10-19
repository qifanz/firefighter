package main.firefighters;

import main.api.CityNode;
import main.api.Firefighter;

import java.util.*;

public class FireDispatchSimulator {
    /**
     * Our goal is the find an optimal path, or, find a Minimum Spanning Tree where the vertices are firefighters + fire nodes with constraints:
     * 1. fire nodes can not be connected to no more than 2 other vertices (otherwise obviously not optimal)
     * 2. firefighter vertices can be connected to no more than (no. of firefighters at that node) vertices, otherwise lack of firefighter :)
     * I cannot think of any other better algorithm to guarantee the optimality other than enumerating over all possibilities with some branch cutting
     * approximation algorithm can work, but we do not want to go that far
     * @param firefighters
     * @param burningBuildings
     * @return the optimal path for each firefighter, without modifying the firefighters passed in
     */
    public static Map<Firefighter, List<CityNode>> solve(List<Firefighter> firefighters, CityNode... burningBuildings) {
        List<CityNode> destinations = new ArrayList<>(Arrays.asList(burningBuildings));
        // to save current path during exploration
        Map<Firefighter, LinkedList<CityNode>> path = new HashMap<>();
        // to save optimal path
        Map<Firefighter, List<CityNode>> optimalPaths = new HashMap<>();
        List<Firefighter> simulationFirefighters = new ArrayList<>();
        // mapping from "simulation" firefighters to real firefighters
        Map<Firefighter, Firefighter> simulationMapping = new HashMap<>();
        for (Firefighter firefighter : firefighters) {
            // we create a "simulation" firefighter for each real firefighter to avoid touching the real firefighter objects
            Firefighter simulationFirefighter = new FirefighterImpl(firefighter);
            simulationFirefighters.add(simulationFirefighter);
            // create an empty path as starting point
            path.put(simulationFirefighter, new LinkedList<>());
            // create a mapping to map back to real firefighters at the end
            simulationMapping.put(simulationFirefighter, firefighter);
        }
        Set<Integer> unvisitedDestinations = new HashSet<>();
        for (int i = 0; i < destinations.size(); i++) unvisitedDestinations.add(i);

        backtracking(unvisitedDestinations, 0, simulationFirefighters, path, optimalPaths, Integer.MAX_VALUE, destinations, simulationMapping);

        return optimalPaths;
    }

    // complexity (O(n^m * m!)) where n is the number of firefighters and m is the number of destinations
    private static int backtracking(Set<Integer> unvisitedDestinationIndices,
                                    int currentDistance, List<Firefighter> firefighters,
                                    Map<Firefighter, LinkedList<CityNode>> path,
                                    Map<Firefighter, List<CityNode>> optimalPaths,
                                    int minDistance,
                                    List<CityNode> destinations,
                                    Map<Firefighter, Firefighter> simulationMapping) {
        // cut branch if the current distance is already too big.
        if (currentDistance > minDistance) return minDistance;

        // if we are at the end, check if we want to replace the best solution
        if (unvisitedDestinationIndices.isEmpty()) {
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                setOptimalPaths(path, optimalPaths, simulationMapping);
            }
            return minDistance;
        }

        // else we need to continue on backtracking
        for (int nextDestinationIndex : new ArrayList<>(unvisitedDestinationIndices)) { // to avoid concurrent modification exception
            for (Firefighter firefighter : firefighters) {
                // prepare for the next iteration
                CityNode nextDestination = destinations.get(nextDestinationIndex);
                CityNode currentLocation = firefighter.getLocation();
                unvisitedDestinationIndices.remove(nextDestinationIndex);
                path.get(firefighter).addLast(nextDestination);

                // recursive call
                minDistance = Math.min(minDistance,
                        backtracking(unvisitedDestinationIndices,
                                currentDistance + firefighter.travelToNode(nextDestination),
                                firefighters,
                                path,
                                optimalPaths,
                                minDistance,
                                destinations,
                                simulationMapping)
                );

                // revert and continue to next possibility
                firefighter.revertToNode(currentLocation);
                path.get(firefighter).removeLast();
                unvisitedDestinationIndices.add(nextDestinationIndex);
            }
        }
        return minDistance;
    }

    private static void setOptimalPaths(Map<Firefighter, LinkedList<CityNode>> path, Map<Firefighter, List<CityNode>> optimalPaths, Map<Firefighter, Firefighter> simulationMapping) {
        optimalPaths.clear();
        for (Firefighter firefighter : path.keySet()) {
            // deep copy to the correct real firefighter here
            optimalPaths.put(simulationMapping.get(firefighter), new LinkedList<>(path.get(firefighter)));
        }
    }
}
