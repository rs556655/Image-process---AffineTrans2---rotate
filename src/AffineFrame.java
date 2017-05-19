import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class AffineFrame extends JFrame {

	private static final long serialVersionUID = 4390493953307669741L;
	JPanel cotrolPanel = new JPanel();
	ImagePanel imagePanel = new ImagePanel();
	JButton btnShow, btnRotate;
	
	JPanel bgColorPanel = new JPanel();
	JPanel rotatePanel = new JPanel();
	
	JTextField 
		bgR = new JTextField("0"), 
		bgG = new JTextField("0"), 
		bgB = new JTextField("0");
				
	JSlider rotateControl = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
	
	JLabel 
		label_bgR = new JLabel("背景 (R)　"),
		label_bgG = new JLabel("背景 (G)　"),
		label_bgB = new JLabel("背景 (B)　"),
		label_rotate = new JLabel("旋轉角度　");
	
	final int[][][] data;
	int height, width;
	BufferedImage img = null;
	
	ActionListener buttonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == btnRotate) processRotate();
			else imagePanel.showImage(width, height, data);
		}
	};
	
	ChangeListener rotateValueChangeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			btnRotate.setText("旋轉 " + rotateControl.getValue() + " 度");
		}
	};
	
	protected AffineFrame(){
		setTitle("影像處理");
		
		try {
		    img = ImageIO.read(new File("file/Munich.png"));
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		
		height = img.getHeight();
		width = img.getWidth();
		data = new int[height][width][3]; 
		
		this.setSize(width + 15, height + 77);
		
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				int rgb = img.getRGB(x, y);
				data[y][x][0] = Utils.getR(rgb);
				data[y][x][1] = Utils.getG(rgb);
				data[y][x][2] = Utils.getB(rgb);
			}
		
		btnShow = new JButton("顯示");
		btnRotate = new JButton("旋轉");
		
		// 事件監聽
		btnShow.addActionListener(buttonActionListener);
		btnRotate.addActionListener(buttonActionListener);
		rotateControl.addChangeListener(rotateValueChangeListener);
		
		// 顯示背景色
		bgColorPanel.setLayout(new GridLayout(3,2));
		bgColorPanel.add(label_bgR);
		bgColorPanel.add(bgR);
		bgColorPanel.add(label_bgG);
		bgColorPanel.add(bgG);
		bgColorPanel.add(label_bgB);
		bgColorPanel.add(bgB);
		
		// 旋轉
		rotateControl.setPreferredSize(new Dimension(150,50));
		rotatePanel.setPreferredSize(new Dimension(200,50));
		rotatePanel.setLayout(new GridLayout(2,1));
		
		JPanel rotateControlPanel = new JPanel();
		rotateControlPanel.setLayout(null);
		label_rotate.setBounds(0, 0, 70, 30);
		rotateControl.setBounds(50, 0, 150, 30);
		rotateControlPanel.add(label_rotate);
		rotateControlPanel.add(rotateControl);
		
		rotatePanel.add(rotateControlPanel);	
		rotatePanel.add(btnRotate);
		
		// 控制面板
		cotrolPanel.add(btnShow);
		cotrolPanel.add(new JPanel());
		cotrolPanel.add(bgColorPanel);
		cotrolPanel.add(new JPanel());
		cotrolPanel.add(rotatePanel);
		
		// 主畫面
		setLayout(new BorderLayout());	 
	    add(cotrolPanel, BorderLayout.PAGE_START);
	    add(imagePanel, BorderLayout.CENTER);
	}
	
	/**
	 * 用新背景色填充
	 * @param data 要填充的陣列
	 */
	private void refillBgColor(int [][][] data) {
		int newR = Utils.checkPixelBound(Integer.parseInt(bgR.getText().length() == 0?"0":bgR.getText()));
		int newG = Utils.checkPixelBound(Integer.parseInt(bgG.getText().length() == 0?"0":bgG.getText()));
		int newB = Utils.checkPixelBound(Integer.parseInt(bgB.getText().length() == 0?"0":bgB.getText()));
		
		if (newR == 0 && newG == 0 && newB == 0) return;
		
		for (int [][] eachLine : data)
			for (int [] pixel : eachLine) {
				pixel[0] = newR;
				pixel[1] = newG;
				pixel[2] = newB;
			}
	}
	
	private void processRotate() {
		int angle = rotateControl.getValue();
		int [][] newPos = getCornerPos(angle);
		
		int maxX = Math.max(newPos[0][0], Math.max(newPos[1][0], Math.max(newPos[2][0], newPos[3][0])));
		int minX = Math.min(newPos[0][0], Math.min(newPos[1][0], Math.min(newPos[2][0], newPos[3][0])));
		int maxY = Math.max(newPos[0][1], Math.max(newPos[1][1], Math.max(newPos[2][1], newPos[3][1])));
		int minY = Math.min(newPos[0][1], Math.min(newPos[1][1], Math.min(newPos[2][1], newPos[3][1])));
		
		int [] newSize = new int [] {maxX - minX + 1, maxY - minY + 1};
		int [] offset = new int [] {minX, minY};
		int [][][] nImage = new int [newSize[1]][newSize[0]][3];
		refillBgColor(nImage);

		Area area = new Area( new Polygon(
						new int [] {newPos[0][0]-offset[0],newPos[1][0]-offset[0],newPos[2][0]-offset[0],newPos[3][0]-offset[0]},
						new int [] {newPos[0][1]-offset[1],newPos[1][1]-offset[1],newPos[2][1]-offset[1],newPos[3][1]-offset[1]}, 4));
		
		for (int y = 0; y < nImage.length; y++) {
			for (int x = 0; x < nImage[0].length; x++) {
				if (!area.contains(x, y)) continue;

				double [] p = getRotatePosRev(x+offset[0], y+offset[1], angle);
				if (p[0] < 0.0 || p[1] < 0.0 || p[0] >= data[0].length || p[1] >= data.length) continue;
				nImage[y][x] = Utils.bilinearColor(data, p[0], p[1]);

			}
		}

		imagePanel.showImage(nImage[0].length, nImage.length, nImage);
	}
	
	private int [][] getCornerPos(double angle) {
		int [] 	posA = getRotatePos(0, 0, angle), 
				posB = getRotatePos(data[0].length, 0, angle),
				posC = getRotatePos(data[0].length, data.length, angle),
				posD = getRotatePos(0, data.length, angle);
		return new int [][] {posA, posB, posC, posD};
	}
	
	private int [] getRotatePos(int x, int y, double angle) {
		double rad = Math.toRadians(angle);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		double [][] pos = {{x}, {y}, {1.0}};
		double [][] newPos = Utils.multiply(new double [][]
				{{cos, -sin, 0},
				{sin, cos, 0},
				{0, 0, 1}}, 
				pos);
		
		double ny = newPos[1][0], nx = newPos[0][0];
		return new int[] {(int)nx, (int) ny};
	}
	
	private double [] getRotatePosRev(int x, int y, double angle) {
		double rad = Math.toRadians(angle);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		double [][] pos = {{x}, {y}, {1.0}};
		double [][] newPos = Utils.multiply(new double [][]
				{{cos, sin, 0.0},
				{-sin, cos, 0.0},
				{0.0, 0.0, 1.0}}, 
				pos);
		
		double ny = newPos[1][0], nx = newPos[0][0];
		return new double[] {nx, ny};
	}
}
