package app;

import java.util.List;

public class BookSorter {

    public static void quickSortByTitle(List<Book> books, int low, int high) {
        if (low < high) {
            int pi = partitionByTitle(books, low, high);
            quickSortByTitle(books, low, pi - 1);
            quickSortByTitle(books, pi + 1, high);
        }
    }

    private static int partitionByTitle(List<Book> books, int low, int high) {
        String pivot = books.get(high).getTitle().toLowerCase();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (books.get(j).getTitle().toLowerCase().compareTo(pivot) <= 0) {
                i++;
                swap(books, i, j);
            }
        }
        swap(books, i + 1, high);
        return i + 1;
    }

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

    private static void swap(List<Book> books, int i, int j) {
        Book temp = books.get(i);
        books.set(i, books.get(j));
        books.set(j, temp);
    }
}