package org.processmining.directlyfollowsmodelminer.model;

import java.util.ArrayList;
import java.util.List;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class DirectlyFollowsModel2AcceptingPetriNet {
	public static AcceptingPetriNet convert(DirectlyFollowsModel dfg) {
		TIntObjectMap<List<Transition>> node2transition = new TIntObjectHashMap<>(10, 0.5f, -1);
		return convert(dfg, node2transition);
	}

	public static AcceptingPetriNet convert(DirectlyFollowsModel dfg, TIntObjectMap<List<Transition>> node2transition) {
		Petrinet petriNet = new PetrinetImpl("converted from Dfm");
		Place source = petriNet.addPlace("net source");
		Place sink = petriNet.addPlace("net sink");
		Marking initialMarking = new Marking();
		initialMarking.add(source);
		Marking finalMarking = new Marking();
		finalMarking.add(sink);

		/**
		 * empty traces
		 */
		if (dfg.isEmptyTraces()) {
			Transition epsilon = petriNet.addTransition("epsilon");
			epsilon.setInvisible(true);
			petriNet.addArc(source, epsilon);
			petriNet.addArc(epsilon, sink);
		}

		/**
		 * Activities (states)
		 */
		TIntObjectMap<Place> activity2place = new TIntObjectHashMap<>();
		for (int activity : dfg.getNodeIndices()) {
			Place place = petriNet.addPlace(dfg.getNodeOfIndex(activity));
			activity2place.put(activity, place);
		}

		/**
		 * Transitions
		 */
		for (long edge : dfg.getEdges()) {
			int sourceActivity = dfg.getEdgeSource(edge);
			int targetActivity = dfg.getEdgeTarget(edge);
			Place sourcePlace = activity2place.get(sourceActivity);
			Place targetPlace = activity2place.get(targetActivity);

			Transition transition = petriNet.addTransition(dfg.getNodeOfIndex(targetActivity));

			node2transition.putIfAbsent(targetActivity, new ArrayList<Transition>());
			node2transition.get(targetActivity).add(transition);

			petriNet.addArc(sourcePlace, transition);
			petriNet.addArc(transition, targetPlace);
		}

		/**
		 * Starts
		 */
		for (TIntIterator it = dfg.getStartNodes().iterator(); it.hasNext();) {
			int activity = it.next();
			Transition transition = petriNet.addTransition(dfg.getNodeOfIndex(activity));

			node2transition.putIfAbsent(activity, new ArrayList<Transition>());
			node2transition.get(activity).add(transition);

			petriNet.addArc(source, transition);
			petriNet.addArc(transition, activity2place.get(activity));
		}

		/**
		 * Ends
		 */
		for (TIntIterator it = dfg.getEndNodes().iterator(); it.hasNext();) {
			int activity = it.next();
			Transition transition = petriNet.addTransition(dfg.getNodeOfIndex(activity) + " -> end");
			transition.setInvisible(true);

			petriNet.addArc(activity2place.get(activity), transition);
			petriNet.addArc(transition, sink);
		}

		return AcceptingPetriNetFactory.createAcceptingPetriNet(petriNet, initialMarking, finalMarking);
	}
}
