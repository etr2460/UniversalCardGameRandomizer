package randomizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

public class CardGameRandomizerMain extends JFrame {

	JPanel starterPanel;
	final JFileChooser fc;
	
	public static RandomizerUI main;
	public static void main(String[] args) {
		try {
			// Set the Look and Feel of the application to the operating
			// system's look and feel.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e) {
		}
		catch (InstantiationException e) {
		}
		catch (IllegalAccessException e) {
		}
		catch (UnsupportedLookAndFeelException e) {
		}

		CardGameRandomizerMain main = new CardGameRandomizerMain("CG randomizer");
		main.pack();
		main.setLocationRelativeTo(null);
		main.setVisible(true);
		
	}

	public CardGameRandomizerMain(String title){
		super(title);
		
		fc = new JFileChooser();
		fc.setDialogTitle("Choose Rom");
		fc.setApproveButtonText("Choose");
		fc.setFileFilter(new FileFilter() {

			   public String getDescription() {
			       return "Gameboy rom (*.gbc)";
			   }

			   public boolean accept(File f) {
			       if (f.isDirectory()) {
			           return true;
			       } else {
			           String filename = f.getName().toLowerCase();
			           return filename.endsWith(".gbc") || filename.endsWith(".gb") ;
			       }
			   }
			});
		
		starterPanel = new JPanel();
		starterPanel.setPreferredSize(new Dimension(200,50));
		starterPanel.add(new JLabel("What rom would you like to load?"));		
		JButton loadRom = new JButton("Load Rom");
		loadRom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				loadRom();
			}
		});
		starterPanel.add(loadRom, BorderLayout.SOUTH);
		
		this.add(starterPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);		
	}
	
	
	
	
	public void loadRom(){

		fc.showOpenDialog(this);

		try{
			
			
			File game = fc.getSelectedFile();
			if(game.getPath().endsWith(".gbc") || game.getPath().endsWith(".gb"))
				parseGB(game);
			
			// either way, reset location.
			this.setLocationRelativeTo(null);
			
		}catch(NullPointerException e){
			JOptionPane.showMessageDialog(null, "Error: no rom found.");
		}

	}
	
	// right now parses by title name, todo: search for better methods
	public void parseGB(File file){
		int gbHeadStart = 0x0134;
		int gbHeadSize = 0x143 - gbHeadStart;
		FileInputStream gbgame;
		String headText = "";
		try {
			byte[] headName = new byte[gbHeadSize];
			gbgame = new FileInputStream(file);
			gbgame.skip(gbHeadStart);
			gbgame.read(headName);
			headText = new String(headName,Charset.forName("US-ASCII")); // not sure this works, found on stack overflow
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		RandomizerUI replace = null;
		if(headText.equalsIgnoreCase("POKECARD\0\0\0AXQE")){
			replace = new PTCG1_UI();
		}
		
		if(replace != null){
			replace.setFile(file);
			replace.setFileChooser(fc);
			this.remove(starterPanel);
			this.add(replace);
			this.pack();
		}
		else{
			JOptionPane.showMessageDialog(null, "Error: not a valid rom.");
		}
		
		
		
	}

}