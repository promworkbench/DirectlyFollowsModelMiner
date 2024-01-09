package org.processmining.directlyfollowsmodelminer.mining;

import java.util.BitSet;

import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;

import gnu.trove.iterator.TIntIterator;

public class CheckSoundness {
	/**
	 * Check the dfm for soundness.
	 * 
	 * @param dfm
	 * @return A string describing an issue, or null if there are no issues.
	 */
	public static String findIssues(DirectlyFollowsModel dfm) {
		BitSet forward = forwardReachabilitySearch(dfm);
		int notForward = forward.nextClearBit(0);
		if (notForward < dfm.getNumberOfNodes()) {
			return "Activity '" + dfm.getNodeOfIndex(notForward) + "' cannot be reached from the start.";
		}

		BitSet backward = backwardReachabilitySearch(dfm);
		int notBackward = backward.nextClearBit(0);
		if (notBackward < dfm.getNumberOfNodes()) {
			return "From activity '" + dfm.getNodeOfIndex(notBackward) + "' the end cannot be reached.";
		}

		if (!dfm.getEdges().iterator().hasNext() && !dfm.isEmptyTraces()) {
			return "There is no way to reach the end.";
		}

		return null;
	}

	public static BitSet forwardReachabilitySearch(DirectlyFollowsModel dfm) {
		BitSet reached = new BitSet();
		for (TIntIterator it = dfm.getStartNodes().iterator(); it.hasNext();) {
			reached.set(it.next());
		}
		boolean another = true;
		while (another) {
			another = false;
			for (long edgeIndex : dfm.getEdges()) {
				int source = dfm.getEdgeSource(edgeIndex);
				int target = dfm.getEdgeTarget(edgeIndex);
				if (reached.get(source) && !reached.get(target)) {
					reached.set(target);
					another = true;
				}
			}
		}
		return reached;
	}

	public static BitSet backwardReachabilitySearch(DirectlyFollowsModel dfm) {
		BitSet reached = new BitSet();
		for (TIntIterator it = dfm.getEndNodes().iterator(); it.hasNext();) {
			reached.set(it.next());
		}
		boolean another = true;
		while (another) {
			another = false;
			for (long edgeIndex : dfm.getEdges()) {
				int source = dfm.getEdgeSource(edgeIndex);
				int target = dfm.getEdgeTarget(edgeIndex);
				if (reached.get(target) && !reached.get(source)) {
					reached.set(source);
					another = true;
				}
			}
		}
		return reached;
	}
}
