package org.processmining.directlyfollowsmodelminer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class DirectlyFollowsModelImplQuadratic implements DirectlyFollowsModel {
	private boolean emptyTraces;
	private boolean[][] edges; //matrix of edges
	private THashMap<String, TIntSet> external2internal;
	private ArrayList<String> internal2external;
	private TIntSet startActivities;
	private TIntSet endActivities;

	public DirectlyFollowsModelImplQuadratic() {
		emptyTraces = false;
		edges = new boolean[0][0];
		external2internal = new THashMap<>();
		internal2external = new ArrayList<>();
		startActivities = new TIntHashSet(10, 0.5f, -1);
		endActivities = new TIntHashSet(10, 0.5f, -1);
	}

	public String getNodeOfIndex(int value) {
		return internal2external.get(value);
	}

	public int getNumberOfNodes() {
		return internal2external.size();
	}

	public String[] getAllNodeNames() {
		String[] result = new String[internal2external.size()];
		return internal2external.toArray(result);
	}

	public Iterable<Integer> getNodeIndices() {
		return new Iterable<Integer>() {
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int now = -1;

					public Integer next() {
						now++;
						return now;
					}

					public boolean hasNext() {
						return now < internal2external.size() - 1;
					}
				};
			}
		};
	}

	public TIntSet getIndicesOfNodeName(String activity) {
		return external2internal.get(activity);
	}

	public int addNode(String activity) {
		int newIndex = internal2external.size();
		external2internal.putIfAbsent(activity, new TIntHashSet(1, 0.5f, -1));
		external2internal.get(activity).add(newIndex);
		internal2external.add(activity);

		int size = internal2external.size();
		boolean[][] newEdges = new boolean[size][];
		for (int i = 0; i < size; i++) {
			if (i < edges.length) {
				newEdges[i] = Arrays.copyOf(edges[i], size);
			} else {
				newEdges[i] = new boolean[size];
			}
		}
		edges = newEdges;

		return newIndex;
	}

	public void addNodes(String[] activities) {
		for (String activity : activities) {
			int newIndex = internal2external.size();
			external2internal.putIfAbsent(activity, new TIntHashSet(1, 0.5f, -1));
			external2internal.get(activity).add(newIndex);
			internal2external.add(activity);
		}

		int size = internal2external.size();
		boolean[][] newEdges = new boolean[size][];
		for (int i = 0; i < size; i++) {
			if (i < edges.length) {
				newEdges[i] = Arrays.copyOf(edges[i], size);
			} else {
				newEdges[i] = new boolean[size];
			}
		}
		edges = newEdges;
	}

	public boolean isEmptyTraces() {
		return emptyTraces;
	}

	public void setEmptyTraces(boolean emptyTraces) {
		this.emptyTraces = emptyTraces;
	}

	public void addEdge(int sourceIndex, int targetIndex) {
		edges[sourceIndex][targetIndex] = true;
	}

	public void removeEdge(int sourceIndex, int targetIndex) {
		edges[sourceIndex][targetIndex] = false;
	}

	public Iterable<Long> getEdges() {
		return new Iterable<Long>() {
			public Iterator<Long> iterator() {
				return new EdgeIterator();
			}
		};
	}

	public class EdgeIterator implements Iterator<Long> {
		int currentIndex = 0;
		int nextIndex = 0;

		public EdgeIterator() {
			//walk to the first non-zero edge
			while (currentIndex < internal2external.size() * internal2external.size()
					&& !edges[currentIndex / internal2external.size()][currentIndex % internal2external.size()]) {
				currentIndex++;
			}
			//and to the next
			nextIndex = currentIndex;
			while (nextIndex < internal2external.size() * internal2external.size()
					&& !edges[nextIndex / internal2external.size()][nextIndex % internal2external.size()]) {
				nextIndex++;
			}
		}

		public void remove() {
			edges[getEdgeSource(currentIndex)][getEdgeTarget(currentIndex)] = false;
		}

		public Long next() {
			currentIndex = nextIndex;
			nextIndex++;
			while (nextIndex < internal2external.size() * internal2external.size()
					&& !edges[nextIndex / internal2external.size()][nextIndex % internal2external.size()]) {
				nextIndex++;
			}
			return (long) currentIndex;
		}

		public boolean hasNext() {
			return nextIndex < internal2external.size() * internal2external.size();
		}
	}

	public boolean containsEdge(int sourceIndex, int targetIndex) {
		return edges[sourceIndex][targetIndex];
	}

	public int getEdgeSource(long edgeIndex) {
		return (int) (edgeIndex / internal2external.size());
	}

	public int getEdgeTarget(long edgeIndex) {
		return (int) (edgeIndex % internal2external.size());
	}

	public boolean hasStartNodes() {
		return !startActivities.isEmpty();
	}

	public TIntSet getStartNodes() {
		return startActivities;
	}

	public int getNumberOfStartNodes() {
		return startActivities.size();
	}

	public void addStartNode(int activity) {
		startActivities.add(activity);
	}

	public boolean hasEndNodes() {
		return !endActivities.isEmpty();
	}

	public TIntSet getEndNodes() {
		return endActivities;
	}

	public int getNumberOfEndNodes() {
		return endActivities.size();
	}

	public void addEndNode(int activity) {
		endActivities.add(activity);
	}

	public DirectlyFollowsModel clone() {
		DirectlyFollowsModelImplQuadratic result;
		try {
			result = (DirectlyFollowsModelImplQuadratic) super.clone();
			result.internal2external = new ArrayList<>(internal2external);
			result.external2internal = new THashMap<>();
			for (Entry<String, TIntSet> entry : external2internal.entrySet()) {
				result.external2internal.put(entry.getKey(), new TIntHashSet(entry.getValue()));
			}
			result.edges = new boolean[edges.length][];
			for (int i = 0; i < edges.length; i++) {
				result.edges[i] = new boolean[edges[i].length];
				System.arraycopy(edges[i], 0, result.edges[i], 0, edges[i].length);
			}
			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}

	}

	public boolean equals(DirectlyFollowsModel other) {
		/**
		 * empty traces
		 */
		if (isEmptyTraces() != other.isEmptyTraces()) {
			return false;
		}

		/**
		 * activities
		 */
		if (!Arrays.equals(getAllNodeNames(), other.getAllNodeNames())) {
			return false;
		}

		/**
		 * edges
		 */
		for (long edge : getEdges()) {
			int source = getEdgeSource(edge);
			int target = getEdgeTarget(edge);
			if (!other.containsEdge(source, target)) {
				return false;
			}
		}
		for (long edge : other.getEdges()) {
			int source = other.getEdgeSource(edge);
			int target = other.getEdgeTarget(edge);
			if (!containsEdge(source, target)) {
				return false;
			}
		}

		/**
		 * start activities
		 */
		if (!getStartNodes().equals(other.getStartNodes())) {
			return false;
		}

		/**
		 * end activities
		 */
		if (!getEndNodes().equals(other.getEndNodes())) {
			return false;
		}

		return true;
	}

}
