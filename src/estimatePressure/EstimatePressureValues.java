/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estimatePressure;

/**
 *
 * @author suchit sharma
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jfree.ui.RefineryUtilities;

import Jama.Matrix;

public class EstimatePressureValues {

	private Double[][] topTopLeft = new Double[8][16];
	private Double[][] topBotLeft = new Double[8][16];
	private Double[][] topBotRight = new Double[8][16];
	private Double[][] topTopRight = new Double[8][16];
	private Double[][] botTopLeft = new Double[8][16];
	private Double[][] botBotLeft = new Double[8][16];
	private Double[][] botBotRight = new Double[8][16];
	private Double[][] botTopRight = new Double[8][16];

	// store average values for a line
	private double avgTopTopLeftLine;
	private double avgTopBotLeftLine;
	private double avgTopBotRightLine;
	private double avgTopTopRightLine;

	private double avgBotTopLeftLine;
	private double avgBotBotLeftLine;
	private double avgBotBotRightLine;
	private double avgBotTopRightLine;
	


	// storage for orientation values
	private Map<String, LinkedList<String>> orientationData = new HashMap<String, LinkedList<String>>();
	// number of lines in file
	private String indexOfReadings;
	private String currentPsrLineIndex;
	// to record the name of session and trail
	private String sessionNo;
	private String trialNo;

	// for reading files
	private BufferedReader br;

	// path to user folder for reading files
	//private String pathPressureData = "/media/hdisc/Documents/study/Maze/TactileData/florian/";
	//private String pathOrientation = "/media/hdisc/Documents/study/Maze/TactileData/orientationData/user/";
	//private String pathExecution = "/media/hdisc/Documents/study/Maze/TactileData/test/";
    private String pathPressureData = "/media/USB DISK/florian2/";
	private String pathOrientation = "/media/USB DISK/orientationData/";
	private String pathExecution = "/media/USB DISK/test/";
	// private String path = "/homes/ssharma/maze/data2/";
	//ratio of frames per second calculated from number of lines in files
	private float ratio = Float.parseFloat("4.98259");

	//Extreme Learning Machine instance
	private ELM elm;
	private int numSample = 0;
	private int inputDim = 3;
	private int outputDim = 8;
	private int hiddenDim = 3;
	private Matrix trainingInput;
	private Matrix expectedValues;
	private ArrayList<ArrayList<Double>> trainingInputList ;
	private ArrayList<ArrayList<Double>> expectedValuesList;
	private boolean isTrainingPhase;
	private boolean useAngles = false;
	
	public static void main(String[] args) {
		
		EstimatePressureValues training = new EstimatePressureValues();
		//initialize elm
		//training.elm = new ELM(training.inputDim, training.hiddenDim, training.outputDim, 0.1, 0.1);
		if(training.useAngles){
			training.elm = new ELM(training.inputDim, training.hiddenDim, training.outputDim, 10.0, 0.1);
		} else{
			training.elm = new ELM(training.inputDim, training.hiddenDim, training.outputDim, 1.0, 0.1);
		}
		
		//initialize list for elm
		training.trainingInputList = new ArrayList<ArrayList<Double>>();
		training.expectedValuesList = new ArrayList<ArrayList<Double>>();
		training.isTrainingPhase = true;
		
		// read data from file
		try {
			training.processFile();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (training.br != null)
					training.br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		//put values to matrices
		training.trainingInput= Matrix.identity(training.inputDim, training.trainingInputList.size());
		training.expectedValues= Matrix.identity(training.outputDim, training.expectedValuesList.size());
		
		System.out.println("Training input list: ");
		for (int i = 0; i < training.inputDim; i++) {
			for (int j = 0; j < training.trainingInputList.size(); j++) {
				training.trainingInput.set(i, j, training.trainingInputList.get(j).get(i));
				System.out.print(training.trainingInput.get(i, j) + "\t");
			}
			System.out.println("");
		}
		System.out.println("Expected value list: ");
		for (int k = 0; k < training.outputDim; k++) {
			for (int n = 0; n < training.expectedValuesList.size(); n++) {
				training.expectedValues.set(k, n, training.expectedValuesList.get(n).get(k));
				System.out.print(training.expectedValues.get(k, n) + "\t");
			}
			System.out.println("");
		}
		
		// training the algorithm
		Matrix outputWeights = training.elm.train(training.trainingInput, training.expectedValues, training.numSample + 1);
		
		/*System.out.println("training output Wout is: ");
		for (int k = 0; k < outputWeights.getRowDimension(); k++) {
			for (int n = 0; n < outputWeights.getColumnDimension(); n++) {
				System.out.print(outputWeights.get(k, n) + "\t");
			}
			System.out.println("");
		}*/
		
		//----------execute the learning algorithm----------//
		EstimatePressureValues m2 = new EstimatePressureValues();
		//initialize elm
		//m2.elm = new ELM(m2.inputDim, m2.hiddenDim, m2.outputDim, 0.1, 0.1);
		if(m2.useAngles){
			m2.elm = new ELM(m2.inputDim, m2.hiddenDim, m2.outputDim, 10.0, 0.1);
		}else {
			m2.elm = new ELM(m2.inputDim, m2.hiddenDim, m2.outputDim, 1.0, 0.1);
		}
		
		//initialize list for elm
		m2.trainingInputList = new ArrayList<ArrayList<Double>>();
		m2.expectedValuesList = new ArrayList<ArrayList<Double>>();
		m2.isTrainingPhase = false;
		
		// read data from file
		try {
			m2.processFile();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (m2.br != null)
					m2.br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		//put values to matrices
		m2.trainingInput= Matrix.identity(m2.inputDim, m2.trainingInputList.size());
		m2.expectedValues=Matrix.identity(m2.outputDim, m2.expectedValuesList.size());
		
		System.out.println("Execution input list: ");
		for (int i = 0; i < m2.inputDim; i++) {
			for (int j = 0; j < m2.trainingInputList.size(); j++) {
				m2.trainingInput.set(i, j, m2.trainingInputList.get(j).get(i));
				System.out.print(m2.trainingInput.get(i, j) + "\t");
			}
			System.out.println("");
		}
		System.out.println("Execution actual output value list: ");
		for (int i = 0; i < m2.outputDim; i++) {
			for (int j = 0; j < m2.expectedValuesList.size(); j++) {
				m2.expectedValues.set(i, j, m2.expectedValuesList.get(j).get(i));
				System.out.print(m2.expectedValues.get(i, j) + "\t");
			}
			System.out.println("");
		}
		
		//execute
		m2.predictPressureValues( m2.trainingInput, outputWeights , m2.expectedValues );
		//m2.predictPressureValues(outputWeights);

	}

	// method reads files
	void processFile() throws IOException {
		File root;
		if( this.isTrainingPhase )
		{
			root = new File(this.pathPressureData);
		}else 
		{
			root = new File(this.pathExecution);
		}
		
		File[] list = root.listFiles();
		if (list == null) {
			throw new RuntimeException(
					"error while reading Tactile data: user directory is empty: "
							+ root.getAbsolutePath());
		}
		for (File f : list) {
			if (f.isDirectory()) {
				// System.out.println("Traversing Dir:" + f.getAbsoluteFile());
				File[] trailList = f.listFiles();
				if (trailList == null) {
					throw new RuntimeException(
							"error while reading Tactile data: user directory is empty: "
									+ f.getAbsoluteFile());
				}
				for (File trailFolder : trailList) {
					if (trailFolder.isDirectory()) {
						System.out.println("Traversing SubDir:"
								+ trailFolder.getAbsoluteFile());
						File[] trailFile = trailFolder.listFiles();
						if (trailFile == null) {
							throw new RuntimeException(
									"error while reading Tactile data: csv file not found in folder:"
											+ trailFolder.getAbsolutePath());
						}
						for (File csvdatafile : trailFile) {
							// read the csv file
							// System.out.println("Reading File:"
							// + csvdatafile.getAbsolutePath());
							
							
							this.br = new BufferedReader(new FileReader(
									csvdatafile.getAbsolutePath()));
							
							// refresh variables
							this.indexOfReadings = null;
							//read number of lines in a file, this method should be optimized
							BufferedReader b = new BufferedReader(new FileReader(
									csvdatafile.getAbsolutePath()) );
							this.getNumberOfLinesInFile(b);
							b.close();
							
							String[] temp1 = f.toString().split("/");
							this.sessionNo = temp1[temp1.length - 1];
							
							String[] temp2 = csvdatafile.toString().split("/");
							String tempfname = temp2[temp2.length - 1];
							
							Pattern p = Pattern.compile(".*_(Trial[0-9]+)\\.csv");
							Matcher m = p.matcher(tempfname);

							if (m.find()) {
							    this.trialNo = m.group(1);
							}else {
								throw new RuntimeException(
										"incorrect file name regex did not match");
							}
							
							//get orientation data
							BufferedReader or = this.openOrientationFile();
							this.readOrientationFile(or);
							this.closeFileReader(or);
							
							// process the data for the csv file
							String sCurrentLine;
							while ((sCurrentLine = this.br.readLine()) != null) {
								//refresh line number
								this.currentPsrLineIndex = null;
								//get pressure values for all eight sensors for this line
								boolean validLine = this.processPressureData(sCurrentLine);
								
								if(validLine){
									//get index of frame in orientation file corresponding to current line number
									float[] oriIndex = this.getOrientationIndex(this.currentPsrLineIndex, this.indexOfReadings );
									String index = Integer.toString( Math.round(oriIndex[1]) );
									//train for every nth frame (around 1000 frame for every second)
									if(  Integer.parseInt( this.currentPsrLineIndex ) % 90 < 1){
										//System.out.println(index);
										LinkedList<String> orientationList = this.orientationData.get(index);
										if(orientationList!=null){
											//System.out.println("orientationList is: " + orientationList);
											//prepare training data
											this.prepareTraingData(orientationList);
										}else{
											System.out.println("orientationList is: " + orientationList);
										}	
									}
								}
							}
							// close the buffered reader
							try {
								if (this.br != null)
									this.br.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			} else {
				throw new RuntimeException(
						"error while reading Tactile data: wrong directory structure");
			}
		}
	}
	
	//returns number of lines in the file , not a good way to do this but fine for now
	void getNumberOfLinesInFile(BufferedReader b) throws IOException {
		String sCurrentLine;
		while ((sCurrentLine = b.readLine()) != null) {
			// remove starting lines
			if (!sCurrentLine.startsWith("# ")) {
				// allocate sensor values to arrays
				String[] line = sCurrentLine.split(",");
				if (line.length == (256 * 4 + 1)) {
					this.indexOfReadings = line[0];
				} else {
					throw new RuntimeException(
							"number of entries from tactile data file not valid, valid is 1025, check empty lines at the end of the file");
				}
			}
		}
	}
	
	boolean processPressureData(String sCurrentLine) {

		// remove starting lines
		if (!sCurrentLine.startsWith("# ")) {
			// allocate sensor values to arrays
			String[] line = sCurrentLine.split(",");
			if (line.length == (256 * 4 + 1)) {
				// refresh average line values
				this.avgTopTopLeftLine = 0;
				this.avgTopBotLeftLine = 0;
				this.avgTopTopRightLine = 0;
				this.avgTopBotRightLine = 0;
				this.avgBotTopLeftLine = 0;
				this.avgBotBotLeftLine = 0;
				this.avgBotTopRightLine = 0;
				this.avgBotBotRightLine = 0;
				
				this.currentPsrLineIndex = line[0];
				
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 16; j++) {
						// skip the first entry (time stamp)
						this.topTopLeft[i][j] = Double.parseDouble(line[i * 16
								+ j + 1]);
						this.topBotLeft[i][j] = Double.parseDouble(line[256 + i
								* 16 + j + 1]);
						this.topBotRight[i][j] = Double.parseDouble(line[256 * 2
								+ i * 16 + j + 1]);
						this.topTopRight[i][j] = Double.parseDouble(line[256 * 3
								+ i * 16 + j + 1]);
						// get average pressure for all four sensors
						this.avgTopTopLeftLine = this.avgTopTopLeftLine
								+ this.topTopLeft[i][j];
						this.avgTopBotLeftLine = this.avgTopBotLeftLine
								+ this.topBotLeft[i][j];
						this.avgTopTopRightLine = this.avgTopTopRightLine
								+ this.topTopRight[i][j];
						this.avgTopBotRightLine = this.avgTopBotRightLine
								+ this.topBotRight[i][j];
					}
				}

				for (int i = 8; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						// skip the first entry (time stamp)
						this.botTopLeft[i - 8][j] = Double.parseDouble(line[i
								* 16 + j + 1]);
						this.botBotLeft[i - 8][j] = Double.parseDouble(line[256
								+ i * 16 + j + 1]);
						this.botBotRight[i - 8][j] = Double.parseDouble(line[256
								* 2 + i * 16 + j + 1]);
						this.botTopRight[i - 8][j] = Double.parseDouble(line[256
								* 3 + i * 16 + j + 1]);
						// get average pressure for all four sensors
						this.avgBotTopLeftLine = this.avgBotTopLeftLine
								+ this.botTopLeft[i - 8][j];
						this.avgBotBotLeftLine = this.avgBotBotLeftLine
								+ this.botBotLeft[i - 8][j];
						this.avgBotTopRightLine = this.avgBotTopRightLine
								+ this.botTopRight[i - 8][j];
						this.avgBotBotRightLine = this.avgBotBotRightLine
								+ this.botBotRight[i - 8][j];
					}
				}

				// for average divide by total number of values
				this.avgTopTopLeftLine = this.avgTopTopLeftLine / 128;
				this.avgTopBotLeftLine = this.avgTopBotLeftLine / 128;
				this.avgTopTopRightLine = this.avgTopTopRightLine / 128;
				this.avgTopBotRightLine = this.avgTopBotRightLine / 128;
				this.avgBotTopLeftLine = this.avgBotTopLeftLine / 128;
				this.avgBotBotLeftLine = this.avgBotBotLeftLine / 128;
				this.avgBotTopRightLine = this.avgBotTopRightLine / 128;
				this.avgBotBotRightLine = this.avgBotBotRightLine / 128;
				// System.out.println(avgTopLeftLine +" "+ avgBotLeftLine + " "
				// +avgTopRightLine + " " + avgBotRightLine);

			} else {
				throw new RuntimeException(
						"number of entries from tactile data file not valid, valid is 1025, check empty lines at the end of the file");
			}
		} else{
			return false;
		}
		
		return true;
	}
	
	float[] getOrientationIndex(String psrIndex,String psrNumOfLines)
	{
		Float psrSize = Float.parseFloat(psrNumOfLines);
		Float psrIndx = Float.parseFloat(psrIndex);
		Float orientationSize = (float) this.orientationData.size();
		
		float orienIndex1 = psrIndx/this.ratio;
		float orienIndex2 = (psrIndx/psrSize) * orientationSize;
//		float rat = psrIndx/psrSize;
//		System.out.println("Ratio is :" + rat);
		float index[] = {orienIndex1,orienIndex2};
		
		return index;
	}
	
	
	// read orientation file
	void readOrientationFile(BufferedReader bufReader) {
		// process the data for the csv file
		String sCurrentLine;
		try {
			while ((sCurrentLine = bufReader.readLine()) != null) {
				if ((!sCurrentLine.startsWith("# "))
						&& (!sCurrentLine.startsWith("T"))
						&& (!sCurrentLine.startsWith("t"))
						&& (!sCurrentLine.equals(""))) {
					String[] lineArray = sCurrentLine.split(",");
					LinkedList<String> tempList = new LinkedList<String>();
					// ignore the first value (time stamp)
					if (lineArray.length != 16) {
						throw new RuntimeException(
								"number of entries from orientation data file not valid, valid is 16, check number of entries or empty lines at the end of the file");
					}
					for (int i = 1; i < lineArray.length; i++) {
						tempList.add(lineArray[i]);
					}
					//add line to the hash map
					this.orientationData.put(lineArray[0],tempList);

				}
			}
		} catch (IOException e) {
			System.out.println("exception while reading orientation file");
			e.printStackTrace();
		}

	}

	// open orientation values from the file corresponding to pressure values
	BufferedReader openOrientationFile() {
		// append session folder name to the path
		String pathSessionFolder = this.pathOrientation + this.sessionNo;
		File root = new File(pathSessionFolder);
		String filename = "*_" + this.trialNo + ".csv";
		FileFilter fileFilter = new WildcardFileFilter(filename);
		File[] list = root.listFiles(fileFilter);
		if (list == null) {
			throw new RuntimeException(
					"error while reading orientation data: session directory does not have required file: "
							+ root.getAbsolutePath() + filename);
		}
		File csvFile = list[0];
		BufferedReader bufReader = null;
		try {
			System.out.println("Reading orientation file :" + csvFile);
			bufReader = new BufferedReader(new FileReader(
					csvFile.getAbsolutePath()));
		} catch (FileNotFoundException e) {
			System.out.println("file with given name:" + list[0].toString()
					+ "not found in the folder:" + root.getAbsolutePath());
			e.printStackTrace();
		}
		return bufReader;

	}

	void closeFileReader(BufferedReader bufReader) {
		// close the buffered reader
		try {
			if (bufReader != null)
				bufReader.close();
		} catch (IOException ex) {
			System.out.println("exception while closing the file reader");
			ex.printStackTrace();
		}
	}
	
	void prepareTraingData(LinkedList<String> orientationList)
	{	
		
		//System.out.println("Oriention values in testing: " + orientationList);
		ArrayList<Double> orientationAngles = new ArrayList<Double>();
		
		for (int i = 0; i < this.inputDim; i++) {
			if( this.useAngles )
			{
				//use below line for angle with z component
				//orientationAngles.add( Double.parseDouble(orientationList.get(i + 3)) );
				double angle = Double.parseDouble(orientationList.get(i + 3) ) ;
				if (i < 2) {
					// scale down the x and y angles as x and y ~= 91.00 or
					// 89.00 and z is =~ -1.00 to 1.00 so that all three angels
					// have same range , this helps in regulating the values of
					// sigmoid function
					angle = angle - 90;
				}
				orientationAngles.add( angle );
			} else
			{
				// use below line for x,y,z components
				//orientationAngles.add(Double.parseDouble(orientationList.get(i)));
				
				// scale down the z components as x and y ~= 0 to 100
				// and z is =~ -10000.00 so that all three components
				// have same range , this helps in regulating the values of
				// sigmoid function
				double values = Double.parseDouble(orientationList.get(i)) ;
				if(i==2){
					values = values + 23500 ;
				}
				values = values/100 ;
				orientationAngles.add( values );
			}
		}
		this.trainingInputList.add(orientationAngles);
		
		ArrayList<Double> pressureValues = new ArrayList<Double>();
		pressureValues.add( this.avgTopBotLeftLine );
		pressureValues.add( this.avgBotBotLeftLine);
		pressureValues.add( this.avgTopTopLeftLine );
		pressureValues.add( this.avgBotTopLeftLine);
		pressureValues.add( this.avgBotBotRightLine );
		pressureValues.add( this.avgTopBotRightLine );
		pressureValues.add( this.avgTopTopRightLine );
		pressureValues.add( this.avgBotTopRightLine );
		
		
		this.expectedValuesList.add(pressureValues);
		
		this.numSample++;
		
	}
	void predictPressureValues(Matrix outputWeights)
	{	
		//sample angles with z components
		double[][] array = { {90.661, 90.688, 0.95414},{90.236, 91.439, 1.4578},{90.315, 90.842, 0.89886} };
		//sample x ,y, z components
		//double[][] array = { {-47.479, -588.31, -23606},{-282.78, 229.11, -23626},{61.283, 1030, -23606} };
		Matrix inputVal = new Matrix(array);
		//ELM lm = new ELM(this.inputDim, this.hiddenDim, this.outputDim, 0.01, 1);
		Matrix result = this.elm.execute(inputVal, outputWeights, array.length);
		//print result
		System.out.println("The predicted pressure values are: ");
		for (int i = 0; i < result.getRowDimension(); i++){
			for (int j = 0; j < result.getColumnDimension(); j++){
				System.out.print(result.get(i, j) + "\t");
			}
			System.out.println();
		}
	}
	
	//predict pressure values 
	void predictPressureValues(Matrix inputVal ,Matrix outputWeights , Matrix expectedOutput)
	{	
		//ELM elmPredict = new ELM(this.inputDim, this.hiddenDim, this.outputDim, 0.01, 1);
		Matrix result = this.elm.execute(inputVal, outputWeights, inputVal.getColumnDimension() );
		//print result
		System.out.println("The predicted pressure values are: ");
		for (int t = 0; t < result.getRowDimension(); t++){
			for (int u = 0; u < result.getColumnDimension(); u++){
				System.out.print( result.get(t, u)  + "\t");
			}
			System.out.println();
		}
		System.out.println("In elm mat values of Wout :");
		for (int p = 0; p < outputWeights.getRowDimension(); p++){
			for (int q = 0; q < outputWeights.getColumnDimension(); q++){
				System.out.print(outputWeights.get(p, q) + "\t");
			}
			System.out.println();
		}
		
		this.calculateError( expectedOutput , result );
	}
	
	//calculate error
	void calculateError(Matrix expectedOutput , Matrix result) {
		Matrix error = new Matrix( expectedOutput.getRowDimension(), expectedOutput.getColumnDimension() );
		double[] avgError = new double[error.getRowDimension()] ; 
		System.out.println("Error values are: ");
		for (int i = 0; i < error.getRowDimension(); i++) {
			for (int j = 0; j < error.getColumnDimension(); j++) {
				if( Math.abs(expectedOutput.get(i, j)) > Math.abs(result.get(i, j)) ){
					error.set(i, j,  ( Math.abs(expectedOutput.get(i, j)) - Math.abs(result.get(i, j)) ) );
					avgError[i] = avgError[i] + error.get(i,j);
				}else{
					error.set(i, j,  ( Math.abs(result.get(i, j)) - Math.abs(expectedOutput.get(i, j)) ) );
					avgError[i] = avgError[i] + error.get(i,j);
				}
				System.out.print(error.get(i, j) + "\t");
			}
			avgError[i] = avgError[i]/error.getColumnDimension();
			System.out.println("");
		}
		for (int k = 0; k < avgError.length; k++) {
			System.out.println("Avg error value: " + k  + " : " + avgError[k]);
		}
		this.drawCharts(error);
	}
	
	//draw charts
	void drawCharts(Matrix error){
		CreateGraph chart1 = new CreateGraph("Error values in predicted pressure" , "Error values", error,0);
		chart1.pack( );
		RefineryUtilities.centerFrameOnScreen( chart1 );
		chart1.setVisible( true );
		
		CreateGraph chart2 = new CreateGraph("Error values in predicted pressure" , "Error values", error,1);
		chart2.pack( );
		RefineryUtilities.centerFrameOnScreen( chart2 );
		chart2.setVisible( true );
		
		CreateGraph chart3 = new CreateGraph("Error values in predicted pressure" , "Error values", error,2);
		chart3.pack( );
		RefineryUtilities.centerFrameOnScreen( chart3 );
		chart3.setVisible( true );
		
		CreateGraph chart4 = new CreateGraph("Error values in predicted pressure" , "Error values", error,3);
		chart4.pack( );
		RefineryUtilities.centerFrameOnScreen( chart4 );
		chart4.setVisible( true );
		
		CreateGraph chart5 = new CreateGraph("Error values in predicted pressure" , "Error values", error,4);
		chart5.pack( );
		RefineryUtilities.centerFrameOnScreen( chart5 );
		chart5.setVisible( true );
		
		CreateGraph chart6 = new CreateGraph("Error values in predicted pressure" , "Error values", error,5);
		chart6.pack( );
		RefineryUtilities.centerFrameOnScreen( chart6 );
		chart6.setVisible( true );
		
		CreateGraph chart7 = new CreateGraph("Error values in predicted pressure" , "Error values", error,6);
		chart7.pack( );
		RefineryUtilities.centerFrameOnScreen( chart7 );
		chart7.setVisible( true );
		
		CreateGraph chart8 = new CreateGraph("Error values in predicted pressure" , "Error values", error,7);
		chart8.pack( );
		RefineryUtilities.centerFrameOnScreen( chart8 );
		chart8.setVisible( true );
		
	}
	
}