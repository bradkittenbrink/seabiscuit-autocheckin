package com.coffeeandpower.cont;

public class Education
	{

		private String school;
		private int startDate;
		private int endDate;
		private String concentrations;
		private String degree;

		public Education (String school, int startDate, int endDate, String concentrations, String degree)
			{
				this.school = school;
				this.startDate = startDate;
				this.endDate = endDate;
				this.concentrations = concentrations;
				this.degree = degree;
			}

		public String getSchool ()
			{
				return school;
			}

		public void setSchool (String school)
			{
				this.school = school;
			}

		public int getStartDate ()
			{
				return startDate;
			}

		public void setStartDate (int startDate)
			{
				this.startDate = startDate;
			}

		public int getEndDate ()
			{
				return endDate;
			}

		public void setEndDate (int endDate)
			{
				this.endDate = endDate;
			}

		public String getConcentrations ()
			{
				return concentrations;
			}

		public void setConcentrations (String concentrations)
			{
				this.concentrations = concentrations;
			}

		public String getDegree ()
			{
				return degree;
			}

		public void setDegree (String degree)
			{
				this.degree = degree;
			}

	}
