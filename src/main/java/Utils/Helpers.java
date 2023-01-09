package Utils;

import com.fasterxml.uuid.Generators;

import java.util.Random;
import java.util.UUID;

public class Helpers {

    public static UUID randomUUID(){
        return Generators.randomBasedGenerator().generate();
    }
    public static double randomPrize() {
        return getRandomNumber(10000,100000);
    }
    public static int randomYear() {
        return getRandomNumber(1990,2023);
    }
    public static int randomVat() {
        int[] vats = new int[]{0,7,22};
        return vats[getRandomNumber(0,2)];
    }

    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
