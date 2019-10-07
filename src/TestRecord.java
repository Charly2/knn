

public class TestRecord extends Record{
	int predictedLabel;
	double dist = 0;
	
	TestRecord(double[] attributes, int classLabel) {
		super(attributes, classLabel);
	}
}
