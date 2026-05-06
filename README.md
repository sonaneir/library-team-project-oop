# Library Manager

A JavaFX desktop application for managing a small library. The system supports two roles — **Administrator** and **User** — and provides functionality for adding, issuing, reserving, and tracking books.

This project was developed as part of a university assignment.

## Overview

The application implements a complete library workflow:

- Administrators can add, delete, search, sort, and issue books, as well as process reservations from a queue.
- Users can browse available books, search by title or author, and reserve a book of their choice.
- Reserved books are placed into a FIFO queue; the administrator issues books to users in the order they reserved them.
- Book data is persisted in a plain text file and loaded at startup.

## Features

- Role-based interface (Administrator / User)
- Add and delete books with automatic ID management
- Issue books directly or through the reservation queue
- Search by title or author
- Sort books by title, author, or ID using a custom Quicksort implementation
- Reservation queue implemented with a `LinkedList`-based FIFO structure
- Live dashboard with statistics and progress indicators
- Persistent storage in a text file

## Tech Stack

- **Language:** Java
- **GUI Framework:** JavaFX
- **Styling:** CSS
- **Storage:** Plain text file (`books.txt`)

## Project Structure

```
src/main/
├── java/app/
│   ├── LibraryApplication.java
│   ├── Library.java
│   ├── Book.java
│   ├── User.java
│   ├── UserQueue.java
│   └── BookSorter.java
└── resources/
    ├── data/books.txt
    └── styles/style.css
```

## Class Responsibilities

| Class | Responsibility |
|---|---|
| `LibraryApplication` | Entry point; builds and manages all JavaFX scenes and views. |
| `Library` | Holds book collections, manages reservations, and handles file I/O. |
| `Book` | Represents a single book and its state (borrowed, reserved, etc.). |
| `User` | Represents a user and their current reservation. |
| `UserQueue` | FIFO queue used for managing the reservation order. |
| `BookSorter` | Provides Quicksort methods for sorting books by title, author, or ID. |
