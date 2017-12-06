package com.example.databinding;

public class User {
		public String firstName;
		public String lastName;

		public User(String firstName, String lastName) {
				this.firstName = firstName;
				this.lastName = lastName;
		}

		public String getFirstName() {
				return firstName;
		}

		public String getLastName() {
				return lastName;
		}

		public void setFirstName(String firstName) {
				this.firstName = firstName;
		}

		public void setLastName(String lastName) {
				this.lastName = lastName;
		}

		@Override public String toString() {
				return "" + firstName + " " + lastName;
		}
}
