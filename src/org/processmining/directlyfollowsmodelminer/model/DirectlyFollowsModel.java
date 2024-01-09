package org.processmining.directlyfollowsmodelminer.model;

import gnu.trove.set.TIntSet;

public interface DirectlyFollowsModel {

	/**
	 * 
	 * @param value
	 * @return The activity, or null if it does not exist.
	 */
	public String getNodeOfIndex(int value);

	/**
	 * 
	 * @return The number of nodes in the model.
	 */
	public int getNumberOfNodes();

	/**
	 * 
	 * @return The added node names, in the order they were added.
	 */
	public String[] getAllNodeNames();

	public Iterable<Integer> getNodeIndices();

	/**
	 * 
	 * @param nodeName
	 * @return The indices of the nodeName.
	 */
	public TIntSet getIndicesOfNodeName(String nodeName);

	/**
	 * Inserts an activity.
	 * 
	 * @param nodeName
	 * @return the index of the newly added node.
	 */
	public int addNode(String nodeName);

	/**
	 * Adds an array of nodes with the provided names. Typically more efficient
	 * than adding the nodes one-by-one.
	 * 
	 * @param nodeNames
	 */
	public void addNodes(String[] nodeNames);

	/**
	 * 
	 * @return Whether the model supports empty (epsilon) traces.
	 */
	public boolean isEmptyTraces();

	/**
	 * Set whether the model supports empty (epsilon) traces.
	 * 
	 * @param emptyTraces
	 */
	public void setEmptyTraces(boolean emptyTraces);

	/*
	 * === edges ===
	 */
	/**
	 * Adds an edge. Has no effect if the edge was already present.
	 * 
	 * @param sourceIndex
	 * @param targetIndex
	 */
	public void addEdge(int sourceIndex, int targetIndex);

	/**
	 * Removes an edge. Has no effect if the edge was not present.
	 * 
	 * @param sourceIndex
	 * @param targetIndex
	 */
	public void removeEdge(int sourceIndex, int targetIndex);

	/**
	 * Gives an iterable that iterates over all edges; The edges that are
	 * returned are indices. The iterable supports remove() calls, however calls
	 * to this method might invalidate other iterables.
	 * 
	 * @return
	 */
	public Iterable<Long> getEdges();

	/**
	 * Returns whether the graph contains an edge between source and target.
	 * 
	 * @param sourceIndex
	 * @param targetIndex
	 * @return
	 */
	public boolean containsEdge(int sourceIndex, int targetIndex);

	/**
	 * Returns the node the edgeIndex comes from.
	 * 
	 * @param edgeIndex
	 * @return
	 */
	public int getEdgeSource(long edgeIndex);

	/**
	 * Returns the node the edgeIndex points to.
	 * 
	 * @param edgeIndex
	 * @return
	 */
	public int getEdgeTarget(long edgeIndex);

	/*
	 * === start nodes ===
	 */
	public boolean hasStartNodes();

	/**
	 * Has no effect if the activity was already a start node.
	 * 
	 * @param nodeIndex
	 */
	public void addStartNode(int nodeIndex);

	public TIntSet getStartNodes();

	public int getNumberOfStartNodes();

	/*
	 * === end nodes ===
	 */
	public boolean hasEndNodes();

	/**
	 * Has no effect if the activity was already an end node.
	 * 
	 * @param nodeIndex
	 */
	public void addEndNode(int nodeIndex);

	public TIntSet getEndNodes();

	public int getNumberOfEndNodes();

	/**
	 * This method considers graphs equal when they are the same and the
	 * activities were added in the same order.
	 * 
	 * @param other
	 * @return
	 */
	public boolean equals(DirectlyFollowsModel other);

	public int hashCode();

	public DirectlyFollowsModel clone();

}
