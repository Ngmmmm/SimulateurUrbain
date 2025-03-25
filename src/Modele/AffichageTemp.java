package Modele;

public class AffichageTemp {

    public static void affiche(Case [][] grid){
        for (int i = 0 ; i < grid.length ; i++){
            for (int j = 0; j < grid[0].length; j++){
                if (grid[i][j].toString().equals("Empty")){
                    System.out.print(" |");
                }else if(grid[i][j].toString().equals("Route")){
                    System.out.print("R|");
                }else if(grid[i][j].toString().equals("Voiture")){
                    System.out.print("V|");
                }else{
                    System.out.print(".|");
                }
            }
            System.out.println();
        }
    }


}
