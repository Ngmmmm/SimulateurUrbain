package Modele;

import java.util.ArrayList;
import java.util.List;

class MinHeap {
    private List<Position> heap; // Tableau pour stocker les positions
    private double[][] distances; // Référence à la matrice des distances

    public MinHeap(double[][] distances) {
        this.heap = new ArrayList<>();
        this.distances = distances;
    }

    // Insérer une position dans le tas
    public void insert(Position position) {
        heap.add(position);
        int currentIndex = heap.size() - 1;
        bubbleUp(currentIndex);
    }

    // Retirer et retourner la position avec la plus petite distance
    public Position extractMin() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        Position min = heap.get(0);
        Position last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapify(0);
        }
        return min;
    }

    // Faire remonter un élément pour maintenir la propriété du tas
    private void bubbleUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (getDistance(heap.get(index)) >= getDistance(heap.get(parentIndex))) {
                break;
            }
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    // Réorganiser le tas à partir d'un index donné
    private void heapify(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int smallest = index;

        if (leftChild < heap.size() && getDistance(heap.get(leftChild)) < getDistance(heap.get(smallest))) {
            smallest = leftChild;
        }
        if (rightChild < heap.size() && getDistance(heap.get(rightChild)) < getDistance(heap.get(smallest))) {
            smallest = rightChild;
        }
        if (smallest != index) {
            swap(index, smallest);
            heapify(smallest);
        }
    }

    // Échanger deux éléments dans le tas
    private void swap(int i, int j) {
        Position temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    // Obtenir la distance d'une position
    private double getDistance(Position position) {
        return distances[position.getY()][position.getX()];
    }

    // Vérifier si le tas est vide
    public boolean isEmpty() {
        return heap.isEmpty();
    }
}