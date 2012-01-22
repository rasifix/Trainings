package com.github.rasifix.trainings.geodeployer.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.github.rasifix.trainings.ElevationModel;


public class ArcGridElevationModel implements ElevationModel {

	private static final int SHORT_NO_DATA_VALUE = Integer.MAX_VALUE;
	
	private int rows;
	private int cols;
	private int cellsize;
	private int nodataValue;
	private double xllcorner;
	private double yllcorner;
	
	private int[][] data;
	
	public ArcGridElevationModel(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line = null;
				
		int currentRow = 1;
		while (currentRow <= 6 && (line = reader.readLine()) != null) {
			if (currentRow++ <= 6)  {
				String[] parts = line.split("[ ]+");
				if ("NCOLS".equals(parts[0])) {
					cols = Integer.parseInt(parts[1]);
				} else if ("NROWS".equals(parts[0])) {
					rows = Integer.parseInt(parts[1]);
				} else if ("NODATA_VALUE".equals(parts[0])) {
					nodataValue = Integer.parseInt(parts[1]);
				} else if ("CELLSIZE".equals(parts[0])) {
					cellsize = Integer.parseInt(parts[1]);
				} else if ("XLLCORNER".equals(parts[0])) {
					xllcorner = Double.parseDouble(parts[1]);
				} else if ("YLLCORNER".equals(parts[0])) {
					yllcorner = Double.parseDouble(parts[1]);
				}
			} 
		}

		this.data = new int[rows][];

		for (int row = 0; row < rows; row++) {
			line = reader.readLine();
			if (line == null) {
				System.out.println(row);
				return;
			}
			data[row] = new int[cols];
			String[] parts = line.split("[ ]+");
			for (int col = 0; col < cols; col++) {
				double value = Double.parseDouble(parts[col]);
				if (value == nodataValue) {
					data[row][col] = SHORT_NO_DATA_VALUE;
				} else {
					data[row][col] = (int) Math.round(value * 10);
				}
			}
		}
		
		reader.close();		
	}
	
	@Override
	public boolean containsPosition(double easting, double northing) {
		if (easting < xllcorner || easting >= xllcorner + cellsize * cols) {
			return false;
		}
		if (northing < yllcorner || northing > yllcorner + cellsize * rows) {
			return false;
		}
		return true;
	}

	@Override
	public double elevationForPosition(double easting, double northing) {
		int col = findCell(xllcorner, cellsize, easting);
		int row = findCell(yllcorner, cellsize, northing);
		int intValue = data[1199 - row][col];
		return intValue == SHORT_NO_DATA_VALUE ? NO_DATA_VALUE : intValue / 10.0;
	}

	private static int findCell(double xllcorner, double cellsize, double x) {
		return (int) ((x - xllcorner) / cellsize);
	}

}
