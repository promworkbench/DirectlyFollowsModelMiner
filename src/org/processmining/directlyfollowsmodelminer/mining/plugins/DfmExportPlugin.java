package org.processmining.directlyfollowsmodelminer.mining.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import gnu.trove.iterator.TIntIterator;

@Plugin(name = "Directly follows model export", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Directly follows model", "File" }, userAccessible = true)
@UIExportPlugin(description = "Directly follows model files", extension = "dfm")
public class DfmExportPlugin {

	public static final String header = "directly follows model";

	@PluginVariant(variantLabel = "Dfm export", requiredParameterLabels = { 0, 1 })
	public void exportDefault(UIPluginContext context, DirectlyFollowsModel dfm, File file) throws IOException {
		export(dfm, file);
	}

	public void exportDefault(PluginContext context, DirectlyFollowsModel dfm, File file) throws IOException {
		export(dfm, file);
	}

	public static void export(DirectlyFollowsModel dfm, File file) throws IOException {
		FileOutputStream result = new FileOutputStream(file);
		export(dfm, result);
		result.close();
	}

	/**
	 * Does not close the stream.
	 * 
	 * @param dfm
	 * @param stream
	 * @throws IOException
	 */
	public static void export(DirectlyFollowsModel dfm, OutputStream stream) throws IOException {
		BufferedWriter result = new BufferedWriter(new OutputStreamWriter(stream));
		result.append(header + "\n");
		result.append(dfm.isEmptyTraces() + "\n");
		result.append(dfm.getNumberOfNodes() + "\n");
		for (String e : dfm.getAllNodeNames()) {
			result.append(e + "\n");
		}

		result.append(dfm.getNumberOfStartNodes() + "\n");
		for (TIntIterator it = dfm.getStartNodes().iterator(); it.hasNext();) {
			int activityIndex = it.next();
			result.append(activityIndex + "\n");
		}

		result.append(dfm.getNumberOfEndNodes() + "\n");
		for (TIntIterator it = dfm.getEndNodes().iterator(); it.hasNext();) {
			int activityIndex = it.next();
			result.append(activityIndex + "\n");
		}

		//dfg-edges
		{
			long edges = 0;
			for (Iterator<Long> iterator = dfm.getEdges().iterator(); iterator.hasNext();) {
				iterator.next();
				edges++;
			}
			result.append(edges + "\n");
			for (long edge : dfm.getEdges()) {
				int source = dfm.getEdgeSource(edge);
				int target = dfm.getEdgeTarget(edge);
				result.append(source + ">" + target + "\n");
			}
		}

		result.flush();
	}
}
