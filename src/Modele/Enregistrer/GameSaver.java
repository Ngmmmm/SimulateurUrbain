package Modele.Enregistrer;

import Modele.*;
import Modele.Signalling.*;
import Modele.Vehicle.Vehicle;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GameSaver {
    private static final String SAVE_DIRECTORY = "Save";
    private static final String SAVE_LIST_FILE = SAVE_DIRECTORY + File.separator + "nom_save.txt";

    
    /** 
     * Sauvegarde l'état de la simulation à l'instant donné et renvoie true en cas de succès
     * @param fileName Nom à donner au fichier pour stocker la simulation
     * @param cityMap Carte à sauvegarder
     * @return boolean Réussite de la sauvegarde
     */
    public static boolean saveGame(String fileName, CityMap cityMap) {
        try {
            // Créer le répertoire de sauvegarde s'il n'existe pas
            Files.createDirectories(Paths.get(SAVE_DIRECTORY));

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
                            writer.write("Route: " + road.getLineType() + " " + road.isIntersection() + " " + arrayToString(road.getDirection()));
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
                            writer.write("Vehicule: " + vehicle.getClass().getSimpleName() + " " + vehicle.getId() + " " + vehicle.getPosition().getX() + "," + vehicle.getPosition().getY() + " " + vehicle.getDirection() + " " + vehicle.getSpeed() + " " + vehicle.getDestination().getX() + "," + vehicle.getDestination().getY());
                            writer.newLine();
                        }

                        // Sauvegarder la signalisation
                        if (currentCase.getSignalling() != null) {
                            Signalling signalling = currentCase.getSignalling();
                            writer.write("Signalisation: " + signalling.getType() + " " + signalling.getPosition().getX() + "," + signalling.getPosition().getY() + " " + signalling.getDirection());

                            // Si c'est un feu de signalisation, sauvegarder l'idGroupe
                            if (signalling instanceof TrafficLight trafficLight) {
                                writer.write(" " + trafficLight.getIDGroupe());
                            }

                            writer.newLine();
                        }
                    }
                }

                updateSaveList(fileName);
                return true;
            }
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

    /**
     * Modifie le nom de sauvegarde donné si il existe déjà
     * @param baseName nom de sauvegarde initial
     * @return String Correspond au nouveau nom de la sauvegarde
     */
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

    /**
     * Renvoie la liste des sauvegarde existante
     * @return List<String> des noms de sauvegarde
     */
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

    /**
     * Ajoute le nom de la nouvelle sauvegarde à la liste des noms de sauvegarde
     * @param fileName Nom de la nouvelle sauvegarde
     */
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
