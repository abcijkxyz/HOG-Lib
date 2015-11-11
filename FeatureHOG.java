// Library: Histogram of Oriented Gradients - HOG in Java
// Author: Hy Truong Son
// Website: http://people.inf.elte.hu/hytruongson/
// Email: sonpascal93@gmail.com
// Copyright 2015 (c) Hy Truong Son. All rights reserved. Only use for academic purposes.

public class FeatureHOG {
		
	private int nColumns;
	private int nRows;
	private int nAngles;
	private int histogram [][][];
	
	public FeatureHOG(int nRows, int nColumns, int nAngles) {
	    this.nRows      = nRows;
	    this.nColumns   = nColumns;
	    this.nAngles    = nAngles;
	    
	    histogram       = new int [this.nRows][this.nColumns][this.nAngles];
	}
	
	public int getNumberRows() {
	    return nRows;
	}
	
	public int getNumberColumns() {
	    return nColumns;
	}
	
	public int getNumberAngles() {
	    return nAngles;
	}
	
	public void setNumberRows(int nRows) {
	    this.nRows  = nRows;
	    histogram   = new int [this.nRows][nColumns][nAngles];
	}
	
	public void setNumberColumns(int nColumns) {
	    this.nColumns   = nColumns;
	    histogram       = new int [nRows][this.nColumns][nAngles];
	}
	
	public void setNumberAngles(int nAngles) {
	    this.nAngles    = nAngles;
	    histogram       = new int [nRows][nColumns][this.nAngles];
	}
	
	public void updateHistogram(int rowIndex, int columnIndex, int angleIndex, int magnitude) {
	    if (histogram[rowIndex][columnIndex][angleIndex] < magnitude) {
	        histogram[rowIndex][columnIndex][angleIndex] = magnitude;
	    }
	}
	
	public void setHistogram(int rowIndex, int columnIndex, int angleIndex, int magnitude) {
	    histogram[rowIndex][columnIndex][angleIndex] = magnitude;
	}
	
	public int getHistogram(int rowIndex, int columnIndex, int angleIndex) {
	    return histogram[rowIndex][columnIndex][angleIndex];
	}
	
	public void normalize() {
	    int maxMagnitude = 0;
		for (int rowIndex = 0; rowIndex < nRows; rowIndex++) {
			for (int columnIndex = 0; columnIndex < nColumns; ++columnIndex) {
				for (int angleIndex = 0; angleIndex < nAngles; ++angleIndex) {
					maxMagnitude = Math.max(maxMagnitude, histogram[rowIndex][columnIndex][angleIndex]);
				}
			}
		}
		
		for (int rowIndex = 0; rowIndex < nRows; ++rowIndex) {
			for (int columnIndex = 0; columnIndex < nColumns; ++columnIndex) {
				for (int angleIndex = 0; angleIndex < nAngles; ++angleIndex) {
					histogram[rowIndex][columnIndex][angleIndex] = histogram[rowIndex][columnIndex][angleIndex] * 255 / maxMagnitude;
				}
			}
		}
	}
	
}
