package org.sgnn7.fourier.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.sgnn7.fourier.dft.ComplexNumberImage;
import org.sgnn7.fourier.util.ARGBUtils;

public class JEditableLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	private static final boolean IS_OFFSET = true;

	private static final Cursor crosshairsCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	private static final Cursor arrowCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	private final ComplexNumberImage complexImage;
	private final int scale;
	private int lastColorUsed;

	public JEditableLabel(final ComplexNumberImage complexImage, final int scale) {
		super(new ImageIcon(ARGBUtils.scaleImage(getFrequencyDomainImage(complexImage), scale)));
		this.complexImage = complexImage;
		this.scale = scale;

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point imageCoordinate = toImageCoordinates(e.getPoint());
				updatePixel(e.getButton(), imageCoordinate);
				setIcon(new ImageIcon(ARGBUtils.scaleImage(getFrequencyDomainImage(complexImage), scale)));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(arrowCursor);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(crosshairsCursor);
			}
		});
		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point imageCoordinate = toImageCoordinates(e.getPoint());
				updatePixel(e.getButton(), imageCoordinate);
				setIcon(new ImageIcon(ARGBUtils.scaleImage(getFrequencyDomainImage(complexImage), scale)));
			}

		});
	}

	private static BufferedImage getFrequencyDomainImage(ComplexNumberImage complexImage) {
		return complexImage.getFrequencyDomainImage(IS_OFFSET, false);
	}

	private Point toImageCoordinates(Point point) {
		int distanceFromControlCenterX = point.x - getWidth() / 2;
		int distanceFromControlCenterY = point.y - getHeight() / 2;
		int x = complexImage.getWidth() / 2 + distanceFromControlCenterX / scale;
		int y = complexImage.getHeight() / 2 + distanceFromControlCenterY / scale;
		return new Point(x, y);
	}

	private void updatePixel(int mouseButton, Point imageCoordinate) {
		if (mouseButton == MouseEvent.BUTTON1) {
			lastColorUsed = 0;
			complexImage.setFrequencyDomainPixel(imageCoordinate.x, imageCoordinate.y, IS_OFFSET, lastColorUsed);
		} else if (mouseButton == MouseEvent.BUTTON3) {
			lastColorUsed = 0xffffff;
			complexImage.setFrequencyDomainPixel(imageCoordinate.x, imageCoordinate.y, IS_OFFSET, lastColorUsed);
		} else {
			complexImage.setFrequencyDomainPixel(imageCoordinate.x, imageCoordinate.y, IS_OFFSET, lastColorUsed);
		}
	}
}
