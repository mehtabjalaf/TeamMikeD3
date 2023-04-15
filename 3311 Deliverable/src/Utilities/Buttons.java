package Utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

/**
 * 
 * @version 2.0 
 * Class responsible for the buttons used in the main window.
 * 
 */
public class Buttons {
	/**
	 * Creates a toggle button that changes views between the raw data table and descriptive statistics. 
	 * @return The button created
	 */
	static JToggleButton createToggleButton() {
		JToggleButton toggleButton;
		toggleButton = new JToggleButton("Switch View");
		toggleButton.setFont(new Font("San Francisco", Font.PLAIN, 16));
		toggleButton.setForeground(Color.WHITE);
		toggleButton.setBackground(new Color(0x4CD964));
		toggleButton.setOpaque(true);
		toggleButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		toggleButton.setPreferredSize(new Dimension(130, 40));

		JPanel togglePanel = new JPanel();
		togglePanel.add(toggleButton);
	
		return toggleButton;

	}
	/**
	 * Creates a button to open up the Forecasting menu. 
	 * @return The button created
	 */
	static JButton createForecastingButton() {
		JButton forecastingButton = new JButton("Forecasting");
		forecastingButton.addActionListener(new ActionListener() {

			//Will open a new window to perform stat. test
			public void actionPerformed(ActionEvent e) {
				try {
					Forecasting frame = new Forecasting();
					frame.pack();  
					frame.setSize(1500, 900);  
					frame.setVisible(true);
					frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
				} 
				catch (Exception e1) {

					e1.printStackTrace();
				}
			}
		});
		
		
		return forecastingButton;
	}
	
	/**
	 * Creates a toggle button that open statistical test window. 
	 * @return The button created
	 */
	static JButton createStatTestButton() {
		JButton statisticalTestButton = new JButton ("Statistical Test");
		
		statisticalTestButton.addActionListener(new ActionListener() {

			//Will open a new window to perform stat. test
			public void actionPerformed(ActionEvent e) {
				try {
					NHIPComparison n = new NHIPComparison();
					n.pack();  
					n.setSize(1550, 400);  
					n.setVisible(true);
					n.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
				} 
				catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		return statisticalTestButton;
		
	}
}
