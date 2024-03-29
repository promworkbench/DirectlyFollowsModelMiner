package org.processmining.directlyfollowsmodelminer.mining;

import org.deckfour.xes.model.XLog;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier.Transition;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.logs.IMEventIterator;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImpl;
import org.processmining.plugins.inductiveminer2.logs.IMTrace;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.Log2DfgMsd;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

public class DFMMiner {
	public static DirectlyFollowsModel mine(XLog xLog, DFMMiningParameters parameters, Canceller canceller) {
		IMLog log = new IMLogImpl(xLog, parameters.getClassifier(), parameters.getLifeCycleClassifier());
		return mine(log, parameters, canceller);
	}

	public static DirectlyFollowsModel mine(IMLog log, DFMMiningParameters parameters, Canceller canceller) {
		int initialSize = log.size();

		//copy the log
		log = log.clone();

		//filter out non-completion events
		for (IMTrace t : log) {
			for (IMEventIterator it = t.iterator(); it.hasNext();) {
				it.nextFast();
				if (it.getLifeCycleTransition() != Transition.complete) {
					it.remove();
				}
			}
		}
		
		DfgMsd dfg = Log2DfgMsd.convert(log);

		while (true) {
			//gather the edges to be filtered
			TLongSet edgesToFilter = getEdgesToFilter(dfg);
			if (edgesToFilter.isEmpty()) {
				return DfgMsd2Dfm.convert(dfg);
			}

			//filter the log
			filterLog(log, edgesToFilter);

			//create a new dfg
			if (log.size() < initialSize * parameters.getNoiseThreshold()) {

				return DfgMsd2Dfm.convert(dfg);
			}
			dfg = Log2DfgMsd.convert(log);
		}
	}

	/**
	 * 
	 * @param dfg
	 * @return A list of edges with the minimum occurrence.
	 */
	public static TLongSet getEdgesToFilter(IntDfg dfg) {
		long min = Long.MAX_VALUE;
		TLongSet result = new TLongHashSet(10, 0.5f, -1);

		//start edges
		for (int startActivity : dfg.getStartActivities()) {
			long w = dfg.getStartActivities().getCardinalityOf(startActivity);
			if (w == min) {
				result.add(getEdge(-1, startActivity));
			} else if (w < min) {
				min = w;
				result.clear();
				result.add(getEdge(-1, startActivity));
			}
		}

		//normal edges
		for (long edge : dfg.getDirectlyFollowsGraph().getEdges()) {
			long w = dfg.getDirectlyFollowsGraph().getEdgeWeight(edge);
			if (w == min) {
				result.add(getEdge(dfg.getDirectlyFollowsGraph().getEdgeSource(edge),
						dfg.getDirectlyFollowsGraph().getEdgeTarget(edge)));
			} else if (w < min) {
				min = w;
				result.clear();
				result.add(getEdge(dfg.getDirectlyFollowsGraph().getEdgeSource(edge),
						dfg.getDirectlyFollowsGraph().getEdgeTarget(edge)));
			}
		}

		//end edges
		for (int endActivities : dfg.getEndActivities()) {
			long w = dfg.getEndActivities().getCardinalityOf(endActivities);
			if (w == min) {
				result.add(getEdge(endActivities, -1));
			} else if (w < min) {
				min = w;
				result.clear();
				result.add(getEdge(endActivities, -1));
			}
		}

		return result;
	}

	public static void filterLog(IMLog log, TLongSet edgesToFilter) {
		for (IMTraceIterator it = log.iterator(); it.hasNext();) {
			it.nextFast();
			filterTrace(edgesToFilter, it);
		}
	}

	private static void filterTrace(TLongSet edgesToFilter, IMTraceIterator it) {
		int lastActivity = -1;
		while (it.itEventHasNext()) {
			it.itEventNext();

			int activity = it.itEventGetActivityIndex();

			if (edgesToFilter.contains(getEdge(lastActivity, activity))) {
				it.remove();
				return;
			}

			lastActivity = activity;
		}

		if (edgesToFilter.contains(getEdge(lastActivity, -1))) {
			it.remove();
			return;
		}
	}

	private static long getEdge(int source, int target) {
		return (((long) source) << 32) | (target & 0xffffffffL);
	}

	//	private static long getSource(long edge) {
	//		return (int) (edge >> 32);
	//	}
	//
	//	private static long getTarget(long edge) {
	//		return (int) edge;
	//	}
}
