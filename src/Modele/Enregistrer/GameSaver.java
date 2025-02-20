package Modele.Enregistrer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Modele.*;
import Modele.Signalling.*;
import Modele.Vehicle.Vehicle;

public class GameSaver {
    private static final String SAVE_DIRECTORY = "Save";
    private static final String SAVE_LIST_FILE = SAVE_DIRECTORY + File.separator + "nom_save.txt";

    public static boolean saveGame(String fileName, CityMap cityMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_DIRECTORY + "/" + fileName + ".txt"))) {
            // Sauvegarder les informations de la carte
            writer.write("Carte: " + cityMap.getRows() + "x" + cityMap.getCols());
            writer.newLine();

            // Sauvegarder chaque case de la grille
            for (int i = 0; i < cityMap.getRows(); i++) {
                for (int j = 0; j < cityMap.getCols(); j++) {
                    Case currentCase = cityMap.getGrid()[i][j];
                    writer.write("Case: " + i + "," + j);
                    writer.newLine();

                    // Sauvegarder la route
                    if (currentCase.getRoad() != null) {
                        Road road = currentCase.getRoad();
                        writer.write("Route: " + road.getLineType() + " " + arrayToString(road.getDirection()));
                        writer.newLine();
                    }

                    // Sauvegarder le bâtiment
                    if (currentCase.getBuilding() != null) {
                        writer.write("Batiment");
                        writer.newLine();
                    }

                    // Sauvegarder le véhicule
                    if (currentCase.getVehicle() != null) {
                        Vehicle vehicle = currentCase.getVehicle();
                        writer.write("Vehicule: " + vehicle.getId() + " " + vehicle.getPosition().getX() + "," + vehicle.getPosition().getY() + " " + vehicle.getDirection() + " " + vehicle.getFuel());
                        writer.newLine();
                    }

                    // Sauvegarder la signalisation
                    if (currentCase.getSignalling() != null) {
                        Signalling signalling = currentCase.getSignalling();
                        writer.write("Signalisation: " + signalling.getType() + " " + signalling.getPosition().getX() + "," + signalling.getPosition().getY());
                        writer.newLine();
                    }
                }
            }

            updateSaveList(fileName);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String arrayToString(Direction[] directions) {
        StringBuilder sb = new StringBuilder();
        for (Direction dir : directions) {
            sb.append(dir.toString()).append(" ");
        }
        return sb.toString().trim();
    }

    public static String generateUniqueSaveName(String baseName) {
        List<String> existingSaves = readExistingSaves();
        int maxNumber = 0;

        for (String save : existingSaves) {
            if (save.startsWith(baseName)) {
                try {
                    String numberPart = save.substring(baseName.length() + 1);
                    int number = Integer.parseInt(numberPart);
                    maxNumber = Math.max(maxNumber, number);
                } catch (NumberFormatException e) {
                    System.err.println("Erreur lors de l'extraction du numero de sauvegarde : " + save);
                }
            }
        }

        return baseName + "_" + (maxNumber + 1);
    }

    private static List<String> readExistingSaves() {
        List<String> saves = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(SAVE_LIST_FILE))) {
                saves = Files.readAllLines(Paths.get(SAVE_LIST_FILE));
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier des sauvegardes : " + SAVE_LIST_FILE);
            e.printStackTrace();
        }
        return saves;
    }

    private static void updateSaveList(String fileName) {
        try {
            File saveListFile = new File(SAVE_LIST_FILE);
            if (!saveListFile.exists()) {
                saveListFile.getParentFile().mkdirs();
                saveListFile.createNewFile();
            }

            List<String> existingSaves = Files.readAllLines(Paths.get(SAVE_LIST_FILE));

            if (!existingSaves.contains(fileName)) {
                try (FileWriter saveListWriter = new FileWriter(SAVE_LIST_FILE, true)) {
                    saveListWriter.write(fileName + "\n");
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de la mise a jour du fichier des noms de sauvegarde : " + SAVE_LIST_FILE);
            e.printStackTrace();
        }
    }
}