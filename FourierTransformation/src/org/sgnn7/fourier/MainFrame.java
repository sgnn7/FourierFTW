package org.sgnn7.fourier;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.sgnn7.fourier.filters.FrequencyFilterProvider;
import org.sgnn7.fourier.filters.IFrequencyFilterFunction;
import org.sgnn7.fourier.ft.ComplexNumberImage;
import org.sgnn7.fourier.ui.FrequencyDomainImageLabel;
import org.sgnn7.fourier.util.ARGBUtils;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final String SOURCE_IMAGE = null;
	private int scaleMutliplier = 1;

	public static void main(String args[]) {
		new MainFrame();
	}

	private BufferedImage image;
	private ComplexNumberImage complexImage;
	private JLabel originalImageLabel = new JLabel();
	private FrequencyDomainImageLabel frequencyDomainImageLabel = new FrequencyDomainImageLabel();
	private JLabel recalculatedSpatialDomainImage = new JLabel();

	public MainFrame() {
		initializeFrame();
		this.setJMenuBar(createMenubar());
		loadNewImage(SOURCE_IMAGE);

		add(originalImageLabel);
		add(frequencyDomainImageLabel);
		add(recalculatedSpatialDomainImage);

		setVisible(true);
		requestFocus();
	}

	private JMenuBar createMenubar() {
		JMenuBar mainMenuBar = new JMenuBar();

		JMenu mainMenu = new JMenu("File");
		mainMenu.setMnemonic(KeyEvent.VK_F);
		mainMenuBar.add(mainMenu);

		JMenuItem openMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
		mainMenu.add(openMenuItem);
		openMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser imageFileChooser = new JFileChooser();
				FileFilter extensionFilter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "bmp", "gif",
						"png");
				imageFileChooser.setFileFilter(extensionFilter);
				imageFileChooser.setMultiSelectionEnabled(false);
				int returnVal = imageFileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " + imageFileChooser.getSelectedFile().getName());
					loadNewImage(imageFileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});

		JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		mainMenu.add(exitMenuItem);
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JMenu imageManipulationMenu = new JMenu("Image");
		imageManipulationMenu.setMnemonic(KeyEvent.VK_I);
		mainMenuBar.add(imageManipulationMenu);

		JMenuItem resetFrequencyDomainMenuItem = new JMenuItem("Reset Freq", KeyEvent.VK_R);
		imageManipulationMenu.add(resetFrequencyDomainMenuItem);
		resetFrequencyDomainMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				redrawAllImages();
			}
		});

		JMenuItem regenerateImageFromFrequencyDomainMenuItem = new JMenuItem("Freq->Spat", KeyEvent.VK_F);
		imageManipulationMenu.add(regenerateImageFromFrequencyDomainMenuItem);
		regenerateImageFromFrequencyDomainMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reCalculateSpatialImageFromFrequency();
			}
		});

		JMenu imageZoomMenu = new JMenu("Zoom");
		imageZoomMenu.setMnemonic(KeyEvent.VK_Z);
		imageManipulationMenu.add(imageZoomMenu);

		for (int i = 1; i <= 4; i++) {
			final int scaleFactorValue = i;
			JMenuItem zoomMenuItem = new JMenuItem(new Integer(scaleFactorValue).toString(), KeyEvent.VK_F);
			imageZoomMenu.add(zoomMenuItem);
			zoomMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					scaleMutliplier = scaleFactorValue;
					redrawAllImages();
				}
			});
		}

		JMenu filtersMenu = new JMenu("Filters");
		filtersMenu.setMnemonic(KeyEvent.VK_L);
		imageManipulationMenu.add(filtersMenu);

		for (final IFrequencyFilterFunction filter : new FrequencyFilterProvider().getFilters()) {
			JMenuItem filterMenuItem = new JMenuItem(filter.getName(), filter.getAcceleratorKey());
			filtersMenu.add(filterMenuItem);
			filterMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					filter.applyFilterToImage(complexImage);
					frequencyDomainImageLabel.updateImage();
					reCalculateSpatialImageFromFrequency();
				}
			});
		}

		return mainMenuBar;
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
		this.setTitle("FourierFTW");
	}

	private void loadNewImage(String filepath) {
		if (filepath != null) {
			try {
				image = ImageIO.read(new File(filepath));
				redrawAllImages();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			recalculateFrameSize();
			centerJFrame();
		}
	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native look and feel: " + e);
		}
	}

	private void reCalculateSpatialImageFromFrequency() {
		BufferedImage modifiedSpatialDomainImage = complexImage.getSpatialDomainImage();
		recalculatedSpatialDomainImage.setIcon(new ImageIcon(ARGBUtils.scaleImage(modifiedSpatialDomainImage,
				scaleMutliplier)));
		validate();
	}

	private void recalculateFrameSize() {
		this.setSize(new Dimension(getImageWidth() * 3 * scaleMutliplier + 50, getImageHeight() * scaleMutliplier + 50));
	}

	private void redrawAllImages() {
		recalculateFrameSize();
		centerJFrame();

		complexImage = new ComplexNumberImage(image);
		BufferedImage spatialDomainImage = complexImage.getSpatialDomainImage();

		originalImageLabel.setIcon(new ImageIcon(ARGBUtils.scaleImage(image, scaleMutliplier)));
		frequencyDomainImageLabel.updateImage(complexImage, scaleMutliplier);
		recalculatedSpatialDomainImage
				.setIcon(new ImageIcon(ARGBUtils.scaleImage(spatialDomainImage, scaleMutliplier)));

		validate();
	}

	private int getImageWidth() {
		return image == null ? 200 : image.getWidth();
	}

	private int getImageHeight() {
		return image == null ? 200 : image.getHeight();
	}
}
