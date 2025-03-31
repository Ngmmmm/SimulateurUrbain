package Modele;


import Modele.Signalling.Signalling;
import Modele.Vehicle.Vehicle;

public class Case{

    private Vehicle vehicle;
    private Building building;
    private Road road;
    private Signalling signalling;
    private Position pos;
    private int nbPassage = 0;

    public Case(){
        this.vehicle = null;
        this.building = null;
        this.road = null;
        this.signalling = null;
        this.pos = null;
    }

    // Pas fonctionnelle pour l'instant
    public Case(String type){
        this.vehicle = null;
        this.signalling = null;
        this.pos = null;
        switch (type) {
            case "Building" -> {
                this.building = new Building();
                this.road = null;
            }
            case "Road" -> //this.road = new Road();
                this.building = null;
            default -> {
                this.road = null;
                this.building = null;
            }
        }

    }

    public Case(Vehicle vehicle, Building building, Road road, Signalling signalling, Position pos){
        this.vehicle = vehicle;
        this.building = building;
        this.road = road;
        this.signalling = signalling;
        this.pos = pos;

        if(building != null && road != null){
            this.building = null;
        }
    }


    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Building getBuilding() {
        return this.building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Road getRoad() {
        return this.road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public Signalling getSignalling() {
        return this.signalling;
    }

    public void setSignalling(Signalling signalling) {
        this.signalling = signalling;
    }

    public Position getPos() {
        return this.pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public boolean isRoad(){
        return this.road != null;
    }
    public boolean isBuilding(){return this.building != null;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (vehicle != null) sb.append("Vehicle: ").append(vehicle.toString()).append(" ");
        if (road != null) sb.append("Road: ").append(road.toString()).append(" ");
        if (building != null) sb.append("Building: ").append(building.toString()).append(" ");
        if (signalling != null) sb.append("Signalling: ").append(signalling.getType()).append(" ");
        return sb.length() == 0 ? "Empty" : sb.toString();
    }

    
    public void update(Case tile){
        vehicle = tile.vehicle;
        building = tile.building;
        road = tile.road;
        signalling = tile.signalling;
        pos = tile.pos;
        
    }

    public boolean isEmpty(){
        return (this.vehicle == null && this.building == null);
    }

    public void addPassage(){
        nbPassage++;
    }




}
