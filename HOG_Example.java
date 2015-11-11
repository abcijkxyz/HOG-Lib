// Library: Histogram of Oriented Gradients - HOG in Java
// Author: Hy Truong Son
// Website: http://people.inf.elte.hu/hytruongson/
// Email: sonpascal93@gmail.com
// Copyright 2015 (c) Hy Truong Son. All rights reserved. Only use for academic purposes.

import java.io.*;

public class HOG_Example {

    public static void main(String args[]) {
		String inputName    = "MyPhoto.jpg";            // Input image 
		String outputName   = "MyPhoto-HOG-9-8-8.png";  // Output image
		
		int nAngles     = 9; // Number of spatial bins
		int widthCell   = 8; // Patch size 8 by 8
		int heightCell  = 8;
		
		HOG obj = new HOG (nAngles, widthCell, heightCell);
	    FeatureHOG feature = null;    
	    
		try {
		    feature = obj.findFeature(inputName);
		} catch (IOException exc) {
		    System.err.println(exc.toString());
		}
		
		try {
		    obj.drawFeatures(inputName, feature, outputName);
	    } catch (IOException exc) {
	        System.err.println(exc.toString());
	    }
	}
	
}
