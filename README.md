# Finanzify – Personal Finance Management App

## Overview

**Finanzify** is a mobile application developed to help users efficiently manage their personal finances. The app focuses on improving financial habits by providing tools to track income and expenses, set budgets, and analyze spending behavior.

This project was developed as part of a Bachelor's Thesis at the Universidad Politécnica de Madrid (UPM).

---

## Features

- Track income and expenses  
- Visualize financial data and spending patterns  
- Manage recurring transactions  
- Create and manage budgets  
- Notifications and reminders  
- User profile and authentication system  

---

## Methodology

The development of Finanzify followed a **User-Centered Design (UCD)** approach, ensuring that the application was designed around real user needs, behaviors, and expectations.

### Requirements Definition

Based on a competitive analysis and user interviews, both **functional** and **interaction requirements** were defined.

#### Functional Requirements

- Income and expense tracking, including recurring transactions  
- Categorization of transactions with customizable categories  
- User account creation and authentication  
- Budget creation (monthly and category-based)  
- Notifications and alerts for financial activity and budget limits  
- Statistical reports with customizable time ranges  
- Security features such as two-factor authentication  
- Data export functionality  
- Multi-currency support for income registration  

#### Interaction Requirements

- Fast and simple transaction input  
- High level of customization (categories and preferences)  
- Visually appealing and professional user interface  
- Automation of repetitive processes (e.g., recurring transactions)  
- Mobile-first design (primary platform: smartphones)  

---

### Prototyping with Figma

Before implementation, a high-fidelity interactive prototype was developed using Figma.

Figma was selected for its flexibility in designing mobile interfaces and its ability to simulate realistic user interactions. Although it does not provide a direct framework for Android development, it allows exporting visual assets and serves as a solid foundation for later implementation in Android Studio.

The prototype included all main application flows:

- User registration and authentication  
- Transaction creation (income and expenses)  
- Budget management  
- Statistics visualization  
- Category management  

---

### Usability Testing

A total of 8 usability tests were conducted with real users to evaluate the prototype.

Each test included:

- Introduction and explanation of the test  
- Demographic questionnaire  
- Eight specific tasks (e.g., user registration, budget creation, statistics visualization)  
- SUS (System Usability Scale) questionnaire  

#### Key Findings

- Most tasks were successfully completed by all users  
- Tasks related to budgets, recurring transactions, and category management presented the highest level of difficulty  
- Some usability issues were caused by unclear navigation or labeling  

#### Metrics Collected

- Number of errors per task  
- Number of actions compared to optimal actions  
- User questions and qualitative feedback  

---

### Usability Results

- High usability in basic tasks (registration, navigation, statistics)  
- Greater difficulty in more complex features (budgets and recurring transactions)  
- Strong performance in learnability and perceived security  
- Areas for improvement identified in system consistency  

The SUS questionnaire results indicated:

- Positive perception of ease of use  
- Very high learnability, with minimal user assistance required  
- Strong sense of security among users  
- Opportunities to improve consistency and navigation  

---

### Iterative Design Improvements

Based on usability testing, several key improvements were implemented in the prototype:

- Improved visibility and accessibility of key features (e.g., budgets and categories)  
- Redesigned navigation to reduce user confusion  
- Enhanced recurring transaction logic by clarifying duration instead of repetition ambiguity  
- Added back navigation to all flows to improve usability  
- Improved consistency and placement of user interface elements  
- Increased the size and clarity of key icons (e.g., settings)  

These changes significantly improved usability, reduced user errors, and aligned the system more closely with user expectations and mental models.

---

### Implementation

After validating the prototype, the application was implemented using:

- Android Studio  
- Java  

The transition from Figma to development involved translating UI designs into Android layouts and integrating application logic, ensuring consistency with the validated prototype.

---

This iterative and user-centered process ensured that the final application is not only functional, but also intuitive, efficient, and aligned with user expectations.

---

## Technologies Used

- Java  
- Android Studio  
- Firebase (authentication and backend services)  
- Gradle  
- Figma  

---

## Abstract

One of the main challenges for many individuals is the effective management of personal finances. Poor financial management can lead to inadequate habits, financial instability, and debt.

This project presents a mobile application designed to support users in managing their finances effectively. The application enables users to track income and expenses, maintain detailed financial control, and improve spending habits through budgeting and data analysis.

The development process followed a User-Centered Design methodology, incorporating competitive analysis, user interviews, and usability testing. A high-fidelity prototype was designed and validated prior to implementation.

The final application was developed using Android Studio and Java. This repository contains the implementation of the application described in the associated thesis.

---

## Thesis Reference

This project was developed as part of a Bachelor's Thesis:

https://oa.upm.es/82500/

Full document available at:

https://oa.upm.es/82500/1/TFG_IRENE_MARTINEZ_ALVAREZ.pdf

---

## Getting Started

### Prerequisites

- Android Studio installed  
- Android SDK configured  

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/your-repo.git
