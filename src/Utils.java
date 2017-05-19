
public class Utils {
	public static int getR(int rgb) {return (rgb >> 16) & 0x000000FF;}
	public static int getG(int rgb) {return (rgb >>8 ) & 0x000000FF;}
	public static int getB(int rgb) {return (rgb) & 0x000000FF;}
	public static int getRGB(int r, int g, int b) {return 0xFF000000 | (r << 16) & 0x00FF0000 | (g << 8) & 0x0000FF00 | b & 0x000000FF;}

	public static int checkPixelBound(int s) {return s > 255 ? 255 : (s < 0 ? 0 : s);}
	
	public static int [] getInterpolationColor(int [] color1, int [] color2, double percent) {
		int [] color = new int [3];
		for (int i = 0; i < 3; i++) {
			color[i] = checkPixelBound((int)(color1[i] + ( (color2[i] - color1[i]) * percent )));
		}
		return color;
	}
	
	// 為了效能而將整數與浮點完全分開，演算法一致 (浮點乘法非常消耗效能)
	public static int[][] multiply(int [][] a, int [][] b)  {
		int  c [][] = new int[a.length][b[0].length];
		for (int k=0; k<a.length; k++)
			for (int i=0; i<b.length; i++)
				for (int j=0; j<b[0].length; j++)
					c[k][j]+= a[k][i]*b[i][j];
	    return c;
	}
	public static double[][] multiply(double [][] a, double [][] b)  {
		double  c [][] = new double[a.length][b[0].length];
		for (int k=0; k<a.length; k++)
			for (int i=0; i<b.length; i++)
				for (int j=0; j<b[0].length; j++)
					c[k][j]+= a[k][i]*b[i][j];
	    return c;
	}
	
	public static int [] bilinearColor(int [][][] data, double x, double y) {
		double fX = Math.floor(x), fY = Math.floor(y);
		int iX = (int) fX, iY = (int) fY;
		
		if (iX + 1 >= data[0].length || iY + 1 >= data.length)
			return data[(int) y][(int) x];
		
		int [] color1 = data[iY][iX], color2 = data[iY][iX+1], 
				color3 = data[iY+1][iX], color4 = data[iY+1][iX+1];
		
		int [] colorA = Utils.getInterpolationColor(color1, color2, x - fX),
				colorB = Utils.getInterpolationColor(color3, color4, x - fX);

		return Utils.getInterpolationColor(colorA, colorB, y - fY);
	}

}
