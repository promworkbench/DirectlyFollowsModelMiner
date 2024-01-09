package org.processmining.directlyfollowsmodelminer.mining.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.directlyfollowsmodelminer.mining.DFMMiner;
import org.processmining.directlyfollowsmodelminer.mining.DFMMiningParameters;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel2AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class DirectlyFollowsModelMinerPlugin {
	@Plugin(name = "Mine directly follows model", level = PluginLevel.Regular, returnLabels = {
			"Directly follows model" }, returnTypes = {
					DirectlyFollowsModel.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public DirectlyFollowsModel mineGuiProcessTree(final UIPluginContext context, XLog xLog) {
		DirectlyFollowsModelMinerDialog dialog = new DirectlyFollowsModelMinerDialog(xLog);
		InteractionResult result = context.showWizard("Mine using Directly Follows Model Miner", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		DFMMiningParameters parameters = dialog.getMiningParameters();

		context.log("Mining...");

		return DFMMiner.mine(xLog, parameters, new Canceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		});
	}

	@Plugin(name = "Mine directly follows model with parameters", level = PluginLevel.Regular, returnLabels = {
			"Directly follows model" }, returnTypes = {
					DirectlyFollowsModel.class }, parameterLabels = { "Log", "Parameters" }, userAccessible = false)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public DirectlyFollowsModel mineProcessTree(final PluginContext context, XLog xLog,
			DFMMiningParameters parameters) {
		return DFMMiner.mine(xLog, parameters, new Canceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		});
	}

	@Plugin(name = "Mine Petri net using directly follows model miner with parameters", level = PluginLevel.Regular, returnLabels = {
			"Petri net", "Marking" }, returnTypes = { PetrinetImpl.class,
					Marking.class }, parameterLabels = { "Log", "Parameters" }, userAccessible = false)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public Object[] minePN(final PluginContext context, XLog xLog, DFMMiningParameters parameters) {
		DirectlyFollowsModel model = DFMMiner.mine(xLog, parameters, new Canceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		});

		AcceptingPetriNet pn = DirectlyFollowsModel2AcceptingPetriNet.convert(model);

		Object[] result = new Object[2];
		result[0] = pn.getNet();
		result[1] = pn.getInitialMarking();
		context.getConnectionManager().addConnection(new InitialMarkingConnection(pn.getNet(), pn.getInitialMarking()));
		return result;
	}

	public static DirectlyFollowsModel mine(XLog log, DFMMiningParameters parameters, Canceller canceller) {
		return DFMMiner.mine(log, parameters, canceller);
	}

}
