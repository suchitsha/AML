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
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class CreateGraphOverTrial extends ApplicationFrame {
	//serial id
	private static final long serialVersionUID = 1L;
	//storage for pressure data
	private LinkedList<LinkedList<String>> pressureData = new LinkedList<LinkedList<String>>() ;
	//which sensor value to be displayed
	private int sensorNumber;
	
	public CreateGraphOverTrial(String applicationTitle, String chartTitle, LinkedList<LinkedList<String>> pData,int sensorNum ,String sessionNo) {
		super(applicationTitle);
		this.pressureData = pData;
		this.sensorNumber = sensorNum;
		JFreeChart lineChart = ChartFactory.createLineChart(chartTitle,
				sessionNo, "Average Pressure Value over the time(per trail)",
				createDataset(), PlotOrientation.VERTICAL, true, true, false);
		//lineChart.setBackgroundPaint(Color.red);
		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		setContentPane(chartPanel);
		
		File fileLineChart = new File( sessionNo +"_sensor" + this.sensorNumber + ".jpeg" ); 
	    try {
			ChartUtilities.saveChartAsJPEG(fileLineChart ,lineChart, 1200 ,500);
		} catch (IOException e) {
			e.printStackTrace();
		}
	   
	}

	private DefaultCategoryDataset createDataset() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (LinkedList<String> lineData : this.pressureData) {
			dataset.addValue(Double.parseDouble(lineData.get(this.sensorNumber)), "pressure", lineData.get(2));
		}
		return dataset;
	}
	// public static void main( String[ ] args )
	// {
	// BuildGraph chart = new BuildGraph(
	// "Pressure values over the session" ,
	// "Pressure values over the session", "a");
	//
	// chart.pack( );
	// RefineryUtilities.centerFrameOnScreen( chart );
	// chart.setVisible( true );
	// }
}