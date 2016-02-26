/**
 * 
 * Author: Ameya Murar
 * 
 * Date: 2/23/2016
 * 
 * Web Services Programming Assignment-1
 * 
 */
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.JSONException;
import org.json.JSONObject;

public class PA1 extends JFrame{
	private JTextField textField;									// A text field to hold the name of the city
	private JTextArea textArea1;									// A text area to display the list of events
	private JTextArea textArea2;									// A text area to display the list of hotels
	private JLabel label;											// A label which indicates what is required to be input into the text area
	private JButton button;											// A button to get information
	private JLabel map;												// A label which holds the map of the required city
	static int counter=0;	
	static int counterForEvents=0;
	static int counterForHotels=0;
	
	public PA1(){
		super("Information about a city!");
		setLayout(new FlowLayout());
		textField=new JTextField(20);
		textArea1=new JTextArea(15,30);
		textArea2=new JTextArea(15,30);
		label=new JLabel("City name: ");
		button=new JButton("Get Information!");
		add(label);
		add(textField);
		add(button);
		add(textArea1);
		add(textArea2);
	
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				String input=textField.getText();					// String input stores the value in the text field
				try {
					getRestaurantList(input);						// method call to the getRestaurantList() method
					getEventList(input);							// method call to the getEventList() method
					displayMap(input);								// method call to the displayMap() method
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/*
	 * Method to display the list of restaurants at the chosen area using the Zomato API
	 * 
	 */
	
	public void getRestaurantList(String input) throws IOException, JSONException{
		String key="54cc80d48d2e27abb24b84a849ec6546";
		input=input.replaceAll(" ", "%20");
		if(counterForHotels != 0){
			textArea1.setText(null);
		}
		URL url3=new URL("https://developers.zomato.com/api/v2.1/cities?apikey="+key+"&q="+input);
		BufferedReader in3 = new BufferedReader(new InputStreamReader(url3.openStream()));
		String inputLine3;
		JSONObject jsonObj3 = null;
		while ((inputLine3 = in3.readLine()) != null){
			jsonObj3=new JSONObject(inputLine3);
		}
		in3.close();
		
		Object weather3=jsonObj3.get("location_suggestions");
		String s3=weather3.toString();
		int main=s3.indexOf("id");
		int main2=s3.indexOf(":", main);
		int main1=s3.indexOf(",", main);
		String id=s3.substring(main2+1, main1);
		
		URL url4=new URL("https://developers.zomato.com/api/v2.1/search?apikey="+key+"&entity_id="+id+"&entity_type=city");
		BufferedReader in4 = new BufferedReader(new InputStreamReader(url4.openStream()));
		String inputLine4;
		JSONObject jsonObj4 = null;
		while ((inputLine4 = in4.readLine()) != null){
			jsonObj4=new JSONObject(inputLine4);
		}
		in4.close();
		
		Object food=jsonObj4.get("restaurants");
		String s4=food.toString();
		int count=0;
		int firstOccurrence=0;
		int secondOccurrence,thirdOccurrence;
		int firstOcc=0;
		int secondOcc,thirdOcc;
		String answer,answerAdd;
		firstOccurrence=s4.indexOf("name");
		secondOccurrence=s4.indexOf(":",firstOccurrence);
		thirdOccurrence=s4.indexOf(",",firstOccurrence);
		answer=s4.substring(secondOccurrence+1, thirdOccurrence);
		firstOcc=s4.indexOf("address");
		secondOcc=s4.indexOf(":",firstOcc);
		thirdOcc=s4.indexOf("locality",firstOcc);
		answerAdd=s4.substring(secondOcc+1, thirdOcc);
		textArea1.append("<----------------------LIST OF RESTAURANTS---------------------->");
		textArea1.append("\n");
		textArea1.append(answer);
		int ref=answerAdd.indexOf("latitude");
		textArea1.append("\n");
		textArea1.append(answerAdd.substring(0, ref-2));
		textArea1.append("\n");
		textArea1.append("\n");
		
		while(true){
			firstOccurrence=s4.indexOf("name",thirdOccurrence);
			secondOccurrence=s4.indexOf(":",firstOccurrence);
			thirdOccurrence=s4.indexOf(",",firstOccurrence);
			answer=s4.substring(secondOccurrence+1, thirdOccurrence);
			
			firstOcc=s4.indexOf("address",thirdOcc);
			secondOcc=s4.indexOf(":",firstOcc);
			thirdOcc=s4.indexOf("locality",firstOcc);
			answerAdd=s4.substring(secondOcc+1, thirdOcc);
			textArea1.append(answer);
			textArea1.append("\n");
			int ref1=answerAdd.indexOf("latitude");
			textArea1.append(answerAdd.substring(0, ref1-2));
			textArea1.append("\n");
			textArea1.append("\n");
			count++;
			if(count>3){
				break;
			}
		}
		counterForHotels++;
	}
	
	/*
	 * Method to display the events happening at the chosen area using the Eventful API
	 * 
	 */
	
	public void getEventList(String input) throws IOException, JSONException{
		String key="sgLzB3FFKsh2n7ss";
		input=input.replaceAll(" ", "%20");
		if(counterForEvents != 0){
			textArea2.setText(null);
		}
		URL url2 = new URL("http://api.eventful.com/json/events/search?app_key="+key+"&location="+input+"&date=This+Week");
	
		BufferedReader in2 = new BufferedReader(new InputStreamReader(url2.openStream()));
		String inputLine2;
		JSONObject jsonObj2 = null;
		while ((inputLine2 = in2.readLine()) != null){
			jsonObj2=new JSONObject(inputLine2);
		}
		in2.close();
		
		Object weather2=jsonObj2.get("events");
		String s2=weather2.toString();
		String time;
		int time2;
		time2=s2.indexOf("start_time");
		time=s2.substring(time2+12, time2+33);
		int count=0;
		int firstOccurrence=0;
		int secondOccurrence,thirdOccurrence;
		int firstOcc=0;
		int secondOcc,thirdOcc;
		String answer,answerAdd;
		firstOccurrence=s2.indexOf("title");
		secondOccurrence=s2.indexOf(":",firstOccurrence);
		thirdOccurrence=s2.indexOf(",",firstOccurrence);
		answer=s2.substring(secondOccurrence+1, thirdOccurrence);
		firstOcc=s2.indexOf("venue_address",thirdOccurrence);
		secondOcc=s2.indexOf(":",firstOcc);
		thirdOcc=s2.indexOf(",",firstOcc);
		answerAdd=s2.substring(secondOcc+1, thirdOcc);
		textArea2.append("<----------------------LIST OF EVENTS---------------------->");
		textArea2.append("\n");
		textArea2.append(answer);
		textArea2.append("\n");
		textArea2.append(time);
		textArea2.append("\n");
		textArea2.append(answerAdd);
		textArea2.append("\n");
		textArea2.append("\n");
		while(true){
			time2=s2.indexOf("start_time",thirdOccurrence);
			time=s2.substring(time2+12, time2+33);
			firstOccurrence=s2.indexOf("title",thirdOccurrence);
			secondOccurrence=s2.indexOf(":",firstOccurrence);
			thirdOccurrence=s2.indexOf(",",firstOccurrence);
			answer=s2.substring(secondOccurrence+1, thirdOccurrence);
			
			firstOcc=s2.indexOf("venue_address",thirdOcc);
			secondOcc=s2.indexOf(":",firstOcc);
			thirdOcc=s2.indexOf(",",firstOcc);
			answerAdd=s2.substring(secondOcc+1, thirdOcc);
			textArea2.append(answer);
			textArea2.append("\n");
			textArea2.append(time);
			textArea2.append("\n");
			textArea2.append(answerAdd);
			textArea2.append("\n");
			textArea2.append("\n");
			count++;
			if(count>3){
				break;
			}
		}
		counterForEvents++;
	}
	
	/*
	 * Method to display the map of the chosen area using Google Maps API
	 * 
	 */
		
	public void displayMap(String input) throws IOException{
		input=input.replaceAll(" ", "%20");
		if(counter!=0){
			remove(map);
		}
		String imageOfMap="map.jpg";
        URL urlMap=new URL("http://maps.google.com/maps/api/staticmap?center="+input+"&zoom=14&size=512x512&maptype=roadmap");
        InputStream is=urlMap.openStream();
        OutputStream os=new FileOutputStream(imageOfMap);
        
        byte[] b=new byte[2048];									
        int length;
        
        while((length=is.read(b)) != -1){
        	os.write(b, 0, length);									
        }
        
        is.close();													
        os.close();													
        
        map=new JLabel(new ImageIcon((new ImageIcon("map.jpg")).getImage().getScaledInstance(450, 300, java.awt.Image.SCALE_SMOOTH)));
        add(map);													
        counter++;
	}
}