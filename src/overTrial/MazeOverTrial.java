/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package overTrial;

/**
 *
 * @author ssharma
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.jfree.ui.RefineryUtilities;

public class MazeOverTrial {

	private Float[][] topLeft = new Float[16][16];
	private Float[][] botLeft = new Float[16][16];
	private Float[][] botRight = new Float[16][16];
	private Float[][] topRight = new Float[16][16];

	// store average values for a line
	private float avgTopLeftLine;
	private float avgBotLeftLine;
	private float avgBotRightLine;
	private float avgTopRightLine;

	// store average values for a line
	private float avgTopLeft;
	private float avgBotLeft;
	private float avgBotRight;
	private float avgTopRight;

	//data structure to accumulate average pressure values over the sessions
	private LinkedList<LinkedList<String>> pressureData = new LinkedList<LinkedList<String>>() ;
	
	// number of lines in file
	private int numberOfReadings;

        //to record the name of session and trail
        private String sessionNo;
        private String trailNo;

	// for reading files
	private BufferedReader br;

	// path to user folder for reading files
	private String path = "/media/hdisc/Documents/study/Maze/TactileData/test/";
	//private String path = "/homes/ssharma/maze/data2/";

	public static void main(String[] args) {
		MazeOverTrial m = new MazeOverTrial();

		// read data from file
		try {
			m.processFile();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (m.br != null)
					m.br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	  //send data to draw charts
      CreateGraphOverTrial chart1 = new CreateGraphOverTrial(
      "Pressure values over the session TopLeft" ,
      "Pressure values over the session", m.pressureData,3, m.sessionNo+m.trailNo );
      chart1.pack( );
      RefineryUtilities.centerFrameOnScreen( chart1 );
      chart1.setVisible( true );
      
		CreateGraphOverTrial chart2 = new CreateGraphOverTrial(
				"Pressure values over the session BotLeft",
				"Pressure values over the session", m.pressureData, 4, m.sessionNo+m.trailNo );
		chart2.pack();
		RefineryUtilities.centerFrameOnScreen(chart2);
		chart2.setVisible(true);

		CreateGraphOverTrial chart3 = new CreateGraphOverTrial(
				"Pressure values over the session BotRight",
				"Pressure values over the session", m.pressureData, 5, m.sessionNo+m.trailNo );
		chart3.pack();
		RefineryUtilities.centerFrameOnScreen(chart3);
		chart3.setVisible(true);

		CreateGraphOverTrial chart4 = new CreateGraphOverTrial(
				"Pressure values over the session TopRight",
				"Pressure values over the session", m.pressureData, 6, m.sessionNo+m.trailNo );
		chart4.pack();
		RefineryUtilities.centerFrameOnScreen(chart4);
		chart4.setVisible(true);
    	      
	}

	private void accumulateData() {
		LinkedList<String> data = new LinkedList<String>();
		data.add(this.sessionNo);
		data.add(this.trailNo);
		data.add("" + this.numberOfReadings);
		data.add("" + this.avgTopLeftLine);
		data.add("" + this.avgBotLeftLine);
		data.add("" + this.avgBotRightLine);
		data.add("" + this.avgTopRightLine);
		this.pressureData.add(data);
		
		System.out.println(this.sessionNo + " " + this.trailNo + " " +"average over total " + this.numberOfReadings
				+ " readings: " + this.avgTopLeft + " " + this.avgBotLeft + " "
				+ this.avgBotRight + " " + this.avgTopRight);
	}

	// method reads files
	void processFile() throws IOException {
		File root = new File(this.path);
		File[] list = root.listFiles();
		if (list == null) {
			throw new RuntimeException(
					"error while reading Tactile data: user directory is empty: "
							+ root.getAbsolutePath());
		}
		for (File f : list) {
			if (f.isDirectory()) {
				//System.out.println("Traversing Dir:" + f.getAbsoluteFile());
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
							//System.out.println("Reading File:"
							//		+ csvdatafile.getAbsolutePath());
							this.br = new BufferedReader(new FileReader(
									csvdatafile.getAbsolutePath()));
							
							//refresh variables
							this.numberOfReadings = 0;
							this.avgTopLeft = 0;
							this.avgBotLeft = 0;
							this.avgTopRight = 0;
							this.avgBotRight = 0;
							
                                                        // write data to file
							String[] temp1 = f.toString().split("/");
							this.sessionNo = temp1[temp1.length-1];
							String[] temp2 = trailFolder.toString().split("/");
							this.trailNo = temp2[temp2.length-1];
                                                        
							//process the data for the csv file
							String sCurrentLine;
							while ((sCurrentLine = this.br.readLine()) != null) {
								this.processData(sCurrentLine);
							}
							// divide sum by number of lines to get average
							this.avgTopLeft = this.avgTopLeft / this.numberOfReadings;
							this.avgBotLeft = this.avgBotLeft / this.numberOfReadings;
							this.avgTopRight = this.avgTopRight / this.numberOfReadings;
							this.avgBotRight = this.avgBotRight / this.numberOfReadings;
							
							//close the buffered reader
							try {
								if (this.br != null)
									this.br.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}


							
//							this.accumulateData(session,trail);

							
						}
					}
				}
			} else {
				throw new RuntimeException(
						"error while reading Tactile data: wrong directory structure");
			}
		}
	}

	void processData(String sCurrentLine) {

		// remove starting lines
		if (!sCurrentLine.startsWith("# ")) {
			// allocate sensor values to arrays
			String[] line = sCurrentLine.split(",");
			if (line.length == (256 * 4 + 1)) {
				this.numberOfReadings++;
				// refresh average line values
				this.avgTopLeftLine = 0;
				this.avgBotLeftLine = 0;
				this.avgTopRightLine = 0;
				this.avgBotRightLine = 0;
				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						// skip the first entry (time stamp)
						this.topLeft[i][j] = Float.parseFloat(line[i * 16 + j
								+ 1]);
						this.botLeft[i][j] = Float.parseFloat(line[256 + i * 16
								+ j + 1]);
						this.botRight[i][j] = Float.parseFloat(line[256 * 2 + i
								* 16 + j + 1]);
						this.topRight[i][j] = Float.parseFloat(line[256 * 3 + i
								* 16 + j + 1]);
						// get average pressure for all four sensors
						this.avgTopLeftLine = this.avgTopLeftLine
								+ this.topLeft[i][j];
						this.avgBotLeftLine = this.avgBotLeftLine
								+ this.botLeft[i][j];
						this.avgTopRightLine = this.avgTopRightLine
								+ this.topRight[i][j];
						this.avgBotRightLine = this.avgBotRightLine
								+ this.botRight[i][j];
					}
				}

				// for average divide by total number of values
				this.avgTopLeftLine = this.avgTopLeftLine / 256;
				this.avgBotLeftLine = this.avgBotLeftLine / 256;
				this.avgTopRightLine = this.avgTopRightLine / 256;
				this.avgBotRightLine = this.avgBotRightLine / 256;
				// System.out.println(avgTopLeftLine +" "+ avgBotLeftLine + " "
				// +avgTopRightLine + " " + avgBotRightLine);
                                
                                
                                this.accumulateData();
                                
                                
				// add to total average
				this.avgTopLeft += this.avgTopLeftLine;
				this.avgBotLeft += this.avgBotLeftLine;
				this.avgTopRight += this.avgTopRightLine;
				this.avgBotRight += this.avgBotRightLine;
				// print values
				/*
				 * for (int i = 0; i < 16; i++) { for (int j = 0; j < 16; j++) {
				 * System.out.print(topRight[i][j] + " "); }
				 * System.out.println(); }
				 */
				// thread.sleep if cpu load is too much
				/*
				 * try { Thread.sleep(10); } catch (InterruptedException e) {
				 * System.out.println("InterruptedException in thread.sleep");
				 * e.printStackTrace(); }
				 */
			} else {
				throw new RuntimeException(
						"number of entries from tactile data file not valid, valid is 1025, check empty lines at the end of the file");
			}
		}
	}

}