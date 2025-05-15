package bktree;

import java.util.*; 
import java.io.*; 

public class Person {



		private String fullName = "";
		private String firstName = "";
		private String lastName = "";
		private String middleName = "";

		private String unix = "";
		private String type = "";
		private int classYear = -1;
		private String homeTown = "";
		private String homeState = "";
		private String homeCountry = "";

 // name, unix, type, homeTown, homeState, homeCountry


		public Person(String name, String unix, String type, int classYear, String homeTown, String homeState, String homeCountry) {
			this.fullName = name;
			this.unix = unix;
			this.type = type;
			this.homeTown = homeTown;
			this.homeState = homeState;
			this.homeCountry = homeCountry;
			this.classYear = classYear;


			initializeFields();


		}


	private void initializeFields() {
		String[] splitFullName = fullName.split(" ");

		// case for less than 2? No first or last name

		if (splitFullName.length == 2) { // first and last name
			firstName = splitFullName[0];
			lastName = splitFullName[1];
			return;
		}

		if (splitFullName.length == 3 && (splitFullName[1].contains(".") || splitFullName[1].length() == 1)) { //case for traditional middle name
			firstName = splitFullName[0];
			middleName = splitFullName[1];
			lastName = splitFullName[2];
			return;
		}

		if (splitFullName[0].length() <= 2 && splitFullName.length > 2) { // special case where people have 1-2 charecter first names (ex. M, J., etc.)
			firstName = splitFullName[0] + " " + splitFullName[1];
			lastName = splitFullName [2];
			return;
		}

		// find with .

		for (int i = 0; i < splitFullName.length; i++) {
			// check if 2 chars or less or if there is a period
			if (splitFullName[i].contains(".") || splitFullName[i].length() <= 2) {

				if (i != 0) {
					middleName = splitFullName[i];
				} else {
					firstName = splitFullName[i];
				}
				

				for (int j = 0; j < i; j++) {
					if (!firstName.equals("")) {
						firstName += (" " + splitFullName[j]);
					} else {
						firstName += splitFullName[j];
					}
					
				}

				for (int k = i + 1; k < splitFullName.length; k++) { // then if found check the rest for Jr., III, II, etc. and get rid of that for the last name, put everything else afterwords 
					if (!(splitFullName[k].equals("Jr.") || splitFullName[k].equals("I") || splitFullName[k].equals("II") || splitFullName[k].equals("III") || splitFullName[k].equals("IV") || splitFullName[k].equals("V"))) {
						lastName += splitFullName[k];
					}
				}

				return;

			}
				
		}

		if (splitFullName.length == 3) { // names with full middle names usually
			firstName = splitFullName[0];
			middleName = splitFullName[1];
			lastName = splitFullName[2];
			return;
		}

		// remainder (4+ full names)

		firstName = splitFullName[0];
		lastName = splitFullName[splitFullName.length - 1];


		for (int x = 1; x < splitFullName.length - 1; x++) {
			if (middleName == "") {
				middleName += splitFullName[x];
			} else {
				middleName += (" " + splitFullName[x]);
			}
		}

		// last name 2 word first words: De, Van, El, Le, Van,

	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getFullName() {
		return fullName;
	}


	public String getUnix() {
		return unix;
	}

	public String getType() {
		return type;
	}

	public int getClassYear() {
		return classYear;
	}

	public String getHomeTown() {
		return homeTown;
	}

	public String getHomeState() {
		return homeState;
	}

	public String getHomeCountry() {
		return homeCountry;
	}

	public String toString() {
		return "" + firstName + " " + middleName + " " + lastName + " | " + unix + " | " + type + " | " + classYear + " | " + homeTown + ", " + homeState + ", " + homeCountry;
	}


	public static void main(String[] args) {
		//Person john = new Person("John Smith");


	}


}