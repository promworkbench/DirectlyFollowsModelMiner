package org.processmining.directlyfollowsmodelminer.mining.variants;

import org.processmining.directlyfollowsmodelminer.mining.DFMMiningParametersAbstract;
import org.processmining.plugins.InductiveMiner.mining.logs.LifeCycleClassifier;

public class DFMMiningParametersDefault extends DFMMiningParametersAbstract {
	public DFMMiningParametersDefault() {
		setNoiseThreshold(0.8);
		setLifeCycleClassifier(new LifeCycleClassifier());
	}
}
