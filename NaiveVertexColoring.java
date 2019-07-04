import java.util.*;

public class NaiveVertexColoring {
	private List<int[]> colorings;
	private int[][] graph;

	public NaiveVertexColoring(int[][] graph) {
		this.graph = graph;
		this.colorings = new ArrayList<>();
	}

	public List<int[]> getAllColorings() {
		int numberOfVertices = this.graph.length;
		// trivial case
		if (numberOfVertices == 1) {
			this.colorings.add(new int[] { 0 });
			return this.colorings;
		}

		int numberOfColors = Integer.MAX_VALUE;
		// first possible partition.
		int[] partition = new int[] { numberOfVertices - 1, 1 }; 
		int[] colors;

		// iterate over partitions
		while (partition != null) {
			if (partition.length > numberOfColors) {
				// all colorings with 'numberOfColors' amount of colors have been found.
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

	// input array must be in ascending order.
	private boolean getNextPermutation(int[] coloring) {
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

	private int[] getNextPartition(int[] array, int numberOfVertices) {
		if (array.length == numberOfVertices) {
			return null;
		}

		int[] copy = new int[array.length];
		System.arraycopy(array, 0, copy, 0, array.length);

		// check if difference of first and last element is less than 2.
		// this means that there are no more possible
		// partitions for the numberOfVertices and
		// numberOfColors in this iteration.
		if (copy[0] - copy[copy.length - 1] < 2) {
			// initialize arrayCopy with one more color.
			copy = new int[copy.length + 1];
			copy[0] = numberOfVertices;
			for (int j = copy.length - 1; j > 0; j--) {
				copy[j] = 1;
				copy[0]--;
			}
			return copy;
		}

		// iterate over arrayCopy to calculate next distribution.
		for (int j = 1; j < copy.length; j++) {
			// check if the difference between
			// the first and the j-th element
			// of the arrayCopy is bigger than 2.
			// this means that there exists
			// a possible next distribution.
			if (copy[0] - copy[j] >= 2) {
				copy[j]++;
				copy[j - 1]--;
				j--;
				// if arrayCopy is not in ascending order,
				// the distribution is not correct yet.
				while (!isInDescendingOrder(copy)) {
					if (copy[j] >= copy[j - 1]) {
						break;
					}
					copy[j]++;
					copy[j - 1]--;
					j--;
				}
				return copy;
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
		for (int i = 0; i < this.graph.length; i++) {
			for (int j = 0; j < this.graph[i].length; j++) {
				if (colors[i] == colors[this.graph[i][j]]) {
					return false;
				}
			}
		}
		return true;
	}

	public static void main(String[] args) { 
		// petersen graph: https://en.wikipedia.org/wiki/Petersen_graph
		int[][] adjacencyArray = new int[][] {
			new int[] { 1, 4, 5 },
			new int[] { 0, 2, 6 },
			new int[] { 1, 3, 7 },
			new int[] { 2, 4, 8 },
			new int[] { 0, 3, 9 },
			new int[] { 0, 7, 8 },
			new int[] { 1, 8, 9 },
			new int[] { 2, 5, 9 },
			new int[] { 3, 5, 6 },
			new int[] { 4, 6, 7 }
		};

		NaiveVertexColoring alg = new NaiveVertexColoring(adjacencyArray);
		System.out.println("3-coloring of Petersen graph:");
		for (int[] coloring : alg.getAllColorings()) {
			System.out.println(Arrays.toString(coloring));
		}
	}
}
