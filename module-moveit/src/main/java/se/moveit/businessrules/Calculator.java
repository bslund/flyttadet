package se.moveit.businessrules;

public class Calculator {

    /**
     * 
     * @param distance km
     * @param houseArea m2
     * @param otherArea m2
     * @param hasPiano
     * @return
     */
    public static int quote(int houseArea, int otherArea, int distance, boolean hasPiano) {
        final int volumePoints = volumePoints(houseArea, otherArea);
        final int noCars = numberOfCars(volumePoints);

        return noCars * pricePerCar(distance) + pricePiano(hasPiano);
    }

    protected static int pricePiano(boolean hasPiano) {
        return hasPiano ? 5000 : 0;
    }
    
    protected static int pricePerCar(int distance) {
        final int price;

        if (distance < 50)
            price = 1000 + 10*distance; 
        else if (distance >= 50 && distance < 100)
            price = 5000 + 8*distance;
        else // distance >= 100
            price = 10000 + 7*distance;

        return price;
    }
    
    protected static int volumePoints(int houseArea, int otherArea) {
        return houseArea + 2*otherArea;
    }
    
    protected static int numberOfCars(int volumePoints) {
        return (volumePoints + 50) / 50;
    }
}
