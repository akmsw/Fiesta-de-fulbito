/**
 * @author Bonino, Francisco Ignacio.
 * 
 * @version 1.0.0
 * 
 * @since 15/02/2021
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Scanner;

public class Main {

    // Campos privados.
    private static ArrayList<String> data;

    /**
     * Método principal.
     * 
     * Aquí se instancia y ejecuta todo el programa.
     */
    public static void main(String[] args) {
        Scanner monitorScan = new Scanner(System.in);

        EnumMap<Position, Integer> playersAmountMap = new EnumMap<>(Position.class);

        data = new ArrayList<>();

        System.out.print("Ingrese la cantidad de jugadores por equipo: ");

        int playersAmount = monitorScan.nextInt();

        try {
            collectPDData(String.valueOf(playersAmount));

            int index = 0;

            for (Position position : Position.values()) {
                playersAmountMap.put(position, Integer.parseInt(data.get(index)));
                index++;
            }

            playersAmountMap.forEach((key, value) -> System.out.println("POSICIÓN " + key + ": " + value));

            data.clear();

            MyLogger log = new MyLogger("useful/DISTRO.PDA");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        monitorScan.close();
    }

    // ----------------------------------------Métodos privados---------------------------------

    /**
     * Este método rescata la cantidad de jugadores para cada posición por equipo
     * mediante expresiones regulares.
     * 
     * [CLMFW].>+.[0-9] : Matchea las líneas que comiencen con C, L, M, F, ó W,
     * estén seguidas por al menos un caracter >, y luego tengan algún número.
     * 
     * [A-Z].>+. : Matchea el trozo de la línea que no es un número.
     * 
     * @param fileName Nombre del archivo a buscar.
     * 
     * @throws IOException Si el archivo no existe.
     */
    private static void collectPDData(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("useful/FDF_F" + fileName + ".PDA"))) {
            String line;

            while ((line = br.readLine()) != null)
                if (line.matches("[CLMFW].>+.[0-9]"))
                    data.add(line.replaceAll("[A-Z].>+.", ""));
        }
    }
}