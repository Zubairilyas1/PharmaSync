# PharmaSync - Pharmacy Management System

PharmaSync is a comprehensive JavaFX-based pharmacy management system designed to streamline pharmacy operations. It provides features for inventory management, sales processing, prescription verification, customer database management, and detailed reporting.

## Project Overview

PharmaSync is built with a modular architecture separating frontend UI components from backend business logic services. The system includes user authentication, role-based access control, and comprehensive audit logging.

### Key Features

- **Dashboard**: Overview of pharmacy operations and key metrics
- **Inventory Management**: Add, edit, and track medicine inventory
- **Sales Terminal**: Point-of-sale system for transactions
- **Prescription Verification**: Check and validate prescriptions
- **Customer Database**: Maintain customer records
- **Procurement**: Manage supplier orders and stock replenishment
- **Returns Management**: Handle medicine returns and refunds
- **Reports**: Generate sales and inventory reports
- **Audit Logs**: Track all system activities for compliance
- **User Authentication**: Secure login with password recovery

## Technology Stack

- **Language**: Java
- **GUI Framework**: JavaFX
- **Database**: MongoDB
- **Architecture**: MVC with service-oriented backend

## Folder Structure

```
PharmaSync/
├── src/
│   ├── App.java                 # Main application entry point
│   ├── frontend/                # UI components and pages
│   │   ├── pages/              # Application screens
│   │   └── ui/                 # UI utilities and themes
│   └── backend/                # Business logic and services
│       └── services/           # Core business logic
├── bin/                        # Compiled output files
├── lib/                        # Project dependencies (JavaFX, MongoDB drivers)
└── README.md                   # Project documentation
```

## Getting Started

1. Ensure Java 11+ and JavaFX SDK are installed
2. Clone or download the project
3. Open the project in VS Code or your preferred IDE
4. Install required dependencies (see `lib` folder)
5. Compile and run the application (see `RUNNING.md` for detailed instructions)

## Building and Running

For complete instructions on building and running the project, refer to [RUNNING.md](RUNNING.md).

## Project Structure Details

- **frontend/pages/**: Application screens (Dashboard, LoginPage, SalesTerminal, etc.)
- **frontend/ui/**: Reusable UI components and styling (UiTheme, Animations, TopBar)
- **backend/services/**: Business logic services (SalesService, InventoryService, etc.)

## Notes

- The compiled output is generated in the `bin/` folder
- Configure VS Code settings in `.vscode/settings.json` if needed
- Use the JAVA PROJECTS view to manage dependencies