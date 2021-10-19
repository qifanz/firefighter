package main.firefighters;

import main.api.Building;
import main.api.CityNode;
import main.api.Firefighter;

public class FirefighterImpl implements Firefighter {
  private CityNode location;
  private int distanceTraveled;

  public FirefighterImpl(Building fireStation) {
    this.location = fireStation.getLocation();
    this.distanceTraveled = 0;
  }

  public FirefighterImpl(Firefighter firefighter) {
    CityNode location = firefighter.getLocation();
    this.location = new CityNode(location.getX(), location.getY());
    this.distanceTraveled = firefighter.distanceTraveled();
  }

  @Override
  public CityNode getLocation() {
    return this.location;
  }

  @Override
  public int distanceTraveled() {
    return this.distanceTraveled;
  }

  @Override
  public int travelToNode(CityNode cityNode) {
    int travelled = (Math.abs(cityNode.getX() - location.getX()) + Math.abs(cityNode.getY() - location.getY()));
    distanceTraveled += travelled;
    this.location = cityNode;
    return travelled;
  }

  @Override
  public int revertToNode(CityNode cityNode) {
    int travelled = (Math.abs(cityNode.getX() - location.getX()) + Math.abs(cityNode.getY() - location.getY()));
    distanceTraveled -= travelled;
    this.location = cityNode;
    return travelled;
  }
}
