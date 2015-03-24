/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estimatePressure;

import java.util.LinkedList;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 *
 * @author suchit sharma
 */

public class ELM {
	
	//input Layer values
	private Matrix x;
	//output of hidden Layer
	private Matrix hx;
	//expected output
	private Matrix y;
	//input weights
	private Matrix w_in;
	private int wInRange;
	//output weights
	private Matrix w_out;
	//scalar
	private Matrix b;
	private int bRange;
	
	private int inputDim;
	private int hiddenDim;
	private int outputDim;
	private int numTrainingSample;
	
	private double MACHEPS = 2E-16;
	
	public ELM(int inDim,int hidDim,int outDim,int wRan,int bRan)
	{
		this.inputDim = inDim;
		this.hiddenDim = hidDim;
		this.outputDim = outDim;
		this.wInRange = wRan;
		this.bRange = bRan;
		
		this.w_in = new Matrix(this.hiddenDim,this.inputDim);
		this.b = new Matrix(this.hiddenDim,1);
		
		for(int i=0; i<this.hiddenDim; i++){
			for(int j=0; j<this.inputDim; j++){
				this.w_in.set(i, j, (2*this.wInRange*Math.random() - this.wInRange));
				if(j==0){
					this.b.set(i, 0, (2*this.bRange*Math.random() - this.bRange));
				}
				
			}
		}
	}
	
	Matrix train(Matrix trainingInput, Matrix expectedValues, int numSample)
	{	
		this.x = trainingInput;
		this.y = expectedValues;
		this.numTrainingSample = numSample;
		this.hx = new Matrix(this.hiddenDim,this.numTrainingSample);
		//calculate h=sigma(w_in*x +b)
		Matrix winX = this.w_in.times(this.x) ;
		Matrix winXb = new Matrix(winX.getRowDimension(),winX.getColumnDimension());
		for(int j=0; j<winX.getColumnDimension(); j++){
			for(int i=0; i<winX.getRowDimension(); i++){
				winXb.set(i, j, ( winX.get(i, j) + this.b.get(i, 0) ));
			}
		}
		this.hx = sigma(winXb);
		
		//calculate w_out = ((H'H)^-1) H'Y
		Matrix hth = this.hx.transpose().times(this.hx);
		System.out.println("In elm mat dimention hx : "+ this.hx.getRowDimension() + " "+ this.hx.getColumnDimension());
		Matrix hthInv;
		try {
			hthInv = hth.inverse();
			System.out.println("In elm mat calculating inverse");
		} catch (Exception e) {
			System.out.println("In elm mat catch");
			hthInv = this.pseudoInverse(hth);
		}
		System.out.println("In elm mat dimention hthInv : "+ hthInv.getRowDimension() + " "+ hthInv.getColumnDimension());
		Matrix hthInvht = hthInv.times( this.hx.transpose() );
		System.out.println("In elm mat dimention hthInvht : "+ hthInvht.getRowDimension() + " "+ hthInvht.getColumnDimension());
		System.out.println("In elm mat dimention y : "+ this.y.getRowDimension() + " "+ this.y.getColumnDimension());
		//TODO check if order of multiplication is correct
		this.w_out = this.y.times(hthInvht);//hthInvht.times(this.y);
		System.out.println("In elm mat dimention w_out : "+ this.w_out.getRowDimension() + " "+ this.w_out.getColumnDimension());
		System.out.println("In elm mat values of w_out :");
		for (int i = 0; i < this.w_out.getRowDimension(); i++){
			for (int j = 0; j < this.w_out.getColumnDimension(); j++){
				System.out.print(this.w_out.get(i, j) + "\t");
			}
			System.out.println();
		}
			
		return this.w_out;
	}
	
	Matrix execute(Matrix input, Matrix wOut, int numSample)
	{	
		Matrix predictedValues;
		Matrix h = new Matrix(this.hiddenDim,numSample);
		
		//calculate h=sigma(w_in*x +b)
		Matrix winX = this.w_in.times(input) ;
		Matrix winXb = new Matrix(winX.getRowDimension(),winX.getColumnDimension());
		for(int j=0; j<winX.getColumnDimension(); j++){
			for(int i=0; i<winX.getRowDimension(); i++){
				winXb.set(i, j, ( winX.get(i, j) + this.b.get(i, 0) ));
			}
		}
		//Hidden Layer
		h = sigma(winXb);
		//calculate predictedValues = w_out*h
		predictedValues = wOut.times(h);
		
		return predictedValues;
	}
	
	Matrix pseudoInverse(Matrix x) {
		int rows = x.getRowDimension();
		int cols = x.getColumnDimension();
		if (rows < cols) {
			Matrix result = pseudoInverse(x.transpose());
			if (result != null)
			{
				result = result.transpose();
			}
			return result;
		}
		SingularValueDecomposition svdX = new SingularValueDecomposition(x);
		if (svdX.rank() < 1)
		{
			return null;
		}
		double[] singularValues = svdX.getSingularValues();
		double tol = Math.max(rows, cols) * singularValues[0] * this.MACHEPS;
		double[] singularValueReciprocals = new double[singularValues.length];
		for (int i = 0; i < singularValues.length; i++)
			if (Math.abs(singularValues[i]) >= tol)
				singularValueReciprocals[i] = 1.0 / singularValues[i];
		double[][] u = svdX.getU().getArray();
		double[][] v = svdX.getV().getArray();
		int min = Math.min(cols, u[0].length);
		double[][] inverse = new double[cols][rows];
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < u.length; j++)
				for (int k = 0; k < min; k++)
					inverse[i][j] += v[i][k] * singularValueReciprocals[k]
							* u[j][k];
		return new Matrix(inverse);
	}
	
	Matrix sigma(Matrix	in)
	{	
		double arrOut[][] = new double[in.getRowDimension()][in.getColumnDimension()]; 
		for(int i=0; i<in.getRowDimension(); i++){
			for(int j=0; j<in.getColumnDimension(); j++){
				arrOut[i][j] = 1/(1+ Math.exp(- in.get(i, j)) );
			}
		}
		Matrix out = new Matrix(arrOut);
		return out;
	}
}