CREATE TABLE IF NOT EXISTS courses (
	code	VARCHAR(30) NOT NULL  ,
	name	VARCHAR(60) NOT NULL,
	PRIMARY KEY(code)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS durations (
	durationName	VARCHAR(40) NOT NULL,
	days	INTEGER NOT NULL,
	PRIMARY KEY(durationName)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS courseDuration (
	courseID	VARCHAR(30) NOT NULL,
	durationName	VARCHAR(40) NOT NULL,
	dateRegistered	DATE,
	fees	DOUBLE NOT NULL,
	FOREIGN KEY (courseID) REFERENCES courses(code) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (durationName) REFERENCES durations(durationName) ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY (courseID, durationName)
	
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS students (
	studentID	VARCHAR(30) NOT NULL,
	firstname	VARCHAR(50) NOT NULL,
	lastname	VARCHAR(50) NOT NULL,
	postalAddress	VARCHAR(200),
	phone	VARCHAR(20) NOT NULL,
	email	VARCHAR(70),
	dateRegistered	DATE NOT NULL,
	courseID	VARCHAR(30) NOT NULL,
	durationName	VARCHAR(40) NOT NULL,
	PRIMARY KEY(studentID),
	FOREIGN KEY (courseID) REFERENCES courseDuration(courseID) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (durationName) REFERENCES courseDuration(durationName) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Expenses (
	name	VARCHAR(40) NOT NULL  ,
	date	DATE,
	PRIMARY KEY(name)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payments (
	id	INTEGER AUTO_INCREMENT PRIMARY KEY,
	expense	VARCHAR(40),
	dateRegistered	DATE NOT NULL,
	amount	NUMERIC(20) NOT NULL,
	FOREIGN KEY(expense) REFERENCES Expenses(name) ON DELETE SET NULL ON UPDATE CASCADE
)ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS receipts (
	receiptNo	VARCHAR(15) NOT NULL  ,
	studentID	VARCHAR(30),
	date	DATE NOT NULL,
	amount	NUMERIC(30) NOT NULL,
	modeOfPayment	VARCHAR(30),
	PRIMARY KEY(receiptNo),
	FOREIGN KEY(studentID) REFERENCES students(studentID) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
	firstname	VARCHAR(55) NOT NULL,
	lastname	VARCHAR(55) NOT NULL,
	phone	VARCHAR(20) NOT NULL,
	email	VARCHAR(100) NOT NULL,
	dateRegistered	DATE,
	password	VARCHAR(40) NOT NULL,
	typeOfUser	VARCHAR(30) NOT NULL,
	image	BLOB 
)ENGINE=InnoDB;
