package com.adfero.projectboard;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.res.Resources;
import android.graphics.Color;

public class ColorGenerator {
	public static final int[] colors = new int[]{
		R.color.colgen_night,
		R.color.colgen_morning,
		R.color.colgen_noon,
		R.color.colgen_evening,
		R.color.colgen_night
	};
	
	private Resources resources;
	
	public ColorGenerator(Resources resources) {
		this.resources = resources;
	}
	
	public int currentColor() {
		Date date = new Date();
		Calendar calendar = GregorianCalendar.getInstance(); 
		calendar.setTime(date);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		return this.getColorForHour(hour);
	}
	
	public int getColorForHour(int h) {
		if (h >= 0 && h <= 24) {
			double markerDistance = 24f / (double)colors.length;
			double hour = (double)h;
			for (int i=0;i<colors.length-1;i++) {
				double min = (double)i * markerDistance;
				double max = ((double)i+1f) * markerDistance;
				if (hour >= min && hour < max) {
					double pctDistance = (hour - min) / markerDistance;
					int colorA = this.resources.getColor(colors[i]);
					int colorB = this.resources.getColor(colors[i+1]);
					ColorDistance dist = new ColorDistance(colorA,colorB);
					int red = Color.red(colorA) + (int)(dist.deltaRed * pctDistance);
					int green = Color.green(colorA) + (int)(dist.deltaGreen * pctDistance);
					int blue = Color.blue(colorA) + (int)(dist.deltaBlue * pctDistance);					
					return Color.rgb(red, green, blue);
				}
			}
		}
		return 0;
	}
	
	private class ColorDistance {
		int deltaRed;
		int deltaGreen;
		int deltaBlue;
		
		public ColorDistance(int colorA, int colorB) {
			this.deltaRed = Color.red(colorB) - Color.red(colorA);
			this.deltaGreen = Color.green(colorB) - Color.green(colorA);
			this.deltaBlue = Color.blue(colorB) - Color.blue(colorA);
		}
	}
}
