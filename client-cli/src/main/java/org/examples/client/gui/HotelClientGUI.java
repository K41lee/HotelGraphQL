package org.examples.client.gui;

import org.examples.client.MultiAgencyClient;
import org.examples.client.MiniJson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Interface graphique principale pour le client d'hôtel
 */
public class HotelClientGUI extends JFrame {
    
    private MultiAgencyClient multiAgencyClient;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Panneaux
    private WelcomePanel welcomePanel;
    private SearchPanel searchPanel;
    private ResultsPanel resultsPanel;
    private ReservationPanel reservationPanel;
    
    // Configuration des agences
    private String agencyHost = "localhost";

    public HotelClientGUI() {
        super("🏨 Système de Réservation d'Hôtels");
        initializeGUI();
        connectToAgency();
    }
    
    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Configuration du Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Panneau principal avec CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Création des panneaux
        welcomePanel = new WelcomePanel(this);
        searchPanel = new SearchPanel(this);
        resultsPanel = new ResultsPanel(this);
        reservationPanel = new ReservationPanel(this);
        
        // Ajout des panneaux
        mainPanel.add(welcomePanel, "WELCOME");
        mainPanel.add(searchPanel, "SEARCH");
        mainPanel.add(resultsPanel, "RESULTS");
        mainPanel.add(reservationPanel, "RESERVATION");
        
        add(mainPanel);
        
        // Afficher le panneau de bienvenue
        showPanel("WELCOME");
        
        // Gestion de la fermeture
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });
    }
    
    private void connectToAgency() {
        // Connexion en arrière-plan pour ne pas bloquer l'affichage
        new Thread(() -> {
            try {
                multiAgencyClient = new MultiAgencyClient();

                // Connexion à l'agence 1 (MegaAgence - 10%)
                try {
                    multiAgencyClient.addAgency(agencyHost, 7070, "MegaAgence (-10%)");
                } catch (Exception e) {
                    System.err.println("Erreur connexion Agence 1: " + e.getMessage());
                }

                // Connexion à l'agence 2 (SuperAgence - 20%)
                try {
                    multiAgencyClient.addAgency(agencyHost, 7071, "SuperAgence (-20%)");
                } catch (Exception e) {
                    System.err.println("Erreur connexion Agence 2: " + e.getMessage());
                }

                if (multiAgencyClient.getAgencyCount() == 0) {
                    throw new Exception("Aucune agence disponible");
                }

                String catalogJson = multiAgencyClient.getCatalog();

                // Extraire les informations du catalogue
                String agencyInfo = multiAgencyClient.getAgencyCount() + " agences connectées";
                List<String> cities = MiniJson.getStringArray(catalogJson, "cities");
                List<String> agencies = MiniJson.getStringArray(catalogJson, "agencies");
                List<String> availableAgencies = MiniJson.getStringArray(catalogJson, "availableAgencies");

                // Mettre à jour l'interface sur le thread Swing
                SwingUtilities.invokeLater(() -> {
                    welcomePanel.setAgencyInfo(agencyInfo, cities, availableAgencies != null ? availableAgencies : agencies);
                    searchPanel.setCities(cities);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(HotelClientGUI.this,
                        "Erreur de connexion aux agences:\n" + e.getMessage() +
                        "\n\nAssurez-vous que les serveurs sont lancés:\n./lancement.sh --no-client",
                        "Erreur de connexion",
                        JOptionPane.ERROR_MESSAGE);
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
    
    public MultiAgencyClient getMultiAgencyClient() {
        return multiAgencyClient;
    }
    
    public void disconnect() {
        if (multiAgencyClient != null) {
            try {
                multiAgencyClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        // Lecture des paramètres système
        String host = System.getProperty("agency.tcp.host", "localhost");

        SwingUtilities.invokeLater(() -> {
            HotelClientGUI gui = new HotelClientGUI();
            gui.agencyHost = host;
            gui.setVisible(true);
        });
    }
}

