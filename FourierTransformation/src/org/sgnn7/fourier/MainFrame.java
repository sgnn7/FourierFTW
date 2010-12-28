package org.sgnn7.fourier;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.sgnn7.fourier.dft.ComplexNumberImage;
import org.sgnn7.fourier.ui.JEditableLabel;
import org.sgnn7.fourier.util.ARGBUtils;

public class MainFrame extends JFrame {
	private static final String SOURCE_IMAGE = "img/rotTextSm.gif";
	private static final long serialVersionUID = 1L;

	public static void main(String args[]) {
		new MainFrame();
	}

	private BufferedImage image;

	MainFrame() {
		initializeFrame();
		loadImage();

		final ComplexNumberImage complexImage = new ComplexNumberImage(image);
		BufferedImage spatialDomainImage = complexImage.getSpatialDomainImage();

		final int scaleMutliplier = 2;
		JLabel originalImageLabel = new JLabel(new ImageIcon(ARGBUtils.scaleImage(image, scaleMutliplier)));
		JEditableLabel editableLabel = new JEditableLabel(complexImage, scaleMutliplier);
		final JLabel recalculatedImage = new JLabel(new ImageIcon(ARGBUtils.scaleImage(spatialDomainImage,
				scaleMutliplier)));

		recalculatedImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BufferedImage modifiedSpatialDomainImage = complexImage.getSpatialDomainImage();
				recalculatedImage.setIcon(new ImageIcon(ARGBUtils.scaleImage(modifiedSpatialDomainImage,
						scaleMutliplier)));
			}
		});

		add(originalImageLabel);
		add(editableLabel);
		add(recalculatedImage);

		this.setSize(image.getWidth() * 6 + 50, image.getHeight() * 2 + 50);
		centerJFrame();
		setVisible(true);
		requestFocus();
	}

	private void centerJFrame() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		int w = this.getSize().width;
		int h = this.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		this.setLocation(x, y);
	}

	private void initializeFrame() {
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLookAndFeel();
		this.setLayout(new GridLayout());
	}

	private void loadImage() {
		try {
			image = ImageIO.read(new File(SOURCE_IMAGE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native look and feel: " + e);
		}
	}
}
