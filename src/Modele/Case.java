package Modele;

import Modele.Signalling.Signalling;
import Modele.Vehicle.Vehicle;

public class Case {

    private Vehicle vehicle;
    private Building building;
    private Road road;
    private Signalling signalling;
    private Position pos;

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
        if(type.equals("Building")){
            this.building = new Building();
            this.road = null;
        }
        else if (type.equals("Road")){
            //this.road = new Road();
            this.building = null;
        }else{
            this.road = null;
            this.building = null;
        }

    }

    public Case(Vehicle vehicle, Building building, Road road, Signalling signalling, Position pos){
        this.vehicle = vehicle;
        this.building = building;
        this.road = road;
        this.signalling = signalling;
        this.pos = pos;
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

}
