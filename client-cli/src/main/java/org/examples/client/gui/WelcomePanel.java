package org.examples.client.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panneau de bienvenue affichant les informations de l'agence
 */
public class WelcomePanel extends JPanel {
    
    private HotelClientGUI mainFrame;
    private JLabel agencyNameLabel;
    private JTextArea infoTextArea;
    private JButton startButton;
    
    public WelcomePanel(HotelClientGUI mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(new Color(240, 248, 255));
        
        // Panneau supérieur avec titre
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🏨 Bienvenue");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.BLACK);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        agencyNameLabel = new JLabel("Système de Réservation d'Hôtels");
        agencyNameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        agencyNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        agencyNameLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        agencyNameLabel.setForeground(Color.BLACK);
        topPanel.add(agencyNameLabel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Panneau central avec informations
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoTextArea.setBackground(Color.WHITE);
        infoTextArea.setForeground(Color.BLACK);
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);
        infoTextArea.setText("Connexion à l'agence en cours...");
        
        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Panneau inférieur avec bouton
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setOpaque(false);
        
        startButton = new JButton("🔍 Rechercher un Hôtel");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setPreferredSize(new Dimension(300, 50));
        startButton.setBackground(Color.LIGHT_GRAY);
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        startButton.addActionListener(e -> mainFrame.showPanel("SEARCH"));
        
        // Effet hover
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(Color.GRAY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(Color.LIGHT_GRAY);
            }
        });
        
        bottomPanel.add(startButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void setAgencyInfo(String agencyName, List<String> cities, List<String> agencies) {
        if (agencyName != null && !agencyName.isEmpty()) {
            agencyNameLabel.setText("Agence: " + agencyName);
        }
        
        StringBuilder info = new StringBuilder();
        info.append("═══════════════════════════════════════════════════════\n");
        info.append("         INFORMATIONS SUR LES SERVICES DISPONIBLES\n");
        info.append("════════════════════════════════════════════════��══════\n\n");
        
        if (cities != null && !cities.isEmpty()) {
            info.append("🌍 Villes desservies:\n");
            for (String city : cities) {
                info.append("   • ").append(city).append("\n");
            }
            info.append("\n");
        }
        
        if (agencies != null && !agencies.isEmpty()) {
            info.append("🏢 Agences partenaires:\n");
            for (String agency : agencies) {
                info.append("   • ").append(agency).append("\n");
            }
            info.append("\n");
        }
        
        info.append("═══════════════════════════════════════════════════════\n\n");
        info.append("✨ Prêt à réserver votre séjour idéal !\n");
        info.append("   Cliquez sur le bouton ci-dessous pour commencer.");
        
        infoTextArea.setText(info.toString());
        infoTextArea.setCaretPosition(0);
    }
}

