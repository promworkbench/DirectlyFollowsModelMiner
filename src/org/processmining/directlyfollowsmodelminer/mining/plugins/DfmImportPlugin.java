package org.processmining.directlyfollowsmodelminer.mining.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModelImplQuadratic;
import org.processmining.framework.abstractplugins.ImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(name = "Import a directly follows model", parameterLabels = { "Filename" }, returnLabels = {
		"Directly follows model" }, returnTypes = { DirectlyFollowsModel.class })
@UIImportPlugin(description = "Directly follows model files 2", extensions = { "dfm" })
public class DfmImportPlugin implements ImportPlugin {

	private File file;

	public File getFile() {
		return file;
	}

	public DirectlyFollowsModel importFile(PluginContext context, String filename) throws Exception {
		file = new File(filename);
		return importFromStream(context, new FileInputStream(file));
	}

	public DirectlyFollowsModel importFile(PluginContext context, URI uri) throws Exception {
		return importFromStream(context, uri.toURL().openStream());
	}

	public DirectlyFollowsModel importFile(PluginContext context, URL url) throws Exception {
		file = new File(url.toURI());
		return importFromStream(context, url.openStream());
	}

	@PluginVariant(variantLabel = "Directly follows model import", requiredParameterLabels = { 0 })
	public DirectlyFollowsModel importFile(PluginContext context, File f) throws Exception {
		file = f;
		return importFromStream(context, new FileInputStream(f));
	}
	
	private static final int BUFFER_SIZE = 8192 * 4;
	private static final String CHARSET = Charset.defaultCharset().name();

	public DirectlyFollowsModel importFromStream(PluginContext context, InputStream input) throws Exception {
		DirectlyFollowsModel dfg2 = readFile(input);

		if (dfg2 != null) {
			return dfg2;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, "Invalid directly follows model/minimum self-distance graph file",
						"Invalid file", JOptionPane.ERROR_MESSAGE);
			}
		});
		context.getFutureResult(0).cancel(false);
		return null;
	}

	public static DirectlyFollowsModel readFile(InputStream input) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(input, CHARSET), BUFFER_SIZE);

		//read header
		String header = r.readLine();
		if (!header.equals(DfmExportPlugin.header)) {
			return null;
		}

		DirectlyFollowsModel dfm = new DirectlyFollowsModelImplQuadratic();

		//read empty traces
		{
			String et = r.readLine();
			dfm.setEmptyTraces(Boolean.valueOf(et));
		}

		//read activity names
		{
			int nrOfActivities = Integer.parseInt(r.readLine());
			String[] activities = new String[nrOfActivities];
			for (int i = 0; i < nrOfActivities; i++) {
				activities[i] = r.readLine();
			}
			dfm.addNodes(activities);
		}

		//read start activities
		{
			int nrOfStartActivities = Integer.parseInt(r.readLine());
			for (int i = 0; i < nrOfStartActivities; i++) {
				int activityIndex = Integer.parseInt(r.readLine());
				dfm.addStartNode(activityIndex);
			}
		}

		//read end activities
		{
			int nrOfEndActivities = Integer.parseInt(r.readLine());
			for (int i = 0; i < nrOfEndActivities; i++) {
				int activityIndex = Integer.parseInt(r.readLine());
				dfm.addEndNode(activityIndex);
			}
		}

		//read dfg-edges
		{
			int nrOfEdges = Integer.parseInt(r.readLine());
			for (int i = 0; i < nrOfEdges; i++) {
				String line = r.readLine();
				int eAt = line.indexOf('>');
				int source = Integer.parseInt(line.substring(0, eAt));
				int target = Integer.parseInt(line.substring(eAt + 1));

				dfm.addEdge(source, target);
			}
		}

		return dfm;
	}
}
