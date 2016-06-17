/*This project was developed and created by ragoolaman.
 * Any and all code in these files cannot be claimed as the work of anyone but ragoolaman.*/

package genomeDownloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class GUI implements ActionListener{
	JPanel totalGUI, buttons;
	JLabel title;
	JButton update, download, search, back;
	public JPanel createContentPane() 
	{
		totalGUI = new JPanel();
		totalGUI.setLayout(null);
		
		title = new JLabel("Please select an option");
		title.setSize(1000,100);
		title.setLocation(380, 50);
		totalGUI.add(title);
		
		buttons = new JPanel();
		buttons.setSize(500,500);
		buttons.setLocation(250, 250);
		buttons.setLayout(null);
		totalGUI.add(buttons);
		
		update = new JButton("Update database");
		update.setSize(200,100);
		update.setLocation(100,0);
		update.addActionListener(this);
		update.setToolTipText("This updates the local copy of genbank");
		buttons.add(update);
		
		download = new JButton("Dowload from file");
		download.setSize(200,100);
		download.setLocation(100,100);
		download.addActionListener(this);
		download.setToolTipText("This downloads multiple genomes from a file");
		buttons.add(download);
		
		search = new JButton("Search for a Genome");
		search.setSize(200,100);
		search.setLocation(100,200);
		search.addActionListener(this);
		search.setToolTipText("This searches the genbank database for the genome you want");
		buttons.add(search);
		
		back = new JButton("Back");
		back.setSize(200,100);
		back.setLocation(100,300);
		back.addActionListener(this);
		back.setToolTipText("Go back a level");
		back.setVisible(false);
		back.setEnabled(false);
		buttons.add(back);
		return totalGUI;
	}
	public static void createAndShowGUI() 
	{
		JFrame frame = new JFrame();
		frame.setSize(1000,1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GUI gui = new GUI();
        frame.setContentPane(gui.createContentPane());
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == update) 
		{
			download.setVisible(false);
			download.setEnabled(false);
			search.setVisible(false);
			search.setEnabled(false);
			back.setVisible(true);
			back.setEnabled(true);
		} else
		if(e.getSource() == download) 
		{
			update.setVisible(false);
			update.setEnabled(false);
			search.setVisible(false);
			search.setEnabled(false);
			back.setVisible(true);
			back.setEnabled(true);
		} else
		if(e.getSource() == search) 
		{
			update.setVisible(false);
			update.setEnabled(false);
			download.setVisible(false);
			download.setEnabled(false);
			back.setVisible(true);
			back.setEnabled(true);
		} else
		if(e.getSource() == back) 
		{
			update.setVisible(true);
			update.setEnabled(true);
			download.setVisible(true);
			download.setEnabled(true);
			search.setVisible(true);
			search.setEnabled(true);
			back.setVisible(false);
			back.setEnabled(false);
		}
	}

}

