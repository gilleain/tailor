package tailor.app;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog  {
	
	private JProgressBar progressBar;
	
	public ProgressDialog(int n) {
		this.setUndecorated(true);
		this.setLayout(new BorderLayout());
		
		// TODO : needs SwingWorker...
		this.progressBar = new JProgressBar(0, n);
		this.progressBar.setBorderPainted(true);
		
		this.add(this.progressBar, BorderLayout.CENTER);
		
		this.pack();
		this.setLocation(300, 200);
	}
	
	public JProgressBar getProgressBar() {
		return this.progressBar;
	}
	
	public static void main(String[] args) {
		ProgressDialog d = new ProgressDialog(46);
		JProgressBar b = d.getProgressBar();
		for (int i = 0; i < 46; i++) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ie) {
				
			}
			b.setValue(i);
		}
	}
}
