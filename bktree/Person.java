package bktree;

import java.util.*; 
import java.io.*; 

public class Person {



		private String fullName;
		private String firstName;
		private String lastName;
		private String middleName;
		private int classYear;

		public Person(String name) {
			this.fullName = name;


			initializeFields();





		}


	private void initializeFields() {
		String[] splitFullName = fullName.split(" ");

		// case for less than 2? No first or last name

		if (splitFullName.length == 2) { // first and last name
			firstName = splitFullName[0];
			lastName = splitFullName[1];
		}

		if (splitFullName.length == 3 && (splitFullName[1].contains(".") || splitFullName[1].length() == 1)) { //case for traditional middle name
			firstName = splitFullName[0];
			middleName = splitFullName[1];
			lastName = splitFullName[2];
		}


	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
		//return "";
	}

	public String getMiddleName() {
		return middleName;
		//return "";
	}

	public String getFullName() {
		return fullName;
	}


	public static void main(String[] args) {
		Person john = new Person("John Smith");


	}


}