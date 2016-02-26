/**
 * NBClassifier.java
 * 
 * Authors: Ameya Murar
 * 	        Vinay Kumbhar
 * 
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.swing.*;

public class NBClassifier extends JFrame {
	static String[] trainPos=new String[12500];
	static String[] trainNeg=new String[12500];
	static String[] train=new String[25000];
	private JLabel label;
	private JLabel result;
	private JButton button;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JPanel panel2;
	String[] trainingDocs;
	int[] trainingLabels;
	int numClasses;
	int[] classCounts;
	String[] classStrings;
	int[] classTokenCounts;
	HashMap<String,Double>[] condProb;
	HashSet<String> vocabulary;
	static ArrayList<String> poslist = new ArrayList<String>();
	static ArrayList<String> neglist = new ArrayList<String>();
	private		JTabbedPane tabbedPane;
	private DefaultListModel model_pos;
	private JList list_pos;
	private DefaultListModel model_neg;
	private JList list_neg;
	
	public NBClassifier() throws IOException{
		super("Opinion Mining on Movie Reviews");
		Scanner s = new Scanner(new File("poswords.txt"));
		while (s.hasNext()){
			poslist.add(s.next());
		}
		Scanner s1=new Scanner(new File("negwords.txt"));
		while (s1.hasNext()){
			neglist.add(s1.next());
		}
		s.close();
		s1.close();
		final File folder1=new File("train\\pos");
		listFilesForFolder(folder1,1);
		final File folder2=new File("train\\neg");
		listFilesForFolder(folder2,2);
		int[] trainLables=new int[25000];
		for(int i=0;i<12500;i++){
			trainLables[i]=0;
		}
		for(int j=12500;j<25000;j++){
			trainLables[j]=1;
		}
		int numClass=2;
		NBClassifier nb = new NBClassifier(train, trainLables, numClass);
		
		
		/* Creating a GUI */
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );
		
		tabbedPane = new JTabbedPane();
		//Review Tab
		panel = new JPanel();
		//Data Tab
		panel2 = new JPanel();
		JLabel label2 = new JLabel("Top positive words                 ");
		JLabel label3 = new JLabel("                 Top negative words");
		JPanel labelpanel = new JPanel();
		labelpanel.setLayout(new BoxLayout(labelpanel, BoxLayout.X_AXIS));
		
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		labelpanel.add(label2);
		labelpanel.add(label3);
		
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setFont(new Font("Verdana", Font.PLAIN, 14));
		scrollPane = new JScrollPane(editorPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		
		label = new JLabel("Enter movie review");
		label.setBorder(BorderFactory.createEmptyBorder(10, 150, 10, 40));
		result = new JLabel();
		result.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
		result.setFont(new Font("Verdana", Font.BOLD, 14));
		button = new JButton("Predict Opinion");
		
		panel.add(label);
		panel.add(scrollPane);
		panel.add(button);
		panel.add(result);
		
		tabbedPane.addTab( "Review section", panel );
		tabbedPane.addTab( "Data Visualization", main );
		topPanel.add( tabbedPane, BorderLayout.CENTER );
	
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				String input=editorPane.getText();
				double feedback=nb.classify(input);
				JOptionPane.showMessageDialog(null, "Result calculated successfully!");
				if(feedback == 0.0){
					result.setText("The review is positive");
				}else if(feedback == 1.0){
					result.setText("The review is negative");
				}else if(feedback == 2.0){
					result.setText("The review is neutral");
				}
			}
		});
		
		
		//Listing words
		model_pos = new DefaultListModel();
	    list_pos = new JList(model_pos);
	    model_neg = new DefaultListModel();
	    list_neg = new JList(model_neg);
	    
	    Collections.reverse(poslist);
	    Collections.reverse(neglist);
	    
	    for(String word: poslist)
	    {
	    	model_pos.addElement(word);
	    }
	    for(String word: neglist)
	    {
	    	model_neg.addElement(word);
	    }
	
	    JScrollPane panelist1 = new JScrollPane(list_pos);
	    JScrollPane panelist2 = new JScrollPane(list_neg);
	    panelist1.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
	    panelist2.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
	    
	    panel2.add(panelist1);
	    panel2.add(panelist2);
	    
	    main.add(labelpanel);
	    main.add(panel2);
	}
	
	public NBClassifier(String[] docs, int[] labels, int numC){
		trainingDocs = docs;
		trainingLabels = labels;
		numClasses = numC;
		classCounts = new int[numClasses];
		classStrings = new String[numClasses];
		classTokenCounts = new int[numClasses];
		condProb = new HashMap[numClasses];
		vocabulary = new HashSet<String>();
		for(int i=0;i<numClasses;i++){
			classStrings[i] = "";
			condProb[i] = new HashMap<String,Double>();
		}
		for(int i=0;i<trainingLabels.length;i++){
			classCounts[trainingLabels[i]]++;
			classStrings[trainingLabels[i]] += (trainingDocs[i] + " ");
		}
		for(int i=0;i<numClasses;i++){
			String[] tokens = classStrings[i].split(" ");
			classTokenCounts[i] = tokens.length;
			for(String token:tokens){
				vocabulary.add(token);
				if(condProb[i].containsKey(token)){
					double count = condProb[i].get(token);
					condProb[i].put(token, count+1);
				}
				else
					condProb[i].put(token, 1.0);
			}
		}
		
		for(int i=0;i<numClasses;i++){
			Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
			int vSize = vocabulary.size();
			while(iterator.hasNext()){
				Map.Entry<String, Double> entry = iterator.next();
				String token = entry.getKey();
				Double count = entry.getValue();
				count = (count+1)/(classTokenCounts[i]+vSize);
				condProb[i].put(token, count);
			}
		}
	}
	
	public static void listFilesForFolder(final File folder,int count) throws IOException {
		if(count==1){
			String file;
			int a=0;
		    for (final File fileEntry : folder.listFiles()) {
		    	file=readFile("train\\pos\\"+fileEntry.getName());
		    	trainPos[a++]=file;
		    }
		}else if(count==2){
			String file;
			int b=0;
		    for (final File fileEntry : folder.listFiles()) {
		    	file=readFile("train\\neg\\"+fileEntry.getName());
		    	trainNeg[b++]=file;
		    }
		    train=concat(trainPos,trainNeg);
		}
	}
	
	public static String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	
	public double classify(String doc){
		double label = 0.0;
		int vSize = vocabulary.size();
		double[] score = new double[numClasses];
		for(int i=0;i<score.length;i++){
			score[i] = Math.log(classCounts[i]*1.0/trainingDocs.length);
		}
		String[] tokens = doc.split(" ");
		for(int i=0;i<numClasses;i++){
			for(String token: tokens){
				if((poslist.contains(token) && i==0) || (neglist.contains(token) && i==1)){
					if(condProb[i].containsKey(token)){
						score[i] += Math.log(condProb[i].get(token)+10);
					}else{
						score[i] += Math.log(1.0/(classTokenCounts[i]+vSize));
					}
				}
				else{
					if(condProb[i].containsKey(token)){
						score[i] += Math.log(condProb[i].get(token));
					}else{
						score[i] += Math.log(1.0/(classTokenCounts[i]+vSize));
					}
				}
			}
		}

		if(Math.abs(score[0]-score[1]) < 8.0){
			return 2.0;
		}
		
		double maxScore = score[0];
		for(int i=0;i<score.length;i++){
			if(score[i]>maxScore){
				label = i;
			}
		}
		return label;
	}
	
	public static String[] concat(String[] trainPos, String[] trainNeg) {
		int lengthOfFirstArray = trainPos.length;
		int lengthOfSecondArray = trainNeg.length;
		int totalLength = lengthOfFirstArray+lengthOfSecondArray;
		String[] totalLengthOfTwoArrays= new String[totalLength];
		System.arraycopy(trainPos, 0, totalLengthOfTwoArrays, 0, lengthOfFirstArray);
		System.arraycopy(trainNeg, 0, totalLengthOfTwoArrays, lengthOfFirstArray, lengthOfSecondArray);
		return totalLengthOfTwoArrays;
	}
}
