package main.scenarios;

import main.api.*;
import main.api.exceptions.FireproofBuildingException;
import main.impls.CityImpl;
import org.junit.Assert;
import org.junit.Test;

public class CustomizedScenarios {

    /**
     * This test case shows that naive greedy algorithm does not work
     * @throws FireproofBuildingException
     */
    @Test
    public void twoFireOneFireFighter() throws FireproofBuildingException {
        City basicCity = new CityImpl(5, 5, new CityNode(0, 0));
        FireDispatch fireDispatch = basicCity.getFireDispatch();


        CityNode[] fireNodes = {
                new CityNode(0, 2),
                new CityNode(0, 1)};
        Pyromaniac.setFires(basicCity, fireNodes);

        fireDispatch.setFirefighters(1);
        fireDispatch.dispatchFirefighers(fireNodes);

        Firefighter firefighter = fireDispatch.getFirefighters().get(0);
        Assert.assertEquals(2, firefighter.distanceTraveled());
        Assert.assertEquals(fireNodes[0], firefighter.getLocation());

        for (CityNode cityNode : fireNodes) {
            Assert.assertFalse(basicCity.getBuilding(cityNode).isBurning());
        }
    }

    @Test
    /**
     * This test case shows that greedy algorithms with improvements e.g. Kruskal's algorithm will not work
     * The order of visiting is critical to reach an optimal solution
     */
    public void twoFireTwoFireFighters() throws FireproofBuildingException {
        City basicCity = new CityImpl(12, 12, new CityNode(0, 0));
        FireDispatch fireDispatch = basicCity.getFireDispatch();


        CityNode[] fireNodes = {
                new CityNode(3, 3),
                new CityNode(2, 7),
                new CityNode(10, 3)
        };
        Pyromaniac.setFires(basicCity, fireNodes);

        fireDispatch.setFirefighters(2);
        fireDispatch.dispatchFirefighers(fireNodes);

        int totalDistance = 0;
        for (Firefighter firefighter: fireDispatch.getFirefighters()) {
            totalDistance += firefighter.distanceTraveled();
        }
        // the optimal path is 0 -> (2, 7) -> (3, 3) -> (10, 3)
        Assert.assertEquals(21, totalDistance);
        for (CityNode cityNode : fireNodes) {
            Assert.assertFalse(basicCity.getBuilding(cityNode).isBurning());
        }
    }

    /**
     * Test 2 batches of fire alerts
     * @throws FireproofBuildingException
     */
    @Test
    public void multiFire() throws FireproofBuildingException {
        City basicCity = new CityImpl(12, 12, new CityNode(0, 0));
        FireDispatch fireDispatch = basicCity.getFireDispatch();


        CityNode[] fireNodes = {
                new CityNode(3, 3),
                new CityNode(2, 7),
                new CityNode(10, 3)
        };
        Pyromaniac.setFires(basicCity, fireNodes);
        fireDispatch.setFirefighters(1);
        fireDispatch.dispatchFirefighers(fireNodes);

        // receive some new fire alerts here
        CityNode[] newFireNodes = {
                new CityNode(11, 4),
                new CityNode(11, 7)
        };
        Pyromaniac.setFires(basicCity, newFireNodes);
        fireDispatch.dispatchFirefighers(newFireNodes);

        // previous test case 21, + additional 10,3 -> 11,4 and 11,4 -> 11,7
        Assert.assertEquals(fireDispatch.getFirefighters().get(0).distanceTraveled(), 26);
        for (CityNode cityNode : fireNodes) {
            Assert.assertFalse(basicCity.getBuilding(cityNode).isBurning());
        }
        for (CityNode cityNode : newFireNodes) {
            Assert.assertFalse(basicCity.getBuilding(cityNode).isBurning());
        }
    }
}
