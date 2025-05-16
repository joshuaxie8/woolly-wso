package bktree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.awt.BorderLayout;
import java.awt.event.*;  // only if needed

// temporary graphic display

class Graphics extends JFrame {

    private JTextField inputField;
    private DefaultListModel<String> suggestionModel;
    private JList<String> suggestionList;
    Search parent;

    public Graphics(Search parent) {
        this.parent = parent;
        setTitle("WSO Mockup");
        setSize(600, 400);
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
        suggestionModel.clear();

        String a = inputField.getText().toLowerCase();
        if (a.isEmpty()) return;

        // ArrayList<Integer> exactMatches = parent.t.traverseVals(parent.t.probe(a), a);
        // ArrayList<Integer> fuzzyMatches = parent.bk.fuzzyVals(a, Math.min(2, a.length() / 3), false, true);
        // // ArrayList<Integer> fuzzyMatches = parent.mbk.fuzzyVals(a, Math.min(2, a.length() / 3), 0, false, true);
        // Set<Integer> allMatches = new LinkedHashSet<>(exactMatches);
        // allMatches.addAll(fuzzyMatches);

        Set<Integer> matches = parent.findMatches(a);

        for (int i : matches) {
            String s = parent.idToPerson.get(i).getFullName();
            s += ", type = "; s += parent.idToPerson.get(i).getType();
            suggestionModel.addElement(s);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Graphics(new Search(MetricFunctions.osa)).setVisible(true);
        });
    }
}