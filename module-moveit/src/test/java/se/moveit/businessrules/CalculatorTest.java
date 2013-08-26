package se.moveit.businessrules;

import static org.junit.Assert.assertEquals;
import static se.moveit.businessrules.Calculator.numberOfCars;
import static se.moveit.businessrules.Calculator.pricePerCar;
import static se.moveit.businessrules.Calculator.pricePiano;
import static se.moveit.businessrules.Calculator.quote;
import static se.moveit.businessrules.Calculator.volumePoints;

import org.junit.Test;

public class CalculatorTest {

    @Test
    public void testQuote() {
        // kompletta exempel
        assertEquals(6350, quote(30, 0, 35, true));
        assertEquals(35880, quote(105, 15, 280, false));
        assertEquals(69200, quote(200, 25, 100, true));
        assertEquals(11200, quote(5, 45, 75, false));
    }
    
    @Test
    public void testPricePiano() {
        assertEquals(5000, pricePiano(true));
        assertEquals(0, pricePiano(false));
    }
    
    @Test
    public void testPricePerCar() {
        assertEquals(1100, pricePerCar(10));
        assertEquals(1490, pricePerCar(49));
        assertEquals(5400, pricePerCar(50));
        assertEquals(5408, pricePerCar(51));
        assertEquals(5792, pricePerCar(99));
        assertEquals(10700, pricePerCar(100));
    }
    
    @Test
    public void testNumberOfCars() {
        assertEquals(1, numberOfCars(volumePoints(49, 0)));
        assertEquals(2, numberOfCars(volumePoints(10, 25)));
        assertEquals(2, numberOfCars(volumePoints(50, 0)));
        assertEquals(3, numberOfCars(volumePoints(100, 0)));
        assertEquals(4, numberOfCars(volumePoints(150, 0)));
    }
}
