# 📱 Finanzify – Personal Finance Management App

## 📖 Overview

**Finanzify** is a mobile application developed to help users efficiently manage their personal finances. The app focuses on improving financial habits by providing tools to track income and expenses, set budgets, and analyze spending behavior.

This project was developed as part of a Bachelor's Thesis at the Universidad Politécnica de Madrid (UPM).

---

## 🎯 Features

* 💰 Track income and expenses
* 📊 Visualize financial data and spending patterns
* 📅 Manage recurring transactions
* 🎯 Create and manage budgets
* 🔔 Notifications and reminders
* 👤 User profile and authentication system

---
## 🧠 Methodology

The development of Finanzify followed a **User-Centered Design (UCD)** approach, ensuring that the application was designed around real user needs, behaviors, and expectations.

### 🔍 Requirements Definition

Based on a competitive analysis and user interviews, both **functional** and **interaction requirements** were defined:

#### Functional Requirements

* Income and expense tracking, including recurring transactions
* Categorization of transactions with customizable categories
* User account creation and authentication
* Budget creation (monthly and category-based)
* Notifications and alerts for financial activity and budget limits
* Statistical reports with customizable time ranges
* Security features such as two-factor authentication
* Data export functionality
* Multi-currency support for income registration

#### Interaction Requirements

* Fast and simple transaction input
* High level of customization (categories, preferences)
* Visually appealing and professional UI
* Automation of repetitive processes (e.g., recurring transactions)
* Mobile-first design (primary platform: smartphones)

---

### 🎨 Prototyping with Figma

Before implementation, a **high-fidelity interactive prototype** was developed using Figma.

Figma was chosen due to its flexibility in designing mobile interfaces and its ability to simulate realistic user interactions. Although it does not provide a direct framework for Android development, it allows exporting visual assets and serves as a solid foundation for later implementation in Android Studio.

The prototype included all main application flows:

* User registration and authentication
* Transaction creation (income and expenses)
* Budget management
* Statistics visualization
* Category management

---

### 🧪 Usability Testing

A total of **8 usability tests** were conducted with real users to evaluate the prototype.

Each test included:

* Introduction and explanation of the test
* Demographic questionnaire
* 8 specific tasks (e.g., registering a user, creating budgets, viewing statistics)
* SUS (System Usability Scale) questionnaire

#### Key Findings:

* Most tasks were completed successfully by all users
* Tasks related to **budgets, recurring transactions, and category management** showed the highest difficulty
* Some usability issues were caused by unclear navigation or labeling

#### Metrics Collected:

* Number of errors per task
* Number of actions vs. optimal actions
* User questions and qualitative feedback

---

### 📊 Usability Results

* High usability in basic tasks (registration, navigation, statistics)
* Greater difficulty in complex features (budgets and recurring transactions)
* Strong results in **learnability and perceived security**
* Mixed results in **system consistency**, indicating areas for improvement

The SUS questionnaire results showed:

* Positive perception of ease of use
* Very high learnability (users required little to no assistance)
* Strong sense of security among users
* Opportunities to improve consistency and navigation

---

### 🔧 Iterative Design Improvements

Based on usability testing, several key improvements were implemented in the prototype:

* Improved visibility and accessibility of key features (e.g., budgets, categories)
* Redesigned navigation to reduce user confusion
* Enhanced recurring transaction logic (clear duration instead of repetition ambiguity)
* Added back navigation to all flows to prevent user frustration
* Improved consistency and placement of UI elements
* Increased size and clarity of important icons (e.g., settings)

These changes significantly improved usability, reduced user errors, and aligned the system more closely with user mental models.

---

### 📱 Implementation

After validating the prototype, the application was implemented using:

* **Android Studio**
* **Java**

The transition from Figma to development included translating UI designs into Android layouts and integrating application logic, ensuring consistency with the validated prototype.

---

This iterative and user-centered process ensured that the final application is not only functional, but also intuitive, efficient, and aligned with user expectations.

---

## 🛠️ Technologies Used

* **Java**
* **Android Studio**
* **Firebase** (authentication & services)
* **Gradle**
* **Figma**

---

## 📄 Abstract

One of the biggest challenges for most people is the efficient management of their money and finances. Inefficient management can lead to bad habits, financial difficulties, and even debt.

This project presents a mobile application designed to help users manage their personal finances effectively. The application allows users to track income and expenses, maintain detailed financial control, and improve consumption habits through budgeting and statistical insights.

The development process followed a User-Centered Design methodology, including competitive analysis, user interviews, and usability testing. A high-fidelity prototype was created and tested before implementation.

The final application was developed using Android Studio and Java. This repository contains the implementation of the application described in the thesis.

---

## 📚 Thesis Reference

This project was done for my the Bachelor's Thesis:

🔗 https://oa.upm.es/82500/

Full document available here:
🔗 https://oa.upm.es/82500/1/TFG_IRENE_MARTINEZ_ALVAREZ.pdf

---

## 🚀 Getting Started

### Prerequisites

* Android Studio installed
* Android SDK configured

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/your-repo.git
   ```
2. Open the project in Android Studio
3. Sync Gradle
4. Run the app on an emulator or device

## 👩‍💻 Author

**Irene Martínez Álvarez**
Bachelor’s Degree in Computer Engineering – UPM

---

## 📜 License

This project is for academic purposes. Feel free to explore and learn from it.
