package org.tejas.jokes;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public class TrainingSetCreater {
	public NeuralDataSet trainingSet = new BasicNeuralDataSet();
	

	public void addToTrainingSet(double [] data, int output){
		trainingSet.add(new BasicMLData(data), new BasicMLData(new double[] {output}));
	}

}
