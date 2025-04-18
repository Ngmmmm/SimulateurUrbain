package Modele;

public class Stack2{
    
    private Position a;
    private Position b;

    public void add(Position c){
        b = a;
        a = c;
    }

    public boolean equal(){
        if (b == null) return false;
        return a == b;
    }

    public Position getA(){
        return a;
    }

    public Position getB(){
        return b;
    }

    public void clear(){
        a = null;
        b = null;
    }

    public boolean isEmpty(){
        return a == null;
    }
}
