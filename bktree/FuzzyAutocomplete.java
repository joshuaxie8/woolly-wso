package bktree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.awt.BorderLayout;
import java.awt.event.*;  // only if needed

// temporary graphic display

public class FuzzyAutocomplete extends JFrame {
    private JTextField inputField;
    private DefaultListModel<String> suggestionModel;
    private JList<String> suggestionList;
    private List<String> dictionary;

    public FuzzyAutocomplete(List<String> dictionary) {
        this.dictionary = dictionary;
        setTitle("Fuzzy Autocomplete");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputField = new JTextField();
        suggestionModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionModel);

        inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        add(inputField, BorderLayout.NORTH);
        add(new JScrollPane(suggestionList), BorderLayout.CENTER);
    }

    private void updateSuggestions() {
        String input = inputField.getText().toLowerCase();
        suggestionModel.clear();

        if (input.isEmpty()) return;

        List<String> matches = new ArrayList<>();
        for (String word : dictionary) {
            if (levenshtein(input, word.toLowerCase()) <= 1) {
                matches.add(word);
            }
        }

        Collections.sort(matches);
        for (String match : matches) {
            suggestionModel.addElement(match);
        }
    }

    // Simple Levenshtein distance implementation
    private int levenshtein(String a, String b) {
        int m = a.length();
        int n = b.length();

        if (m == 0) return n;
        if (n == 0) return m;

        int[] d0 = new int[n + 1];
        int[] d1 = new int[n + 1];
        int[] d2 = new int[n + 1];

        Arrays.setAll(d1, i -> i); // d1 = {0, 1, 2, ... , n};

        for (int i = 0; i < m; i++) {
            d2[0] = i + 1;

            for (int j = 0; j < n; j++) {
                int del = d1[j + 1] + 1;
                int ins = d2[j] + 1;
                int sub = (a.charAt(i) == b.charAt(j)) ? d1[j] : d1[j] + 1;

                d2[j + 1] = Math.min(del, Math.min(ins, sub));

                if (i > 0 && j > 0 && a.charAt(i) == b.charAt(j - 1) && a.charAt(i - 1) == b.charAt(j)) {
                    d2[j + 1] = Math.min(d2[j + 1], d0[j - 1] + 1);
                }
            }
            int[] temp = d0;
            d0 = d1;
            d1 = d2;
            d2 = temp;
        }
        return d1[n];
    }

    public static void main(String[] args) {
        List<String> sampleWords = Arrays.asList("hello", "help", "helmet", "hell", "heal", "hole", "hero", "heap");
        SwingUtilities.invokeLater(() -> {
            new FuzzyAutocomplete(sampleWords).setVisible(true);
        });
    }
}