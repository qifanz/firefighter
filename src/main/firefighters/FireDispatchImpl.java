package main.firefighters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.api.City;
import main.api.CityNode;
import main.api.FireDispatch;
import main.api.Firefighter;
import main.api.exceptions.NoFireFoundException;

public class FireDispatchImpl implements FireDispatch {
  private City city;
  private List<Firefighter> firefighters;

  public FireDispatchImpl(City city) {
    this.city = city;
    this.firefighters = new ArrayList<>();
  }

  @Override
  public void setFirefighters(int numFirefighters) {
    for (int i = 0; i < numFirefighters; i++) {
      firefighters.add(new FirefighterImpl(this.city.getFireStation()));
    }
  }

  @Override
  public List<Firefighter> getFirefighters() {
    return this.firefighters;
  }

  @Override
  public void dispatchFirefighers(CityNode... burningBuildings) {
    // step 1. simulate to get the optimal paths for each firefighter, note that the order of the city nodes count
    Map<Firefighter, List<CityNode>> optimalPaths = FireDispatchSimulator.solve(this.firefighters, burningBuildings);

    // step 2. actually let the firefighters move and extinguish fire
    for (Firefighter firefighter : optimalPaths.keySet()) {
      for (CityNode cityNode : optimalPaths.get(firefighter)) {
        firefighter.travelToNode(cityNode);
        try {
          this.city.getBuilding(cityNode).extinguishFire();
        } catch (NoFireFoundException e) {
          // simple error handling here
          System.err.println("Error no fire at this node" + cityNode.toString());
        }
      }
    }
  }
}
