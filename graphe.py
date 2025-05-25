import networkx as nx
import matplotlib.pyplot as plt

def read_adjacency_matrix(file_path):
    with open(file_path, 'r') as file:
        lines = file.readlines()

    adjacency_matrix = []
    for line in lines:
        row = list(map(int, line.strip().split()))
        adjacency_matrix.append(row)

    return adjacency_matrix

def visualize_graph_grid(adjacency_matrix, rows, cols):
    import networkx as nx
    import matplotlib.pyplot as plt

    G = nx.DiGraph()
    num_nodes = len(adjacency_matrix)

    # Ajouter les noeuds
    for i in range(num_nodes):
        G.add_node(i)

    # Ajouter les arêtes
    for i in range(num_nodes):
        for j in range(num_nodes):
            if adjacency_matrix[i][j] == 1:
                G.add_edge(i, j)

    # Créer un dictionnaire de positions basé sur la grille
    pos = {}
    for node in G.nodes():
        row = node // cols
        col = node % cols
        # Inverser l'axe y pour que 0,0 soit en haut à gauche
        pos[node] = (col, -row)  

    plt.figure(figsize=(12, 12))
    nx.draw(G, pos, with_labels=False, node_size=50, node_color='skyblue', arrowsize=10, edge_color='gray')
    plt.title("Graphe avec layout grille (50x50)")
    plt.axis('off')
    plt.show()

# Chemin vers le fichier contenant la matrice d'adjacence
file_path = 'adjacency_matrix.txt'

# Lire la matrice d'adjacence à partir du fichier
adjacency_matrix = read_adjacency_matrix(file_path)

# Visualiser le graphe
visualize_graph_grid(adjacency_matrix,50,50)
