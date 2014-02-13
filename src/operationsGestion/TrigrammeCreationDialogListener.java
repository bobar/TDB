package operationsGestion;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class TrigrammeCreationDialogListener implements KeyListener, ActionListener {

	private TrigrammeCreationDialog parent;

	public TrigrammeCreationDialogListener(TrigrammeCreationDialog dialog) {
		super();
		this.parent = dialog;
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
			parent.validation = true;
			parent.dispose();
		} else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
			parent.validation = false;
			parent.dispose();
		}

	}

	public void keyReleased(KeyEvent arg0) {
		if (arg0.getSource().equals(parent.champTrigramme)) {
			parent.champTrigramme.setText(parent.champTrigramme.getText().toUpperCase());
			if (parent.champTrigramme.getText().length() < 3) {
				parent.validation = false;
				parent.champTrigramme.setBackground(null);
			} else {
				try {
					Statement stmt = parent.parent.connexion.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT id FROM accounts WHERE trigramme='"
							+ parent.champTrigramme.getText() + "'");
					if (!rs.first()) {
						parent.validation = true;
						parent.champTrigramme.setBackground(Color.GREEN);
					} else {
						parent.validation = false;
						parent.champTrigramme.setBackground(Color.RED);
					}

				} catch (Exception e) {
					parent.parent.afficherErreur(e);
				}
			}
			parent.champTrigramme.repaint();
		}
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(parent.okButton)) {
			parent.validation = true;
			parent.dispose();
		} else if (e.getSource().equals(parent.cancelButton)) {
			parent.validation = false;
			parent.dispose();
		} else if (e.getSource().equals(parent.photoButton)) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileFilter() {

				@Override
				public boolean accept(File arg0) {
					return (arg0.isDirectory() || arg0.getName().contains(".gif")
							|| arg0.getName().contains(".jpg") || arg0.getName().contains(".jpeg") || arg0
							.getName().contains(".png"));
				}

				@Override
				public String getDescription() {
					return null;
				}

			});
			int returnVal = chooser.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				parent.champPhoto.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		}
	}

}