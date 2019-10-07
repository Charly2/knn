import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class knn {
	
	public static void main(String[] args){	
		System.out.println("iris");
		knn("iris_train.txt","iris_test.txt",1,2);
		System.out.println();

		
		/*System.out.println("glass");
		knn("glass_train.txt","glass_test.txt",1,0);
		System.out.println();
		
		System.out.println("vowel");
		knn("vowel_train.txt","vowel_test.txt",3,2);
		System.out.println();
		
		System.out.println("vehicle");
		knn("vehicle_train.txt","vehicle_test.txt",3,1);
		System.out.println();
		
		System.out.println("letter");
		knn("letter_train.txt","letter_test.txt",3,0);
		System.out.println();
		
		System.out.println("DNA");
		knn("dna_train.txt","dna_test.txt",5,2);
		System.out.println();*/
	}
	
	public static void knn(String trainingFile, String testFile, int K, int metricType){
		//Tiempo de inicio
		final long startTime = System.currentTimeMillis();
		
		// k > 0
		if(K <= 0){
			System.out.println("K debe ser > 0!");
			return;
		}
		
		// metric [0,2]
		if(metricType > 2 || metricType <0){
			System.out.println("metrica [0,2]");
			return;
		}
		
		//TrainingFile and testFile mismo grupo
		String trainGroup = extractGroupName(trainingFile);
		String testGroup = extractGroupName(testFile);
		
		if(!trainGroup.equals(testGroup)){
			System.out.println("No son del mismo grupo");
			return;
		}
		
		
		try {
			//read trainingSet y testingSet
			TrainRecord[] trainingSet =  FileManager.readTrainFile(trainingFile);
			TestRecord[] testingSet =  FileManager.readTestFile(testFile);
			

			
			//determinar metrica
			Metric metric;
			if(metricType == 0)
				metric = new CosineSimilarity();
			else if(metricType == 1)
				metric = new L1Distance();
			else if (metricType == 2)
				metric = new EuclideanDistance();
			else{
				System.out.println("No se encontro la metrica");
				return;
			}
			
			//Test uno a uno
			int numOfTestingRecord = testingSet.length;
			for(int i = 0; i < numOfTestingRecord; i ++){
				TrainRecord[] neighbors = findKNearestNeighbors(trainingSet, testingSet[i], K, metric);
				Classify classLabel = classify(neighbors);
				testingSet[i].predictedLabel = classLabel.cla; 
				testingSet[i].dist = classLabel.dis;
		}
			
			//verifica si es correcto
			int correctPrediction = 0;
			for(int j = 0; j < numOfTestingRecord; j ++){
				if(testingSet[j].predictedLabel == testingSet[j].classLabel)
					correctPrediction ++;
			}
			
			//file output
			String predictPath = FileManager.outputFile(testingSet, trainingFile);
			System.out.println("Se guardo en "+predictPath);
			System.out.println("La exactitud  "+((double)correctPrediction / numOfTestingRecord)*100+"%");
			
			//print result
			final long endTime = System.currentTimeMillis();
			System.out.println("Tiempo total: "+(endTime - startTime) / (double)1000 +" s.");
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// busca los k nn
	static TrainRecord[] findKNearestNeighbors(TrainRecord[] trainingSet, TestRecord testRecord,int K, Metric metric){
		int NumOfTrainingSet = trainingSet.length;
		assert K <= NumOfTrainingSet : "K is lager than the length of trainingSet!";
		
		//Update KNN: take the case when testRecord has multiple neighbors with the same distance into consideration
		//Solution: Update the size of container holding the neighbors
		TrainRecord[] neighbors = new TrainRecord[K];
		
		//initialization, put the first K trainRecords into the above arrayList
		int index;
		for(index = 0; index < K; index++){
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);
			neighbors[index] = trainingSet[index];
		}
		
		//go through the remaining records in the trainingSet to find K nearest neighbors
		for(index = K; index < NumOfTrainingSet; index ++){
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);
			
			//get the index of the neighbor with the largest distance to testRecord
			int maxIndex = 0;
			for(int i = 1; i < K; i ++){
				if(neighbors[i].distance > neighbors[maxIndex].distance)
					maxIndex = i;
			}
			
			//add the current trainingSet[index] into neighbors if applicable
			if(neighbors[maxIndex].distance > trainingSet[index].distance)
				neighbors[maxIndex] = trainingSet[index];
		}
		
		return neighbors;
	}
	
	// Get the class label by using neighbors
	static Classify classify(TrainRecord[] neighbors){
		//construct a HashMap to store <classLabel, weight>
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		int num = neighbors.length;
		
		for(int index = 0;index < num; index ++){
			TrainRecord temp = neighbors[index];
			int key = temp.classLabel;
		
			//if this classLabel does not exist in the HashMap, put <key, 1/(temp.distance)> into the HashMap
			if(!map.containsKey(key))
				map.put(key, 1 / temp.distance);
			
			//else, update the HashMap by adding the weight associating with that key
			else{
				double value = map.get(key);
				value += 1 / temp.distance;
				map.put(key, value);
			}
		}	
		
		//Find the most likely label
		double maxSimilarity = 0;
		int returnLabel = -1;
		Set<Integer> labelSet = map.keySet();
		Iterator<Integer> it = labelSet.iterator();
		
		//go through the HashMap by using keys 
		//and find the key with the highest weights 
		while(it.hasNext()){
			int label = it.next();
			double value = map.get(label);
			
			if(value > maxSimilarity){
				maxSimilarity = value;
				returnLabel = label;
				
				System.out.print(value + "----"+label);
				System.out.println();
			}
		}
		
		return new Classify(returnLabel,maxSimilarity);
	}
	
	static String extractGroupName(String filePath){
		StringBuilder groupName = new StringBuilder();
		for(int i = 15; i < filePath.length(); i ++){
			if(filePath.charAt(i) != '_')
				groupName.append(filePath.charAt(i));
			else
				break;
		}
		
		return groupName.toString();
	}
}
