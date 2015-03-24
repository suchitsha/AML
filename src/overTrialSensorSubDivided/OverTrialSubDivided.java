/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package overTrialSensorSubDivided;

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

public class OverTrialSubDivided {

	private Float[][] topTopLeft = new Float[8][16];
	private Float[][] topBotLeft = new Float[8][16];
	private Float[][] topBotRight = new Float[8][16];
	private Float[][] topTopRight = new Float[8][16];
    private Float[][] botTopLeft = new Float[8][16];
	private Float[][] botBotLeft = new Float[8][16];
	private Float[][] botBotRight = new Float[8][16];
	private Float[][] botTopRight = new Float[8][16];
        
	// store average values for a line
	private float avgTopTopLeftLine;
	private float avgTopBotLeftLine;
	private float avgTopBotRightLine;
	private float avgTopTopRightLine;

	private float avgBotTopLeftLine;
	private float avgBotBotLeftLine;
	private float avgBotBotRightLine;
	private float avgBotTopRightLine;
        
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
		OverTrialSubDivided m = new OverTrialSubDivided();

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
            CreateGraphSubDivided chart1 = new CreateGraphSubDivided(
                            "Pressure values over the trial TopTopLeft" ,
                            "Pressure values over the trial", m.pressureData,3, m.sessionNo+m.trailNo );
            chart1.pack( );
            RefineryUtilities.centerFrameOnScreen( chart1 );
            chart1.setVisible( true );

            CreateGraphSubDivided chart2 = new CreateGraphSubDivided(
                            "Pressure values over the trial TopBotLeft",
                            "Pressure values over the trial", m.pressureData, 4, m.sessionNo+m.trailNo );
            chart2.pack();
            RefineryUtilities.centerFrameOnScreen(chart2);
            chart2.setVisible(true);

            CreateGraphSubDivided chart3 = new CreateGraphSubDivided(
                            "Pressure values over the trial TopBotRight",
                            "Pressure values over the trial", m.pressureData, 5, m.sessionNo+m.trailNo );
            chart3.pack();
            RefineryUtilities.centerFrameOnScreen(chart3);
            chart3.setVisible(true);

            CreateGraphSubDivided chart4 = new CreateGraphSubDivided(
                            "Pressure values over the trial TopTopRight",
                            "Pressure values over the trial", m.pressureData, 6, m.sessionNo+m.trailNo );
            chart4.pack();
            RefineryUtilities.centerFrameOnScreen(chart4);
            chart4.setVisible(true);
    	    
            CreateGraphSubDivided chart5 = new CreateGraphSubDivided(
                            "Pressure values over the trial BotTopLeft" ,
                            "Pressure values over the trial", m.pressureData,7, m.sessionNo+m.trailNo );
            chart5.pack( );
            RefineryUtilities.centerFrameOnScreen( chart5 );
            chart5.setVisible( true );

            CreateGraphSubDivided chart6 = new CreateGraphSubDivided(
                            "Pressure values over the trial BotBotLeft",
                            "Pressure values over the trial", m.pressureData, 8, m.sessionNo+m.trailNo );
            chart6.pack();
            RefineryUtilities.centerFrameOnScreen(chart6);
            chart6.setVisible(true);

            CreateGraphSubDivided chart7 = new CreateGraphSubDivided(
                            "Pressure values over the trial BotBotRight",
                            "Pressure values over the trial", m.pressureData, 9, m.sessionNo+m.trailNo );
            chart7.pack();
            RefineryUtilities.centerFrameOnScreen(chart7);
            chart7.setVisible(true);

            CreateGraphSubDivided chart8 = new CreateGraphSubDivided(
                            "Pressure values over the trial BotTopRight",
                            "Pressure values over the trial", m.pressureData, 10, m.sessionNo+m.trailNo );
            chart8.pack();
            RefineryUtilities.centerFrameOnScreen(chart8);
            chart8.setVisible(true);
    	    
	}

	private void accumulateData() {
		LinkedList<String> data = new LinkedList<String>();
		data.add(this.sessionNo);
		data.add(this.trailNo);
		data.add("" + this.numberOfReadings);
		data.add("" + this.avgTopTopLeftLine);
		data.add("" + this.avgTopBotLeftLine);
		data.add("" + this.avgTopBotRightLine);
		data.add("" + this.avgTopTopRightLine);
                data.add("" + this.avgBotTopLeftLine);
		data.add("" + this.avgBotBotLeftLine);
		data.add("" + this.avgBotBotRightLine);
		data.add("" + this.avgBotTopRightLine);
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
				this.avgTopTopLeftLine = 0;
				this.avgTopBotLeftLine = 0;
				this.avgTopTopRightLine = 0;
				this.avgTopBotRightLine = 0;
                                this.avgBotTopLeftLine = 0;
				this.avgBotBotLeftLine = 0;
				this.avgBotTopRightLine = 0;
				this.avgBotBotRightLine = 0;
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 16; j++) {
						// skip the first entry (time stamp)
						this.topTopLeft[i][j] = Float.parseFloat(line[i * 16 + j
								+ 1]);
                                                this.topBotLeft[i][j] = Float.parseFloat(line[256 + i * 16
								+ j + 1]);
						this.topBotRight[i][j] = Float.parseFloat(line[256 * 2 + i
								* 16 + j + 1]);
						this.topTopRight[i][j] = Float.parseFloat(line[256 * 3 + i
								* 16 + j + 1]);
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
						this.botTopLeft[i-8][j] = Float.parseFloat(line[i * 16 + j
								+ 1]);
                                                this.botBotLeft[i-8][j] = Float.parseFloat(line[256 + i * 16
								+ j + 1]);
						this.botBotRight[i-8][j] = Float.parseFloat(line[256 * 2 + i
								* 16 + j + 1]);
						this.botTopRight[i-8][j] = Float.parseFloat(line[256 * 3 + i
								* 16 + j + 1]);
						// get average pressure for all four sensors
						this.avgBotTopLeftLine = this.avgBotTopLeftLine
								+ this.botTopLeft[i-8][j];
						this.avgBotBotLeftLine = this.avgBotBotLeftLine
								+ this.botBotLeft[i-8][j];
						this.avgBotTopRightLine = this.avgBotTopRightLine
								+ this.botTopRight[i-8][j];
						this.avgBotBotRightLine = this.avgBotBotRightLine
								+ this.botBotRight[i-8][j];
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
                                
                                
                                this.accumulateData();
                                
                                
				// add to total average
				this.avgTopLeft += this.avgTopTopLeftLine;
				this.avgBotLeft += this.avgTopBotLeftLine;
				this.avgTopRight += this.avgTopTopRightLine;
				this.avgBotRight += this.avgTopBotRightLine;
                                this.avgTopLeft += this.avgBotTopLeftLine;
				this.avgBotLeft += this.avgBotBotLeftLine;
				this.avgTopRight += this.avgBotTopRightLine;
				this.avgBotRight += this.avgBotBotRightLine;
				
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