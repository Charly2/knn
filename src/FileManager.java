//FileManager
// * ReadFile: read training files and test files
// * OutputFile: output predicted labels into a file

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class FileManager {
	
	//read training files
	public static TrainRecord[] readTrainFile(String fileName) throws IOException{
		File file = new File("/home/charly/git/KNN/classification/"+fileName);
		
		Scanner scanner = new Scanner(file);
		
		//System.out.print(scanner);
        //

		//read file
		int NumOfSamples = scanner.nextInt();
		int NumOfAttributes = scanner.nextInt();
		int LabelOrNot = scanner.nextInt();
		scanner.nextLine();
		
		
		
		
		
		assert LabelOrNot == 1 : "No class ";// ensure that C is present in this file
		
		
		//transform data from file into TrainRecord objects
		TrainRecord[] records = new TrainRecord[NumOfSamples];
		int index = 0;
		while(scanner.hasNext()){
			double[] attributes = new double[NumOfAttributes];
			int classLabel = -1;
			
			int id= (int) scanner.nextDouble();
			
			//Read a whole line for a TrainRecord
			for(int i = 0; i < NumOfAttributes; i ++){
				double aux = scanner.nextDouble();
				System.out.print(aux);System.out.println();
				attributes[i] = aux;
			}
			
			//Read classLabel
			classLabel = (int) scanner.nextDouble();
			assert classLabel != -1 : "Reading class label is wrong!";
			/*System.out.println();
			System.out.println();
			System.out.print(scanner.nextDouble());
			System.out.println();
			System.exit(1);*/
			
			records[index] = new TrainRecord(attributes, classLabel);
			records[index].id = id;
			index ++;
		}
		
		return records;
	}
	
	
	public static TestRecord[] readTestFile(String fileName) throws IOException{
		File file = new File("/home/charly/git/KNN/classification/"+fileName);
		
		Scanner scanner = new Scanner(file);

		//read file
		int NumOfSamples = scanner.nextInt();
		int NumOfAttributes = scanner.nextInt();
		int LabelOrNot = scanner.nextInt();
		scanner.nextLine();
		
		
		
		assert LabelOrNot == 1 : "No classLabel";
		
		TestRecord[] records = new TestRecord[NumOfSamples];
		int index = 0;
		while(scanner.hasNext()){
			if(index == NumOfSamples) {break; }
			double[] attributes = new double[NumOfAttributes];
			int classLabel = -1;
			int id= (int) scanner.nextDouble();
			
			//read a whole line for a TestRecord
			for(int i = 0; i < NumOfAttributes; i ++){
				double aux = scanner.nextDouble();
				System.out.print(aux);System.out.println();
				attributes[i] = aux;
			}
			
			//read the true lable of a TestRecord which is later used for validation
			classLabel = (int) scanner.nextDouble();
			assert classLabel != -1 : "Reading class label is wrong!";
			
			records[index] = new TestRecord(attributes, classLabel);
			records[index].id = id;
			index ++;
		}
		
		return records;
	}
	
	public static String outputFile(TestRecord[] testRecords, String trainFilePath) throws IOException{
		//construct the predication file name
		StringBuilder predictName = new StringBuilder();
		for(int i = 15; i < trainFilePath.length(); i ++){
			if(trainFilePath.charAt(i) != '_')
				predictName.append(trainFilePath.charAt(i));
			else
				break;
		}
		String predictPath = "/home/charly/git/KNN/classification/"+predictName.toString()+"_prediction.txt";
		
		//ouput the prediction labels
		File file = new File(predictPath);
		if(!file.exists())
			file.createNewFile();
		
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(int i =0; i < testRecords.length; i ++){
			TestRecord tr = testRecords[i];
			bw.write(Integer.toString(tr.predictedLabel));
			bw.write("---");
			bw.write(Double.toString(tr.dist));
			bw.write("---");
			bw.write(Double.toString(tr.id));
			
			bw.newLine();
		}
		
		bw.close();
		fw.close();
		
		return predictPath;
	}
}
