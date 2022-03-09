package billydrivingschoolfmis;

import java.util.Random;

public class RandomNumbers {
    public static int randomNumber() {
        int min, max, randomNumber;
        Random rand = new Random();
        min = 1000;
        max = 100000;

        randomNumber = rand.nextInt((max - min) + 1) + min;
        return randomNumber;
    }
}
