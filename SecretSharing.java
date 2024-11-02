import org.json.JSONObject;
import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.math.BigInteger;

public class SecretSharing {

    public static void main(String[] args) {
        try {
            // Read input from JSON file
            String jsonString = readJsonFromFile("input.json");
            JSONObject jsonObject = new JSONObject(jsonString);

            // Extract n and k
            JSONObject keys = jsonObject.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            // Prepare arrays for x and y values
            BigInteger[] xValues = new BigInteger[k];
            BigInteger[] yValues = new BigInteger[k];

            int index = 0;
            for (int i = 1; i <= n; i++) {
                if (jsonObject.has(String.valueOf(i))) {
                    JSONObject root = jsonObject.getJSONObject(String.valueOf(i));
                    String baseString = root.getString("base");
                    String valueString = root.getString("value");

                    // Decode the base and value
                    int base = Integer.parseInt(baseString);
                    BigInteger y = decodeValue(valueString, base);
                    BigInteger x = BigInteger.valueOf(i);

                    // Store the first k values
                    if (index < k) {
                        xValues[index] = x;
                        yValues[index] = y;
                        index++;
                    }
                }
            }

            // Calculate the constant term c using Lagrange interpolation
            BigInteger c = lagrangeInterpolation(xValues, yValues, BigInteger.ZERO);
            System.out.println("The secret (constant term c) is: " + c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readJsonFromFile(String filename) throws FileNotFoundException {
        StringBuilder jsonString = new StringBuilder();
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            jsonString.append(scanner.nextLine());
        }
        scanner.close();
        return jsonString.toString();
    }

    private static BigInteger decodeValue(String value, int base) {
        return new BigInteger(value, base);
    }

    private static BigInteger lagrangeInterpolation(BigInteger[] x, BigInteger[] y, BigInteger xValue) {
        BigInteger result = BigInteger.ZERO;
        int k = x.length;

        for (int i = 0; i < k; i++) {
            BigInteger term = y[i];
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term = term.multiply(xValue.subtract(x[j])).divide(x[i].subtract(x[j]));
                }
            }
            result = result.add(term);
        }
        return result;
    }
}