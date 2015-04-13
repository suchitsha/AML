/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estimatePressure;

/**
 *
 * @author ssharma
 */
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import Jama.Matrix;

public class CreateGraph extends ApplicationFrame {
	//serial id
	private static final long serialVersionUID = 1L;
	//storage for pressure data
	private Matrix errorData ;
	//which sensor value to be displayed
	private int sensorNumber;
	
	public CreateGraph(String applicationTitle, String chartTitle, Matrix errData,int sensorNum) {
		super(applicationTitle);
		this.errorData = errData;
		this.sensorNumber = sensorNum;
		JFreeChart lineChart = ChartFactory.createLineChart(chartTitle,
				"Time", "Error",
				createDataset(), PlotOrientation.VERTICAL, true, true, false);
		//lineChart.setBackgroundPaint(Color.red);
		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		setContentPane(chartPanel);
		
		File fileLineChart = new File( "Error values"+ this.sensorNumber + ".jpeg" ); 
	    try {
			ChartUtilities.saveChartAsJPEG(fileLineChart ,lineChart, 1200 ,500);
		} catch (IOException e) {
			e.printStackTrace();
		}
	   
	}

	private DefaultCategoryDataset createDataset() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int j = 0; j < this.errorData.getColumnDimension() ;j++ ){
			dataset.addValue( this.errorData.get(this.sensorNumber,j) , "error" , "" + j);
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