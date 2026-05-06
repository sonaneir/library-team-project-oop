package app;

import java.util.List;

// utility class with quicksort implementations for sorting books by different fields
public class BookSorter {

    // quicksort by title — recursively sorts the list
    public static void quickSortByTitle(List<Book> books, int low, int high) {
        if (low < high) {
            // pi = pivot index after partitioning
            int pi = partitionByTitle(books, low, high);
            // sort left part (smaller than pivot)
            quickSortByTitle(books, low, pi - 1);
            // sort right part (bigger than pivot)
            quickSortByTitle(books, pi + 1, high);
        }
    }

    // partition step — puts pivot in correct position, smaller items left, bigger right
    private static int partitionByTitle(List<Book> books, int low, int high) {
        // pivot is the last element's title (lowercased for case-insensitive sorting)
        String pivot = books.get(high).getTitle().toLowerCase();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            // compareTo <= 0 means "less than or equal" alphabetically
            if (books.get(j).getTitle().toLowerCase().compareTo(pivot) <= 0) {
                i++;
                swap(books, i, j);
            }
        }
        // place pivot in its final position
        swap(books, i + 1, high);
        return i + 1;
    }

    // quicksort by id — same logic, just compares numbers
    public static void quickSortById(List<Book> books, int low, int high) {
        if (low < high) {
            int pi = partitionById(books, low, high);
            quickSortById(books, low, pi - 1);
            quickSortById(books, pi + 1, high);
        }
    }

    private static int partitionById(List<Book> books, int low, int high) {
        int pivot = books.get(high).getId();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (books.get(j).getId() <= pivot) {
                i++;
                swap(books, i, j);
            }
        }
        swap(books, i + 1, high);
        return i + 1;
    }

    // quicksort by author — same logic as by title
    public static void quickSortByAuthor(List<Book> books, int low, int high) {
        if (low < high) {
            int pi = partitionByAuthor(books, low, high);
            quickSortByAuthor(books, low, pi - 1);
            quickSortByAuthor(books, pi + 1, high);
        }
    }

    private static int partitionByAuthor(List<Book> books, int low, int high) {
        String pivot = books.get(high).getAuthor().toLowerCase();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (books.get(j).getAuthor().toLowerCase().compareTo(pivot) <= 0) {
                i++;
                swap(books, i, j);
            }
        }
        swap(books, i + 1, high);
        return i + 1;
    }

    // helper — swaps two books in the list using a temporary variable
    private static void swap(List<Book> books, int i, int j) {
        Book temp = books.get(i);
        books.set(i, books.get(j));
        books.set(j, temp);
    }
}