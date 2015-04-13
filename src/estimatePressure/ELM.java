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
	private double wInRange;
	//output weights
	private Matrix w_out;
	//scalar
	private Matrix b;
	private double bRange;
	
	private int inputDim;
	private int hiddenDim;
	private int outputDim;
	private int numTrainingSample;
	
	private double MACHEPS = 2E-16;
	
	public ELM(int inDim,int hidDim,int outDim,double wRan,double bRan)
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
				//this.w_in.set(i, j, ( this.wInRange*Math.random() ) );
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
		System.out.println("In elm mat dimention hx : "+ this.hx.getRowDimension() + " "+ this.hx.getColumnDimension());
		
		//calculate w_out = pseudoinverse(H).Y
		//this.w_out = this.y.times(this.pseudoInverse(this.hx));
		/**/
		//TODO check which of these is the right way to get w_out = h-1.y
		//calculate w_out = ((H'H)^-1) H'Y
		Matrix hth = this.hx.transpose().times(this.hx);
		System.out.println("In elm mat dimension hx : "+ this.hx.getRowDimension() + " "+ this.hx.getColumnDimension());
		Matrix hthInv;
		try {
			hthInv = hth.inverse();
			System.out.println("In elm mat calculating inverse");
		} catch (Exception e) {
			System.out.println("In elm mat catch");
			hthInv = this.pseudoInverse(hth);
		}
		System.out.println("In elm mat dimension hthInv : "+ hthInv.getRowDimension() + " "+ hthInv.getColumnDimension());
		Matrix hthInvht = hthInv.times( this.hx.transpose() );
		System.out.println("In elm mat dimension hthInvht : "+ hthInvht.getRowDimension() + " "+ hthInvht.getColumnDimension());
		System.out.println("In elm mat dimension y : "+ this.y.getRowDimension() + " "+ this.y.getColumnDimension());
		//TODO check if order of multiplication is correct
                //this.w_out = hthInvht.transpose().times(this.y.transpose());//testing
		this.w_out = this.y.times(hthInvht);//hthInvht.times(this.y);
		/**/
		
		System.out.println("In elm mat dimention w_out : "+ this.w_out.getRowDimension() + " "+ this.w_out.getColumnDimension());
		System.out.println("In elm mat values of w_out :");
		//print matrix
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
                
                //print values
                System.out.println("value of w_in is: ");
                for (int p = 0; p < this.w_in.getRowDimension(); p++){
			for (int q = 0; q < this.w_in.getColumnDimension(); q++){
				System.out.print(this.w_in.get(p, q) + "\t");
			}
			System.out.println();
		}
                //print values
                System.out.println("value of winx is: ");
                for (int p = 0; p < winX.getRowDimension(); p++){
			for (int q = 0; q < winX.getColumnDimension(); q++){
				System.out.print(winX.get(p, q) + "\t");
			}
			System.out.println();
		}
                
		Matrix winXb = new Matrix(winX.getRowDimension(),winX.getColumnDimension());
		for(int j=0; j<winX.getColumnDimension(); j++){
			for(int i=0; i<winX.getRowDimension(); i++){
				winXb.set(i, j, ( winX.get(i, j) + this.b.get(i, 0) ));
			}
		}
                
                //print values
                System.out.println("Values for matrix winxb in elm is: ");
                for (int r = 0; r < winXb.getRowDimension(); r++){
			for (int s = 0; s < winXb.getColumnDimension(); s++){
				System.out.print(winXb.get(r, s) + "\t");
			}
			System.out.println();
		}
                
		//Hidden Layer
		h = sigma(winXb);
                
                //print values
                System.out.println("Values for matrix h in elm is: ");
                for (int k = 0; k < h.getRowDimension(); k++){
			for (int l = 0; l < h.getColumnDimension(); l++){
				System.out.print(h.get(k, l) + "\t");
			}
			System.out.println();
		}
                
		//calculate predictedValues = w_out*h
                //for tested with transposed values
                //predictedValues = wOut.transpose().times( h.transpose() );// test
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
	
	Matrix sigma(Matrix in)
	{	
                double arrOut[][] = new double[in.getRowDimension()][in.getColumnDimension()]; 
		for(int i=0; i<in.getRowDimension(); i++){
			for(int j=0; j<in.getColumnDimension(); j++){
                                //System.out.print( Math.pow( Math.E, (- in.get(i, j)) ) + "\t" );
				//arrOut[i][j] = 1.0 /(1.0 + Math.exp( -( in.get(i, j) ) ) );
                                arrOut[i][j] = 1.0 /(1.0 + Math.pow( Math.E, (- in.get(i, j)) ) );
			}
		}
		Matrix out = new Matrix(arrOut);
                
                //print values
                System.out.println("Output of sigma fuction is: ");
                for (int p = 0; p < out.getRowDimension(); p++){
			for (int q = 0; q < out.getColumnDimension(); q++){
				System.out.print(out.get(p, q) + "\t");
			}
			System.out.println();
		}
                
		return out;
	}
}