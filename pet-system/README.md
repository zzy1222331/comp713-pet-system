# COMP713 Assessment 2 - Pet Management System (Option A)

## Overview
This is a distributed web/API application built with Java, Jakarta EE, and Payara Server. It allows users to manage and search for pets, demonstrating client-server architecture, RESTful API design, and JDBC connection pooling.

## Prerequisites
* **JDK 21** (or higher)
* **Apache Maven 3.9+**
* **Payara Server 6** (Community Edition)
* **MySQL Database** (AWS RDS is used for this project)
* **MySQL Connector/J 9.x** (Must be placed in Payara's `domain1/lib` folder)

## Setup Instructions
1. **Database Setup:** 
   Execute the SQL scripts in the `database/init.sql` file (if applicable) or manually create the `owners` and `pets` tables in your MySQL instance.
2. **Payara Configuration:**
   * Start Payara: `asadmin start-domain`
   * Log into the admin console: `http://localhost:4848`
   * Create a JDBC Connection Pool named `MySQLAnimalPool` pointing to your MySQL database.
   * Create a JDBC Resource with JNDI name `jdbc/MySQLAnimalPool` mapping to the pool.

## Build and Run Instructions
1. Clone this repository to your local machine.
2. Open a terminal in the project root directory.
3. Build the project:
   ```bash
   mvn clean package