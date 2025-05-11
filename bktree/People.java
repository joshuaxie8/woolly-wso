package bktree;

import java.util.*; 
import java.io.*; 

public class People {

		public ArrayList<Person> peopleList = new ArrayList<>();


		public People() {
			injestData();
		}

		public void injestData() {
			try {
				String filePath = "bktree/names.csv";
				File file = new File(filePath);
				Scanner scanner = new Scanner(file);

				while (scanner.hasNextLine()) {

					String fullName = scanner.nextLine();

					Person newPerson = new Person(fullName);

					peopleList.add(newPerson);

				}
			} catch (FileNotFoundException e) {
				System.err.println("File not found: " + e.getMessage());
			}
		}


		public static void main (String[] args) {

			People allPeople = new People();

			
		}






}