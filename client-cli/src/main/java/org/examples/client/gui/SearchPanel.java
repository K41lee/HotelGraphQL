package org.examples.client.gui;

import org.examples.client.MiniJson;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Panneau de recherche d'offres d'hôtels
 */
public class SearchPanel extends JPanel {

    private HotelClientGUI mainFrame;
    private JComboBox<String> cityComboBox;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JSpinner bedsSpinner;
    private JButton searchButton;
    private JButton backButton;
    private JTextArea statusTextArea;

    public SearchPanel(HotelClientGUI mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 250));

        // Titre
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("🔍 Recherche d'Offres");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Panneau de formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Ville
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel cityLabel = new JLabel("🌍 Ville:");
        cityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cityLabel.setForeground(Color.BLACK);
        formPanel.add(cityLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cityComboBox = new JComboBox<>();
        cityComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        cityComboBox.setPreferredSize(new Dimension(300, 35));
        formPanel.add(cityComboBox, gbc);

        // Date de début
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel startLabel = new JLabel("📅 Date d'arrivée:");
        startLabel.setFont(new Font("Arial", Font.BOLD, 14));
        startLabel.setForeground(Color.BLACK);
        formPanel.add(startLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("dd/MM/yyyy");
        startDateChooser.setMinSelectableDate(new Date());
        startDateChooser.setPreferredSize(new Dimension(300, 35));
        startDateChooser.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(startDateChooser, gbc);

        // Date de fin
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel endLabel = new JLabel("📅 Date de départ:");
        endLabel.setFont(new Font("Arial", Font.BOLD, 14));
        endLabel.setForeground(Color.BLACK);
        formPanel.add(endLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("dd/MM/yyyy");
        endDateChooser.setMinSelectableDate(new Date());
        endDateChooser.setPreferredSize(new Dimension(300, 35));
        endDateChooser.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(endDateChooser, gbc);

        // Nombre de lits
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel bedsLabel = new JLabel("🛏️ Nombre de lits:");
        bedsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bedsLabel.setForeground(Color.BLACK);
        formPanel.add(bedsLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        bedsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        bedsSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) bedsSpinner.getEditor()).getTextField().setEditable(false);
        bedsSpinner.setPreferredSize(new Dimension(100, 35));
        formPanel.add(bedsSpinner, gbc);

        // Panneau de boutons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 10, 10);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        searchButton = new JButton("🔍 Rechercher");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.setPreferredSize(new Dimension(180, 45));
        searchButton.setBackground(Color.LIGHT_GRAY);
        searchButton.setForeground(Color.BLACK);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> performSearch());

        backButton = new JButton("⬅️ Retour");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(120, 45));
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainFrame.showPanel("WELCOME"));

        buttonPanel.add(backButton);
        buttonPanel.add(searchButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Zone de statut
        statusTextArea = new JTextArea(3, 50);
        statusTextArea.setEditable(false);
        statusTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusTextArea.setBackground(new Color(250, 250, 250));
        statusTextArea.setForeground(Color.BLACK);
        statusTextArea.setBorder(new TitledBorder("Statut"));
        add(new JScrollPane(statusTextArea), BorderLayout.SOUTH);
    }

    public void setCities(List<String> cities) {
        cityComboBox.removeAllItems();
        if (cities != null) {
            for (String city : cities) {
                cityComboBox.addItem(city);
            }
        }
    }

    private void performSearch() {
        // Validation
        if (cityComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ville", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner les dates", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (endDate.before(startDate)) {
            JOptionPane.showMessageDialog(this, "La date de départ doit être après la date d'arrivée", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Recherche
        statusTextArea.setText("Recherche en cours...");
        searchButton.setEnabled(false);

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String city = (String) cityComboBox.getSelectedItem();
                String start = sdf.format(startDate);
                String end = sdf.format(endDate);
                int beds = (Integer) bedsSpinner.getValue();

                // Recherche dans toutes les agences
                return mainFrame.getMultiAgencyClient().searchAll(city, start, end, beds, null);
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                try {
                    String result = get();
                    statusTextArea.setText("Recherche terminée. " + countOffers(result) + " offre(s) trouvée(s).");

                    // Passer au panneau de résultats
                    ResultsPanel resultsPanel = (ResultsPanel) ((JPanel) mainFrame.getContentPane().getComponent(0))
                        .getComponent(2);
                    resultsPanel.displayResults(result,
                        (String) cityComboBox.getSelectedItem(),
                        startDate, endDate, (Integer) bedsSpinner.getValue());
                    mainFrame.showPanel("RESULTS");

                } catch (Exception ex) {
                    statusTextArea.setText("Erreur: " + ex.getMessage());
                    JOptionPane.showMessageDialog(SearchPanel.this,
                        "Erreur lors de la recherche:\n" + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private int countOffers(String json) {
        try {
            List<String> offers = MiniJson.getStringArray(json, "offers");
            return offers != null ? offers.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}

