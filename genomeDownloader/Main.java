/*This project was developed and created by ragoolaman.
 * Any and all code in these files cannot be claimed as the work of anyone but ragoolaman.*/
package genomeDownloader;

import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	       SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                GUI.createAndShowGUI();
	            }
	        });
	}

}
