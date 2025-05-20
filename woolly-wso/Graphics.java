package woollywso;

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
    private JLabel placeholderLabel;
    Search parent;

    public Graphics(Search parent) {
        this.parent = parent;

        setTitle("WSO Search – Joshua Xie & Aaron Anidjar");
        setSize(800, 450);
        setLocationRelativeTo(null); // center window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Search bar 
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));

        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        placeholderLabel = new JLabel("Search by name or hometown");
        placeholderLabel.setForeground(Color.GRAY);
        placeholderLabel.setFont(inputField.getFont());
        placeholderLabel.setBorder(inputField.getBorder());
        placeholderLabel.setEnabled(false);
        placeholderLabel.setOpaque(false);
        placeholderLabel.setVisible(true);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(inputField.getPreferredSize());
        inputField.setBounds(0, 0, 600, 30);
        placeholderLabel.setBounds(5, 0, 600, 30);

        layeredPane.add(placeholderLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(inputField, JLayeredPane.DEFAULT_LAYER);
        inputPanel.add(layeredPane, BorderLayout.CENTER);

        inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { togglePlaceholder(); searchAsync(); }
            public void removeUpdate(DocumentEvent e) { togglePlaceholder(); searchAsync(); }
            public void changedUpdate(DocumentEvent e) { togglePlaceholder(); searchAsync(); }
        });

        String[] columnNames = { "Name", "Unix", "Home Town" };
        tableModel = new DefaultTableModel(columnNames, 0);
        suggestionTable = new JTable(tableModel) {
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
        suggestionTable.setShowGrid(false);
        suggestionTable.setIntercellSpacing(new Dimension(0, 0));
        suggestionTable.setAutoCreateRowSorter(true);

        JTableHeader header = suggestionTable.getTableHeader();
        header.setBackground(new Color(102, 51, 153));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(suggestionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void togglePlaceholder() {
        placeholderLabel.setVisible(inputField.getText().trim().isEmpty());
    }

    private void searchAsync() {
        String query = inputField.getText().toLowerCase();

        new SwingWorker<Void, Void>() {
            Set<Integer> matches;

            @Override
            protected Void doInBackground() {
                if (!query.isEmpty()) {
                    matches = parent.findMatches(query);
                }
                return null;
            }

            @Override
            protected void done() {
                tableModel.setRowCount(0);
                if (query.isEmpty() || matches == null) return;

                for (int i : matches) {
                    Person person = parent.idToPerson.get(i);
                    String name = person.getFullName();
                    String unix = person.getUnix();
                    String homeTown = person.getHomeTown();

                    if (!homeTown.isEmpty()) {
                        if (!person.getHomeState().isEmpty()) {
                            homeTown += ", " + person.getHomeState();
                        }
                        if (!person.getHomeCountry().isEmpty() &&
                            !person.getHomeCountry().equals("United States")) {
                            homeTown += ", " + person.getHomeCountry();
                        }
                    }

                    tableModel.addRow(new Object[]{name, unix, homeTown});
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new Graphics(new Search(MetricFunctions.osa, "woolly-wso/dummy-data.csv")).setVisible(true)
        );
    }
}
