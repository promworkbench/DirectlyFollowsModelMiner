package org.processmining.directlyfollowsmodelminer.mining.plugins;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.directlyfollowsmodelminer.visualisation.DirectlyFollowsModel2Dot;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import gnu.trove.map.TIntObjectMap;

public class DirectlyFollowsModelVisualisationPlugin {
	@Plugin(name = "Directly follows model visualisation", returnLabels = { "Dot visualization" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Directly follows model" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise process tree", requiredParameterLabels = { 0 })
	public DotPanel fancy(PluginContext context, DirectlyFollowsModel dfm) throws UnknownTreeNodeException {
		return fancy(dfm);
	}

	public static DotPanel fancy(DirectlyFollowsModel dfgMsd) {
		Dot dot;
		if (dfgMsd.getNumberOfNodes() > 50) {
			dot = new Dot();
			dot.addNode("Graphs with more than 50 nodes are not visualised to prevent hanging...");
		} else {
			Pair<Dot, TIntObjectMap<DotNode>> p = DirectlyFollowsModel2Dot.visualise(dfgMsd);
			dot = p.getA();
		}
		return new DotPanel(dot);
	}
}
