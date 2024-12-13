package packageclient;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class MaClasse {
    public static final byte CLA_ATM_APPLET = (byte) 0xB0;
    public static final byte INS_VERIFY_PIN = 0x03;
    public static final byte INS_DRAW = 0x02;
    public static final byte INS_DEPOSIT = 0x01;
    public static final byte INS_CHECK_BALANCE = 0x00;

    private static CadT1Client cad;

    public static void main(String[] args) {
    	  try {
              Socket socket = new Socket("localhost", 9025);
              BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
              BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
              cad = new CadT1Client(input, output);
              cad.powerUp();
          } catch (Exception e) {
              e.printStackTrace();
          }
      
    	  JFrame frame = new JFrame("ATM Client");
    	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	    frame.setSize(600, 500); // Increased frame size
    	    frame.setLayout(new BorderLayout());
    	    frame.setUndecorated(true); // Remove window decorations (title bar)

    	    // Main Panel with background color
    	    JPanel mainPanel = new JPanel(new BorderLayout());
    	    mainPanel.setBackground(new Color(43, 84, 126)); // Background color

    	    // Header Panel
    	    JPanel headerPanel = new JPanel(new BorderLayout());
    	    headerPanel.setBackground(new Color(43, 84, 126));
    	    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    	    // Bank Name and Logo
    	    JLabel logoLabel = new JLabel();
    	    ImageIcon logoIcon = new ImageIcon(MaClasse.class.getResource("bank_logo.png"));
    	    Image scaledLogo = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
    	    logoLabel.setIcon(new ImageIcon(scaledLogo));
    	    
    	    JLabel bankNameLabel = new JLabel("Generic Bank", SwingConstants.LEFT);
    	    bankNameLabel.setFont(new Font("Verdana", Font.BOLD, 20));
    	    bankNameLabel.setForeground(Color.WHITE);

    	    JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    	    logoPanel.setBackground(new Color(43, 84, 126));
    	    logoPanel.add(logoLabel);
    	    logoPanel.add(bankNameLabel);

    	    headerPanel.add(logoPanel, BorderLayout.WEST);

    	    // Welcome Text with Shadow Effect
    	    JLabel headerLabel = new JLabel("Welcome !", SwingConstants.CENTER);
    	    headerLabel.setFont(new Font("Verdana", Font.BOLD, 24));
    	    headerLabel.setForeground(Color.WHITE);
    	    headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    	    headerLabel.setOpaque(false);

    	    headerPanel.add(headerLabel, BorderLayout.SOUTH);
    	    mainPanel.add(headerPanel, BorderLayout.NORTH);

    	    // Center Panel with PIN input and buttons
    	    JPanel inputPanel = new JPanel(new GridBagLayout());
    	    inputPanel.setBackground(new Color(43, 84, 126));
    	    inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
    	    GridBagConstraints gbc = new GridBagConstraints();
    	    gbc.insets = new Insets(10, 10, 10, 10);

    	    // Label for PIN input
    	    JLabel pinLabel = new JLabel("Enter your 4-digit PIN:");
    	    pinLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    	    pinLabel.setForeground(Color.LIGHT_GRAY);

    	    // PIN input field
    	    JTextField pinField = new JTextField();
    	    pinField.setFont(new Font("Arial", Font.PLAIN, 16));
    	    pinField.setHorizontalAlignment(JTextField.CENTER);
    	    pinField.setPreferredSize(new Dimension(250, 30));
    	    pinField.setBackground(new Color(220, 220, 220));
    	    pinField.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 2));

    	    // Validate Button with new styling
    	    JButton validateButton = new JButton("Validate");
    	    validateButton.setFont(new Font("Arial", Font.BOLD, 16));
    	    validateButton.setBackground(new Color(34, 139, 34)); // Green button
    	    validateButton.setForeground(Color.WHITE);
    	    validateButton.setFocusPainted(false);
    	    validateButton.setPreferredSize(new Dimension(120, 40));
    	    validateButton.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));

    	    // Exit Button with red color and custom style
    	    JButton exitButton = new JButton("Exit");
    	    exitButton.setFont(new Font("Arial", Font.BOLD, 16));
    	    exitButton.setBackground(new Color(200, 0, 0)); // Red button
    	    exitButton.setForeground(Color.WHITE);
    	    exitButton.setFocusPainted(false);
    	    exitButton.setPreferredSize(new Dimension(120, 40));
    	    exitButton.setBorder(BorderFactory.createLineBorder(new Color(150, 0, 0), 2));
    	    exitButton.addActionListener(e -> frame.dispose());

    	    // Status message label for validation
    	    JLabel statusLabel = new JLabel("", JLabel.CENTER);
    	    statusLabel.setForeground(Color.RED);

    	    // Adding components to the center panel
    	    gbc.gridx = 0;
    	    gbc.gridy = 0;
    	    gbc.gridwidth = 2;
    	    inputPanel.add(pinLabel, gbc);

    	    gbc.gridy = 1;
    	    inputPanel.add(pinField, gbc);

    	    gbc.gridy = 2;
    	    gbc.gridwidth = 1;
    	    gbc.anchor = GridBagConstraints.CENTER;
    	    inputPanel.add(validateButton, gbc);

    	    gbc.gridx = 1;
    	    inputPanel.add(exitButton, gbc);

    	    gbc.gridy = 3;
    	    inputPanel.add(statusLabel, gbc);

    	    mainPanel.add(inputPanel, BorderLayout.CENTER);

    	    // Footer Panel with a refined message
    	    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    	    footerPanel.setOpaque(false);
    	    JLabel footerLabel = new JLabel("Thank you for using our ATM!", SwingConstants.CENTER);
    	    footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
    	    footerLabel.setForeground(Color.LIGHT_GRAY);
    	    footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	    footerPanel.add(footerLabel);

    	    mainPanel.add(footerPanel, BorderLayout.SOUTH);

    	    // Add the main panel to the frame
    	    frame.add(mainPanel);
    	    frame.setLocationRelativeTo(null); // Center the frame
    	    frame.setVisible(true);

         // Validate button functionality with status message
        

        try {
        	 

        	    validateButton.addActionListener(e -> {
        	        String pin = pinField.getText();
        	        try {
        	            String status = verifyPin(cad, pin);
        	            if (status.equals("true")) {
        	                showTransactionOptions();
        	                frame.dispose(); // Fermer la fenêtre actuelle
        	                // Ouvrir la nouvelle fenêtre avec les options de transaction
        	            } else {
        	                if (status.equals("false")) {
        	                    JOptionPane.showMessageDialog(null, "The PIN must be exactly 4 digits.");
        	                } else if (status.equals("false2"))
        	                    JOptionPane.showMessageDialog(frame, "Code Pin Incorrect.");
        	                    else if (status.equals("false3"))
        	                    {JOptionPane.showMessageDialog(frame, "Your card is blocked. Please contact customer service.");
        	                    frame.dispose();
        	                    }else 
            	                    JOptionPane.showMessageDialog(frame, "Error ");

        	                }
        	                
        	            
        	        } catch (Exception ex) {
        	            ex.printStackTrace();
        	        }
        	    });

        	} catch (Exception e) {
        	    e.printStackTrace();           
    }}
    static String res = "";

    private static String verifyPin(CadT1Client cad, String pin) throws Exception {
        if (!pin.matches("\\d{4}")) {
            res = "false";
        } else {
            Apdu apdu = new Apdu();
            apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
            apdu.command[Apdu.INS] = INS_VERIFY_PIN;
            apdu.command[Apdu.P1] = 0x00;
            apdu.command[Apdu.P2] = 0x00;

            // Convert the PIN into decimal values before sending
            byte[] pinBytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                pinBytes[i] = (byte) (pin.charAt(i) - '0'); // Convert each ASCII character to a decimal value
            }

            apdu.setDataIn(pinBytes);

            // Log the sent APDU command
            logApduCommand(apdu);

            cad.exchangeApdu(apdu);

            // Log the received APDU response
            logApduResponse(apdu);

            if(apdu.getStatus() == 0x9000)
            res="true";
            else if(apdu.getStatus() == 0x6301)
            	res="false2";//verification echouee 
            else if(apdu.getStatus() == 0x6A88)
            	res="false3";//Card blocked 
            
        }

        return res;
    }

    public static void showTransactionOptions() {
        // Create a frame for transaction options
        JFrame transactionFrame = new JFrame("ATM Transaction Options");
        transactionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        transactionFrame.setSize(600, 300); // Adjusted size for better layout
        transactionFrame.setUndecorated(true); // Removes window title bar for an ATM look
        transactionFrame.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 4)); // ATM screen border

        // Create a main panel with a smooth gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 70, 120), 0, getHeight(), new Color(10, 40, 80));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));

        // Add a top label with a sleek style
        JLabel optionsLabel = new JLabel("Choose Your Transaction", JLabel.CENTER);
        optionsLabel.setForeground(Color.WHITE);
        optionsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        optionsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Create a panel for buttons with a modern card layout
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10)); // Vertical layout for buttons
        buttonPanel.setOpaque(false);

        // Create buttons with fixed size
        JButton withdrawButton = createFixedSizeButton("Withdraw Money", 220, 40);
        JButton depositButton = createFixedSizeButton("Deposit Money", 220, 40);
        JButton checkBalanceButton = createFixedSizeButton("Check Balance", 220, 40);

        // Center align the buttons within their grid
        JPanel withdrawPanel = createCenteredPanel(withdrawButton);
        JPanel depositPanel = createCenteredPanel(depositButton);
        JPanel checkBalancePanel = createCenteredPanel(checkBalanceButton);

        // Add buttons to the panel
        buttonPanel.add(withdrawPanel);
        buttonPanel.add(depositPanel);
        buttonPanel.add(checkBalancePanel);

        // Create a bottom panel for the Exit button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);

        // Create the Exit button
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(200, 0, 0)); // Red background
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createLineBorder(new Color(150, 0, 0), 2)); // Dark red border
        exitButton.setPreferredSize(new Dimension(80, 30)); // Fixed size

        // Add action listener to close the frame
        exitButton.addActionListener(e -> transactionFrame.dispose());

        // Add the Exit button to the bottom panel
        bottomPanel.add(exitButton);

        // Add all panels to the main panel
        mainPanel.add(optionsLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        transactionFrame.add(mainPanel);

        // Add action listeners for buttons
        withdrawButton.addActionListener(e -> Afficher_frame_draw(INS_DRAW));
        depositButton.addActionListener(e -> Afficher_FRame_deposit(INS_DEPOSIT));
        checkBalanceButton.addActionListener(e -> Afficher_Frame_checkbalance(INS_CHECK_BALANCE));

        // Center the frame on the screen
        transactionFrame.setLocationRelativeTo(null);

        // Display the frame
        transactionFrame.setVisible(true);
    }

    // Helper method to create buttons with fixed size
    private static JButton createFixedSizeButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 204)); // Default blue color
        button.setPreferredSize(new Dimension(width, height));
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1)); // Slight border

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 153, 255)); // Lighter blue on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204)); // Back to default blue
            }
        });

        return button;
    }

    // Helper method to center a component inside a panel
    private static JPanel createCenteredPanel(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);
        panel.add(component);
        return panel;
    }

    // Helper method to create rounded buttons with smaller size
    
    private static Object Afficher_Frame_checkbalance(byte insCheckBalance) {
        try {
            Apdu apdu = new Apdu();
            apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
            apdu.command[Apdu.INS] = insCheckBalance;
            apdu.command[Apdu.P1] = 0x00;
            apdu.command[Apdu.P2] = 0x00;
            // Log the sent APDU command
            logApduCommand(apdu);

            cad.exchangeApdu(apdu);

            // Log the received APDU response
            logApduResponse(apdu);

            if (apdu.getStatus() == 0x9000) {
                byte[] balanceBytes = apdu.getDataOut();
                int balance = ((balanceBytes[0] & 0xFF) << 8) | (balanceBytes[1] & 0xFF);
                JOptionPane.showMessageDialog(null, "Current Balance: " + balance + " dinar.");
            } else {
                JOptionPane.showMessageDialog(null, "Error checking balance.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean deposit(CadT1Client cad, int amount) throws Exception {
        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
        apdu.command[Apdu.INS] = INS_DEPOSIT;
        apdu.command[Apdu.P1] = 0x00;
        apdu.command[Apdu.P2] = 0x00;

        byte[] amountBytes = new byte[2];
        amountBytes[0] = (byte) (amount >> 8);
        amountBytes[1] = (byte) (amount);

        apdu.setDataIn(amountBytes);

        logApduCommand(apdu);

        cad.exchangeApdu(apdu);

        logApduResponse(apdu);

        int status = apdu.getStatus();
        if (status == 0x9000) {
            return true; // Succès
        } else {
            showErrorDialog(status);
            return false; // Échec
        }
    }

    private static void showErrorDialog(int status) {
        String errorMessage;
        switch (status) {
            case 0x6A83:
                errorMessage = "Montant de dépôt trop élevé. Le montant ne doit pas dépasser 500 unités.";
                break;
            case 0x6A82:
                errorMessage = "Montant de dépôt invalide. Le montant doit être un multiple de 10.";
                break;
            case 0x6A84:
                errorMessage = "Balance maximale dépassée. Le solde ne doit pas dépasser 10000 unités.";
                break;
            default:
                errorMessage = "Erreur lors du dépôt.";
                break;
        }
        JOptionPane.showMessageDialog(null, errorMessage, "Erreur de Dépôt", JOptionPane.ERROR_MESSAGE);
    }

    private static void Afficher_FRame_deposit(byte insDeposit) {
        JFrame depositFrame = new JFrame("Déposer de l'argent");
        depositFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        depositFrame.setSize(300, 200);
        depositFrame.setLayout(new GridLayout(3, 1));

        JTextField amountField = new JTextField();
        JButton depositButton = new JButton("Déposer");

        depositFrame.add(new JLabel("Entrez le montant à déposer :"));
        depositFrame.add(amountField);
        depositFrame.add(depositButton);

        depositButton.addActionListener(e -> {
            String amountText = amountField.getText();
            try {
                int amount = Integer.parseInt(amountText);
                if (deposit(cad, amount)) {
                    JOptionPane.showMessageDialog(depositFrame, "Dépôt réussi.");
                    depositFrame.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(depositFrame, "Veuillez entrer un montant valide.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        depositFrame.setVisible(true);
    }


	private static void Afficher_frame_draw(byte insDraw) {
	    SwingUtilities.invokeLater(() -> {
	        JFrame mainFrame = new JFrame("ATM Simulator - Withdraw");
	        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        mainFrame.setSize(300, 300);
	        mainFrame.setLayout(new GridLayout(5, 1, 10, 10));

	        JLabel instructionLabel = new JLabel("Select a withdrawal amount:", SwingConstants.CENTER);
	        mainFrame.add(instructionLabel);

	        String[] amounts = {"10", "20", "50", "100", "Other"};
	        for (String amount : amounts) {
	            JButton button = new JButton(amount);
	            button.addActionListener(e -> {
	                if ("Other".equals(amount)) {
	                    createOtherAmountGUI(insDraw);
	                } else {
	                    try {
	                        int amountInt = Integer.parseInt(amount);
	                        withdrawFunds(insDraw, amountInt);
	                    } catch (Exception ex) {
	                        ex.printStackTrace();
	                    }
	                }
	            });
	            mainFrame.add(button);
	        }

	        mainFrame.setVisible(true);
	    });
	}

	private static void createOtherAmountGUI(byte insDraw) {
	    JFrame otherFrame = new JFrame("Custom Amount");
	    otherFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    otherFrame.setSize(300, 200);
	    otherFrame.setLayout(new GridLayout(3, 1, 10, 10));

	    JTextField amountField = new JTextField();
	    JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
	    errorLabel.setForeground(Color.RED);
	    JButton confirmButton = new JButton("Confirm");

	    confirmButton.addActionListener(e -> {
	        String input = amountField.getText();
	        try {
	            int amount = Integer.parseInt(input);
	            if (amount <= 0) {
	                errorLabel.setText("Amount must be greater than 0.");
	            } else {
	                withdrawFunds(insDraw, amount);
	                otherFrame.dispose();
	            }
	        } catch (NumberFormatException ex) {
	            errorLabel.setText("Invalid amount. Please enter a number.");
	        }
	    });

	    otherFrame.add(amountField);
	    otherFrame.add(confirmButton);
	    otherFrame.add(errorLabel);

	    otherFrame.setVisible(true);
	}

	private static void withdrawFunds(byte insDraw, int amount) {
	    try {
	        Apdu apdu = new Apdu();
	        apdu.command[Apdu.CLA] = CLA_ATM_APPLET; // Use appropriate CLA
	        apdu.command[Apdu.INS] = insDraw; // Use the provided instruction
	        apdu.command[Apdu.P1] = 0x00;
	        apdu.command[Apdu.P2] = 0x00;
	        apdu.setDataIn(new byte[]{(byte) (amount >> 8), (byte) amount});

	        logApduCommand(apdu);
	        cad.exchangeApdu(apdu);
	        logApduResponse(apdu);

	        if (apdu.getStatus() == 0x9000) {
	            JOptionPane.showMessageDialog(null, "Withdrawal successful: " + amount + " units.");
	        } else {
	            JOptionPane.showMessageDialog(null, "Error during withdrawal.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private static void handleTransaction(byte instruction) {
        try {
            Apdu apdu = new Apdu();
            apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
            apdu.command[Apdu.INS] = instruction;
            apdu.command[Apdu.P1] = 0x00;
            apdu.command[Apdu.P2] = 0x00;

            // Journaliser la commande APDU envoyée
            logApduCommand(apdu);

            cad.exchangeApdu(apdu);

            // Journaliser la réponse APDU reçue
            logApduResponse(apdu);

            if (apdu.getStatus() == 0x9000) {
                JOptionPane.showMessageDialog(null, "Opération réussie.");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de l'opération.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour journaliser une commande APDU envoyée
    private static void logApduCommand(Apdu apdu) {
        System.out.println("Command APDU:");
        System.out.println("CLA: " + String.format("%02X", apdu.command[Apdu.CLA]));
        System.out.println("INS: " + String.format("%02X", apdu.command[Apdu.INS]));
        System.out.println("P1: " + String.format("%02X", apdu.command[Apdu.P1]));
        System.out.println("P2: " + String.format("%02X", apdu.command[Apdu.P2]));
        System.out.println("LC: " + (apdu.getDataIn() != null ? apdu.getDataIn().length : 0));
        if (apdu.getDataIn() != null && apdu.getDataIn().length > 0) {
            System.out.println("Data: " + bytesToHex(apdu.getDataIn()));
        }
    }

    // Méthode pour journaliser une réponse APDU reçue
    private static void logApduResponse(Apdu apdu) {
        System.out.println("Response APDU:");
        if (apdu.getDataOut() != null && apdu.getDataOut().length > 0) {
            System.out.println("Data: " + bytesToHex(apdu.getDataOut()));
        }
        System.out.println("SW: " + String.format("%04X", apdu.getStatus()));
    }

    // Méthode utilitaire pour convertir un tableau de bytes en une chaîne hexadécimale
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
