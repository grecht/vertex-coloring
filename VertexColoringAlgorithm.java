import java.util.*;

/**
 * A brute-force vertex coloring algorithm which determines all 
 * non-equivalent colorings of an undirected graph using a minimal 
 * amount of colors.
 * 
 * This algorithm was origininally developed for a class project 
 * (https://github.com/DifferentLink/Grape). 
 */ 
public class VertexColoringAlgorithm {
	private List<int[]> colorings;
	private int[][] adjacencyMatrix;
	private int largestCliqueSize;
	
	public VertexColoringAlgorithm(int[][] adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
		this.largestCliqueSize = 0;
		this.colorings = new ArrayList<>();
	}
	
	public VertexColoringAlgorithm(int[][] adjacencyMatrix, int largestCliqueSize) {
		this.adjacencyMatrix = adjacencyMatrix;
		this.largestCliqueSize = largestCliqueSize;	
		this.colorings = new ArrayList<>();
	}
	
	public List<int[]> getAllColorings() {
		int numberOfVertices = this.adjacencyMatrix[0].length;		
		// trivial case
		if (numberOfVertices == 1) {
			this.colorings.add(new int[]{ 0 });
			return this.colorings;
		}
		
		int numberOfColors = Integer.MAX_VALUE;
		int[] partition = getFirstPartition(numberOfVertices, largestCliqueSize);
		int[] colors;
		
		// iterate over partitions
		while (partition != null) {
			if (partition.length > numberOfColors) {
				// all colorings with numberOfColors have been found.
				break;
			}			
			colors = this.partitionToColoring(partition, numberOfVertices);
			do {
				if (isValidVertexColoring(colors)) {
					// found one valid coloring for this partition.
					this.colorings.add(colors);
					numberOfColors = partition.length;
					break;
				}
			} while (getNextPermutation(colors));
			partition = getNextPartition(partition, numberOfVertices);		
		}	
		return this.colorings;		
	}
	
	// todo: simplify this.
	private int[] partitionToColoring(int[] partition, int numberOfVertices) {
		int[] result = new int[numberOfVertices];
		int index = 0;
		for (int i = 0; i < partition.length; i++) {
			for (int j = index; j < result.length; j++) {
				result[j] = i;
			}
			index += partition[i];
		}
		return result;
	}
	
	// input array must be of ascending order
	public boolean getNextPermutation(int[] coloring) {
		int i = coloring.length - 1;
		while (i > 0 && coloring[i - 1] >= coloring[i]) {
			i--;
		}
		if (i <= 0) {
			// last permutation
			return false;
		}
		int j = coloring.length - 1;
		while (coloring[j] <= coloring[i - 1]) {
			j--;
		}

		// swap
		int tmp = coloring[i - 1];
		coloring[i - 1] = coloring[j];
		coloring[j] = tmp;

		j = coloring.length - 1;
		while (i < j) {
			tmp = coloring[i];
			coloring[i] = coloring[j];
			coloring[j] = tmp;
			i++;
			j--;
		}
		return true;
	}
	
	private int[] getFirstPartition(int numberOfVertices, int largestCliqueSize) {
		int[] result = new int[largestCliqueSize];
		result[0] = numberOfVertices - (largestCliqueSize - 1);
		for (int i = 1; i < result.length; i++) {
			result[i] = 1;
		}
		return result;
	}
	
	// todo: simplify this.
	private int[] getNextPartition(int[] array, int numberOfVertices) {
		if (array.length == numberOfVertices) {
			return null;
		}

		int[] arrayCopy = new int[array.length];
		System.arraycopy(array, 0, arrayCopy, 0, array.length);

		// check if first element equals last element
		// or if their difference is less than 2.
		// this means that there are no more possible
		// partitions for the numberOfVertices and
		// numberOfColors in this iteration.
		if (arrayCopy[0] == arrayCopy[arrayCopy.length - 1]
				|| arrayCopy[0] - arrayCopy[arrayCopy.length - 1] < 2) {
			// initialize arrayCopy with one more color.
			arrayCopy = new int[arrayCopy.length + 1];
			int tmp = numberOfVertices;
			for (int j = arrayCopy.length - 1; j > 0; j--) {
				arrayCopy[j] = 1;
				tmp--;
			}
			arrayCopy[0] = tmp;
			return arrayCopy;
		}

		// iterate over arrayCopy to calculate
		// next distribution.
		for (int j = 1; j < arrayCopy.length; j++) {
			// check if the difference between
			// the first and the j-th element
			// of the arrayCopy is bigger than 2.
			// this means that there exists
			// a possible next distribution.
			if (arrayCopy[0] - arrayCopy[j] >= 2) {
				arrayCopy[j]++;
				arrayCopy[j - 1]--;
				j--;
				// if arrayCopy is not in ascending order,
				// the distribution is not correct yet.
				while (!isInDescendingOrder(arrayCopy)) {
					if (arrayCopy[j] >= arrayCopy[j - 1]) {
						break;
					}
					arrayCopy[j]++;
					arrayCopy[j - 1]--;
					j--;
				}
				return arrayCopy;
			}
		}
		return null;
	}
	
	private boolean isInDescendingOrder(int[] array) {
		for (int i = 0; i < array.length - 1; i++) {
			if (array[i] < array[i + 1]) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isValidVertexColoring(int[] colors) {
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = i + 1; j < adjacencyMatrix.length; j++) {
				if (adjacencyMatrix[i][j] == 1 && colors[i] == colors[j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		// adjacency matrix should be symmetrical
		// as it's representing an undirected graph.

		// petersen graph: https://en.wikipedia.org/wiki/Petersen_graph
		int[][] petersenGraph = new int[10][10];
		petersenGraph[0] = new int[] { 0, 1, 0, 0, 1, 1, 0, 0, 0, 0};
		petersenGraph[1] = new int[] { 1, 0, 1, 0, 0, 0, 1, 0, 0, 0};
		petersenGraph[2] = new int[] { 0, 1, 0, 1, 0, 0, 0, 1, 0, 0};
		petersenGraph[3] = new int[] { 0, 0, 1, 0, 1, 0, 0, 0, 1, 0};
		petersenGraph[4] = new int[] { 1, 0, 0, 1, 0, 0, 0, 0, 0, 1};
		petersenGraph[5] = new int[] { 1, 0, 0, 0, 0, 0, 0, 1, 1, 1};
		petersenGraph[6] = new int[] { 0, 1, 0, 0, 0, 0, 0, 0, 1, 1};
		petersenGraph[7] = new int[] { 0, 0, 1, 0, 0, 1, 0, 0, 0, 1};
		petersenGraph[8] = new int[] { 0, 0, 0, 1, 0, 1, 1, 0, 0, 0};
		petersenGraph[9] = new int[] { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0};	
		
		VertexColoringAlgorithm alg2 = new VertexColoringAlgorithm(petersenGraph, 1);
		List<int[]> colorings2 = alg2.getAllColorings();
		System.out.println("3-coloring of Petersen graph:");
		for (int[] coloring : colorings2) {
			System.out.println(Arrays.toString(coloring));
		}
	}
}
