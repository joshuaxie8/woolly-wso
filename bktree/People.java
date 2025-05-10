package bktree;

import java.util.*; 
import java.io.*; 

public class People {

		public ArrayList<Person> peopleList = new ArrayList<>();
		public int injestErrCount = 0;



		public People() {
			System.out.println("people method! here is where the data is going to be injested and transformed into Persons!");

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
			System.out.println("running!");

			People allPeople = new People();

			for (int i = 0; i < allPeople.peopleList.size(); i++) {
				if (allPeople.peopleList.get(i).getFirstName() == null) {
					allPeople.injestErrCount += 1;
					System.out.println(allPeople.peopleList.get(i).getFullName());
				}
			}
			System.out.println(allPeople.injestErrCount);
		}






}