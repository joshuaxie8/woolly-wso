package bktree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.Set;

class Graphics extends JFrame {

    private JTextField inputField;
    private DefaultTableModel tableModel;
    private JTable suggestionTable;
    Search parent;

    public Graphics(Search parent) {
        this.parent = parent;
        setTitle("WSO Search Temp Graphics - Joshua Xie and Aaron Anidjar");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 16));

        String[] columnNames = { "Name", "Williams Username", "Home Town" };
        tableModel = new DefaultTableModel(columnNames, 0);
        suggestionTable = new JTable(tableModel) {
            // Alternate row colors
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                } else {
                    c.setBackground(getSelectionBackground());
                }
                return c;
            }
        };

        suggestionTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        suggestionTable.setRowHeight(28);
        suggestionTable.setFillsViewportHeight(true);

        // Style header
        JTableHeader header = suggestionTable.getTableHeader();
        header.setBackground(new Color(102, 51, 153));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));

        inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        add(inputField, BorderLayout.NORTH);
        add(new JScrollPane(suggestionTable), BorderLayout.CENTER);
    }

    private void updateSuggestions() {
        tableModel.setRowCount(0); // Clear table

        String a = inputField.getText().toLowerCase();
        if (a.isEmpty()) return;

        Set<Integer> matches = parent.findMatches(a);

        for (int i : matches) {
            String name = parent.idToPerson.get(i).getFullName();
            String username = parent.idToPerson.get(i).getUnix();  
            String homeTown = parent.idToPerson.get(i).getHomeTown();
            if (!homeTown.equals("")) {
                if (!parent.idToPerson.get(i).getHomeState().equals("")) {
                    homeTown += ", " + parent.idToPerson.get(i).getHomeState();
                }
                if (!(parent.idToPerson.get(i).getHomeCountry().equals("") || parent.idToPerson.get(i).getHomeCountry().equals("United States"))) {
                     homeTown += ", " + parent.idToPerson.get(i).getHomeCountry();
                }
            }
            tableModel.addRow(new Object[]{name, username, homeTown});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Graphics(new Search(MetricFunctions.osa)).setVisible(true);
        });
    }
}
