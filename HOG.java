// Library: Histogram of Oriented Gradients - HOG in Java
// Author: Hy Truong Son
// Website: http://people.inf.elte.hu/hytruongson/
// Email: sonpascal93@gmail.com
// Copyright 2015 (c) Hy Truong Son. All rights reserved. Only use for academic purposes.

import java.io.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class HOG {

	private int     nAngles;
	private double  angleValues [];
	
	private int     widthCell;
	private int     heightCell;
	
	private final int SobelX [][] = {
		{ -3, 0,  3 },
		{-10, 0, 10 },
		{ -3, 0,  3 },
	};
	
	private final int SobelY [][] = {
		{-3, -10, -3},
		{ 0,   0,  0},
		{ 3,  10,  3},
	};
	
	public HOG(int nAngles, int widthCell, int heightCell) {
		this.nAngles    = nAngles;
		this.widthCell  = widthCell;
		this.heightCell = heightCell;
		
		angleValues     = new double [this.nAngles + 1];
		angleValues[0]  = 0.0;
		
		double delta    = Math.PI / this.nAngles;
		
		for (int angleIndex = 1; angleIndex <= this.nAngles; ++angleIndex) {
			angleValues[angleIndex] = angleValues[angleIndex - 1] + delta;
		}
	}
	
	private double findAngleValue(double y, double x) {
		double alpha = Math.atan2(y, x);
		if (alpha < 0.0)
			alpha += Math.PI;
		return alpha;
	}
	
	private int findAngleIndex(double angleValue) {
		double difference = angleValue;
		int angleIndex = 0;
		
		for (int i = 1; i <= nAngles; ++i) {
			if (Math.abs(angleValues[i] - angleValue) < difference) {
				difference = Math.abs(angleValues[i] - angleValue);
				angleIndex = i;
			}
		}
		
		return angleIndex % nAngles;
	}
	
	private int SobelOperator(int grayScale[][], int Sobel[][], int x, int y) {
		int sum = 0;
		for (int i = -1; i <= 1; ++i)
			for (int j = -1; j <= 1; ++j)
				sum += grayScale[x + i][y + j] * Sobel[1 + i][1 + j];
		return sum;
	}
	
	private int verticalDerivative(int grayScale[][], int x, int y) {
		return grayScale[x - 1][y] - grayScale[x + 1][y];
	}
	
	private int horizontalDerivative(int grayScale[][], int x, int y) {
		return grayScale[x][y - 1] - grayScale[x][y + 1];
	}
	
	public FeatureHOG findFeature(int grayScale[][], int widthImg, int heightImg) {
	    int nRows       = (heightImg - 2)   / heightCell;
	    int nColumns    = (widthImg - 2)    / widthCell;
		
		FeatureHOG feature = new FeatureHOG(nRows, nColumns, nAngles);
		
		for (int rowIndex = 0; rowIndex < nRows; ++rowIndex) {
			for (int columnIndex = 0; columnIndex < nColumns; ++columnIndex) {
			
				int x1 = 1 + columnIndex * widthCell;
				int y1 = 1 + rowIndex * heightCell;
				
				int x2 = (columnIndex + 1) * widthCell;
				int y2 = (rowIndex + 1) * heightCell;
				
				for (int x = x1; x <= x2; x++) {
					for (int y = y1; y <= y2; y++) {
					
						// int deltaX = verticalDerivative(Intensity, x, y);
						// int deltaY = horizontalDerivative(Intensity, x, y);
						
						int deltaX = SobelOperator(grayScale, SobelX, x, y);
						int deltaY = SobelOperator(grayScale, SobelY, x, y);
						
						int magnitude           = Math.abs(deltaX) + Math.abs(deltaY);
						double angleValue       = findAngleValue(deltaY, deltaX);
						int angleIndex          = findAngleIndex(angleValue);
						
						feature.updateHistogram(rowIndex, columnIndex, angleIndex, magnitude);
					}
				}
			}
		}
		
		feature.normalize();
		
		return feature;
	}
	
	public FeatureHOG findFeature(String imageName) throws IOException {
		File inputFile      = new File(imageName);
		BufferedImage image = ImageIO.read(inputFile);
		
		int widthImg    = image.getWidth(null);
		int heightImg   = image.getHeight(null);
		
		int grayScale[][] = new int [widthImg][heightImg];
		
		for (int widthIndex = 0; widthIndex < widthImg; ++widthIndex) {
			for (int heightIndex = 0; heightIndex < heightImg; ++heightIndex) {
				
				int RGB     = image.getRGB(widthIndex, heightIndex); 
				int red     = (RGB & 0x00ff0000) >> 16;
				int green   = (RGB & 0x0000ff00) >> 8;
				int blue    =  RGB & 0x000000ff;
				
				grayScale[widthIndex][heightIndex] = (red * 299 + green * 587 + blue * 114) / 1000;
			}
		}
		
		return findFeature(grayScale, widthImg, heightImg);
	}
	
	private void drawArrow(BufferedImage image, int x0, int y0, double angleValue, int magnitude) {
		Graphics2D g2d = image.createGraphics();
		
		int x1 = (int)(x0 + 0.5 * widthCell  * Math.cos(angleValue));
		int y1 = (int)(y0 + 0.5 * heightCell * Math.sin(angleValue));
		
		int x2 = (int)(x0 - 0.5 * widthCell  * Math.cos(angleValue));
		int y2 = (int)(y0 - 0.5 * heightCell * Math.sin(angleValue));
		
		g2d.setColor(new Color((int)(magnitude), (int)(magnitude), (int)(magnitude)));
		g2d.drawLine(x1, y1, x2, y2);		
	}
	
	private String getType(String fileName) {
		int dotPosition = 0;
		for (int i = 0; i < fileName.length(); ++i)
			if (fileName.charAt(i) == '.'){
				dotPosition = i;
				break;
			}
		
		String ret = "";
		for (int i = dotPosition + 1; i < fileName.length(); ++i) {
		    ret += fileName.charAt(i);
		}
		
		return ret;
	}
	
	public BufferedImage drawFeatures(int widthImg, int heightImg, FeatureHOG feature) {
	    BufferedImage outputImg = new BufferedImage(widthImg, heightImg, BufferedImage.TYPE_INT_RGB);
		
		int nRows       = feature.getNumberRows();
		int nColumns    = feature.getNumberColumns();
		int nAngles     = feature.getNumberAngles();
		
		for (int rowIndex = 0; rowIndex < nRows; ++rowIndex) {
			for (int columnIndex = 0; columnIndex < nColumns; ++columnIndex) {
				for (int angleIndex = 0; angleIndex < nAngles; ++angleIndex) {
				
					int x = 1 + columnIndex * widthCell  + widthCell  / 2;
					int y = 1 + rowIndex    * heightCell + heightCell / 2;
					
					drawArrow(outputImg, x, y, angleValues[angleIndex], feature.getHistogram(rowIndex, columnIndex, angleIndex));
				}
			}
		}
		
		return outputImg;
	}
	
	public void drawFeatures(int widthImg, int heightImg, FeatureHOG feature, String outputName) throws IOException {
	    BufferedImage outputImg = drawFeatures(widthImg, heightImg, feature);
		File outputFile = new File(outputName);
		ImageIO.write(outputImg, getType(outputName), outputFile);
	}
	
	public void drawFeatures(String inputName, FeatureHOG feature, String outputName) throws IOException {
		File inputFile          = new File(inputName);
		BufferedImage inputImg  = ImageIO.read(inputFile);
		
		int widthImg    = inputImg.getWidth(null);
		int heightImg   = inputImg.getHeight(null);
		
		drawFeatures(widthImg, heightImg, feature, outputName);
	}
	
}
