package networkProject;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UI {
	static boolean running = true;
	public static void UIGenerator()
	{
		TestProxyServer proxy = new TestProxyServer();
		Thread thread = new Thread(proxy);
		
		
		JFrame frame = new JFrame("My First GUI");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(300,300);
	    //JButton button = new JButton("Press");
	    JMenuBar mb = new JMenuBar();
        JMenu FILE = new JMenu("FILE");
        JMenuItem HELP = new JMenuItem("Help");
        mb.add(FILE);
        mb.add(HELP);
        JMenuItem start = new JMenuItem("Start");
        JMenuItem stop = new JMenuItem("Stop");
        JMenuItem report = new JMenuItem("Report");
        JMenuItem addHostToFilter = new JMenuItem("Add Host To Filter");
        JMenuItem displayFilteredHosts = new JMenuItem("Display current filtered hosts");
        JMenuItem exit = new JMenuItem("Exit");
        JTextField myOutput = new JTextField(30);
        frame.add(myOutput);
        myOutput.setText("");
        start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				myOutput.setText("Proxy Started");
				if(!running)
				{
					thread.resume();
				}
				else
				{
					thread.start();
				}
				
			}
        });
        HELP.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				myOutput.setText("Mehmet Arif Tasli");
			}
        });
        stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				myOutput.setText("Stopped Proxy");
				running = false;
				thread.suspend();
				
			}
        });
        report.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame2 = new JFrame("AddHost");
				frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame2.setSize(300,300);
				JPanel panel = new JPanel();
				JLabel label = new JLabel("Enter ip: ");
				JTextField tf = new JTextField(20);
				JButton go = new JButton("get report");
				panel.add(label);
				panel.add(tf);
				panel.add(go);
				frame2.getContentPane().add(BorderLayout.CENTER,panel);
				frame2.setVisible(true);
				go.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						PrintWriter writer = null;
						try {
							writer = new PrintWriter(new File("C:\\Users\\arif1\\eclipse-workspace\\networkProject\\report.txt"));  
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						writer.println(proxy.report.get("/"+tf.getText()));
						writer.flush();
					}
					
				});
		
			}
        });
        addHostToFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("Add host to filter");
				JFrame frame2 = new JFrame("AddHost");
				frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame2.setSize(300,300);
				JPanel panel = new JPanel();
				JLabel label = new JLabel("Enter text: ");
				JTextField tf = new JTextField(20);
				JButton go = new JButton("Add domain");
				go.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						proxy.avoidedList.add(tf.getText());
						
					}
					
				});
				panel.add(label);
				panel.add(tf);
				panel.add(go);
				frame2.getContentPane().add(BorderLayout.CENTER,panel);
				frame2.setVisible(true);
			}
        });
        displayFilteredHosts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				myOutput.setText(proxy.avoidedList.toString());
				
			}
        });
        exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Exit");
				System.exit(0);
				
			}
        });
        FILE.add(start);
        FILE.add(stop);
        FILE.add(report);
        FILE.add(addHostToFilter);
        FILE.add(displayFilteredHosts);
        FILE.add(exit);
        




        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.setVisible(true);
        
        
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UI ui = new UI();
		ui.UIGenerator();
	}
}
