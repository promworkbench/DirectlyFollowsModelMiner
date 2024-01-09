package org.processmining.directlyfollowsmodelminer.mining;

import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModelImplQuadratic;
import org.processmining.framework.util.ArrayUtils;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class DfgMsd2Dfm {
	public static DirectlyFollowsModel convert(DfgMsd in) {
		DirectlyFollowsModel result = new DirectlyFollowsModelImplQuadratic();

		/**
		 * Activities. Filter out activities without edges and that are not
		 * start or end activities.
		 */
		String[] newActivities = new String[0];
		TIntIntMap in2out = new TIntIntHashMap(10, 0.5f, -1, -1);
		for (int a : in.getActivities()) {
			String activity = in.getActivityOfIndex(a);
			if (in.getDirectlyFollowsGraph().getEdgesOf(a).iterator().hasNext() || in.getStartActivities().contains(a)
					|| in.getEndActivities().contains(a)) {
				newActivities = ArrayUtils.copyOf(newActivities, newActivities.length + 1);
				newActivities[newActivities.length - 1] = activity;
				in2out.put(a, newActivities.length - 1);
			}
		}
		result.addNodes(newActivities);

		/**
		 * empty traces
		 */
		result.setEmptyTraces(
				in.getNumberOfEmptyTraces() > 0 || !in.getDirectlyFollowsGraph().getEdges().iterator().hasNext());

		/**
		 * edges
		 */
		for (long edgeIndex : in.getDirectlyFollowsGraph().getEdges()) {
			int source = in.getDirectlyFollowsGraph().getEdgeSource(edgeIndex);
			int target = in.getDirectlyFollowsGraph().getEdgeTarget(edgeIndex);
			result.addEdge(in2out.get(source), in2out.get(target));
		}

		/**
		 * start activities
		 */
		for (int activity : in.getStartActivities()) {
			result.getStartNodes().add(in2out.get(activity));
		}

		/**
		 * end activities
		 */
		for (int activity : in.getEndActivities()) {
			result.getEndNodes().add(in2out.get(activity));
		}

		return result;
	}
}
