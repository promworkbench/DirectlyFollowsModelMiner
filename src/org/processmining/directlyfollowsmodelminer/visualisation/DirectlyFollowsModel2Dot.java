package org.processmining.directlyfollowsmodelminer.visualisation;

import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotNode;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class DirectlyFollowsModel2Dot {

	public static Pair<Dot, TIntObjectMap<DotNode>> visualise(DirectlyFollowsModel dfm) {
		Dot result = new Dot();

		TIntObjectMap<DotNode> activity2dotNode = new TIntObjectHashMap<>(10, 0.5f, -1);
		for (int activityIndex : dfm.getNodeIndices()) {
			DotNode node = result.addNode(dfm.getNodeOfIndex(activityIndex));
			activity2dotNode.put(activityIndex, node);

			node.setOption("shape", "box");

			//determine node colour using start and end activities
			if (dfm.getStartNodes().contains(activityIndex) && dfm.getEndNodes().contains(activityIndex)) {
				node.setOption("style", "filled");
				node.setOption("fillcolor", "green:red");
			} else if (dfm.getStartNodes().contains(activityIndex)) {
				node.setOption("style", "filled");
				node.setOption("fillcolor", "green:white");
			} else if (dfm.getEndNodes().contains(activityIndex)) {
				node.setOption("style", "filled");
				node.setOption("fillcolor", "white:red");
			}
		}

		for (long edgeIndex : dfm.getEdges()) {
			int source = dfm.getEdgeSource(edgeIndex);
			int target = dfm.getEdgeTarget(edgeIndex);
			result.addEdge(activity2dotNode.get(source), activity2dotNode.get(target));
		}

		if (dfm.isEmptyTraces()) {
			DotNode node = result.addNode("");
			node.setOption("style", "filled");
			node.setOption("fillcolor", "green:red");
		}

		return Pair.of(result, activity2dotNode);

	}

}
