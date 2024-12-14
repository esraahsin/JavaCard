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
    	    frame.setSize(600, 500); 
    	    frame.setLayout(new BorderLayout());
    	    frame.setUndecorated(true); 

    	    JPanel mainPanel = new JPanel(new BorderLayout());
    	    mainPanel.setBackground(new Color(43, 84, 126)); 

    	    // Header Panel
    	    JPanel headerPanel = new JPanel(new BorderLayout());
    	    headerPanel.setBackground(new Color(43, 84, 126));
    	    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    	    // Bank Name and Logo
    	    JLabel logoLabel = new JLabel();
    	    ImageIcon logoIcon = new ImageIcon(MaClasse.class.getResource("bank_logo.png"));
    	    Image scaledLogo = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
    	    logoLabel.setIcon(new ImageIcon(scaledLogo));
    	    
    	    JLabel bankNameLabel = new JLabel("JC Bank", SwingConstants.LEFT);
    	    bankNameLabel.setFont(new Font("Verdana", Font.BOLD, 16));
    	    bankNameLabel.setForeground(Color.WHITE);

    	    JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    	    logoPanel.setBackground(new Color(43, 84, 126));
    	    logoPanel.add(logoLabel);
    	    logoPanel.add(bankNameLabel);

    	    headerPanel.add(logoPanel, BorderLayout.WEST);

    	    JLabel headerLabel = new JLabel("Welcome !", SwingConstants.CENTER);
    	    headerLabel.setFont(new Font("Verdana", Font.BOLD, 24));
    	    headerLabel.setForeground(Color.WHITE);
    	    headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    	    headerLabel.setOpaque(false);

    	    headerPanel.add(headerLabel, BorderLayout.SOUTH);
    	    mainPanel.add(headerPanel, BorderLayout.NORTH);

    	    JPanel inputPanel = new JPanel(new GridBagLayout());
    	    inputPanel.setBackground(new Color(43, 84, 126));
    	    inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
    	    GridBagConstraints gbc = new GridBagConstraints();
    	    gbc.insets = new Insets(10, 10, 10, 10);

    	 // Label for PIN input
    	    JLabel pinLabel = new JLabel("Enter your 4-digit PIN:");
    	    pinLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    	    pinLabel.setForeground(Color.LIGHT_GRAY);

    	    JPasswordField pinField = new JPasswordField();
    	    pinField.setFont(new Font("Arial", Font.PLAIN, 16));
    	    pinField.setHorizontalAlignment(JTextField.CENTER);
    	    pinField.setPreferredSize(new Dimension(250, 30));
    	    pinField.setBackground(new Color(220, 220, 220));
    	    pinField.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 2));
    	    pinField.setEchoChar('*'); // Set masking character

    	    JButton validateButton = new JButton("Validate");
    	    validateButton.setFont(new Font("Arial", Font.BOLD, 16));
    	    validateButton.setBackground(new Color(34, 139, 34)); // Green button
    	    validateButton.setForeground(Color.WHITE);
    	    validateButton.setFocusPainted(false);
    	    validateButton.setPreferredSize(new Dimension(120, 40));
    	    validateButton.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));

    	    JButton exitButton = new JButton("Exit");
    	    exitButton.setFont(new Font("Arial", Font.BOLD, 16));
    	    exitButton.setBackground(new Color(200, 0, 0)); // Red button
    	    exitButton.setForeground(Color.WHITE);
    	    exitButton.setFocusPainted(false);
    	    exitButton.setPreferredSize(new Dimension(120, 40));
    	    exitButton.setBorder(BorderFactory.createLineBorder(new Color(150, 0, 0), 2));
    	    exitButton.addActionListener(e -> frame.dispose());

    	    JLabel statusLabel = new JLabel("", JLabel.CENTER);
    	    statusLabel.setForeground(Color.RED);

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

    	    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    	    footerPanel.setOpaque(false);
    	    JLabel footerLabel = new JLabel("Thank you for using our ATM!", SwingConstants.CENTER);
    	    footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
    	    footerLabel.setForeground(Color.LIGHT_GRAY);
    	    footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	    footerPanel.add(footerLabel);

    	    mainPanel.add(footerPanel, BorderLayout.SOUTH);

    	    frame.add(mainPanel);
    	    frame.setLocationRelativeTo(null);
    	    frame.setVisible(true);

        

        try {
        	 

        	validateButton.addActionListener(e -> {
        		String pin = new String(pinField.getPassword());
        	    
        	    // Create and show the loading frame
        	    JFrame loadingFrame = new JFrame();
        	    loadingFrame.setUndecorated(true); // No title bar
        	    loadingFrame.setSize(200, 100);
        	    loadingFrame.setLayout(new BorderLayout());
        	    loadingFrame.setLocationRelativeTo(frame); // Center it relative to the main frame

        	    JLabel loadingLabel = new JLabel("Validating...", SwingConstants.CENTER);
        	    loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        	    loadingLabel.setForeground(new Color(34, 139, 34));
        	    loadingFrame.add(loadingLabel, BorderLayout.CENTER);

        	    loadingFrame.setVisible(true);

        	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
        	        @Override
        	        protected Void doInBackground() throws Exception {
        	            Thread.sleep(1500); 
        	            return null;
        	        }

        	        @Override
        	        protected void done() {
        	            loadingFrame.dispose();

        	            try {
        	                String status = verifyPin(cad, pin);
        	                if (status.equals("true")) {
        	                    showTransactionOptions();
        	                    frame.dispose(); 
        	                } else {
        	                    if (status.equals("false")) {
        	                        JOptionPane.showMessageDialog(frame, "The PIN must be exactly 4 digits.", "Error", JOptionPane.ERROR_MESSAGE);
        	                    } else if (status.equals("false2")) {
        	                        JOptionPane.showMessageDialog(frame, "Code Pin Incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
        	                    } else if (status.equals("false3")) {
        	                        JOptionPane.showMessageDialog(frame, "Your card is blocked. Please contact customer service.", "Error", JOptionPane.ERROR_MESSAGE);
        	                        frame.dispose();
        	                    } else {
        	                        JOptionPane.showMessageDialog(frame, "Error");
        	                    }
        	                }
        	            } catch (Exception ex) {
        	                ex.printStackTrace();
        	            }
        	        }
        	    };

        	    worker.execute(); 
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

            byte[] pinBytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                pinBytes[i] = (byte) (pin.charAt(i) - '0'); 
            }

            apdu.setDataIn(pinBytes);

            logApduCommand(apdu);

            cad.exchangeApdu(apdu);
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
        JFrame transactionFrame = new JFrame("ATM Transaction Options");
        transactionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        transactionFrame.setSize(600, 300);
        transactionFrame.setUndecorated(true); 
        transactionFrame.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 4)); 

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

        JLabel optionsLabel = new JLabel("Choose Your Transaction", JLabel.CENTER);
        optionsLabel.setForeground(Color.WHITE);
        optionsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        optionsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10)); 
        buttonPanel.setOpaque(false);

        JButton withdrawButton = createFixedSizeButton("Withdraw Money", 220, 40);
        JButton depositButton = createFixedSizeButton("Deposit Money", 220, 40);
        JButton checkBalanceButton = createFixedSizeButton("Check Balance", 220, 40);
        JPanel withdrawPanel = createCenteredPanel(withdrawButton);
        JPanel depositPanel = createCenteredPanel(depositButton);
        JPanel checkBalancePanel = createCenteredPanel(checkBalanceButton);

        buttonPanel.add(withdrawPanel);
        buttonPanel.add(depositPanel);
        buttonPanel.add(checkBalancePanel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(200, 0, 0)); // Red background
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createLineBorder(new Color(150, 0, 0), 2)); // Dark red border
        exitButton.setPreferredSize(new Dimension(80, 30)); // Fixed size

        exitButton.addActionListener(e -> transactionFrame.dispose());

        bottomPanel.add(exitButton);

        mainPanel.add(optionsLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        transactionFrame.add(mainPanel);

        withdrawButton.addActionListener(e -> Afficher_frame_draw(INS_DRAW));
        depositButton.addActionListener(e -> Afficher_FRame_deposit(INS_DEPOSIT));
        checkBalanceButton.addActionListener(e -> Afficher_Frame_checkbalance(INS_CHECK_BALANCE));

        transactionFrame.setLocationRelativeTo(null);

        transactionFrame.setVisible(true);
    }

    private static JButton createFixedSizeButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 204)); 
        button.setPreferredSize(new Dimension(width, height));
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1)); 

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 153, 255)); 
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204)); 
            }
        });

        return button;
    }

    private static JPanel createCenteredPanel(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);
        panel.add(component);
        return panel;
    }

    
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
                errorMessage = "Deposit amount too high. The amount should not exceed 500 units.";
                break;
            case 0x6A82:
                errorMessage = "Invalid deposit amount. The amount must be a multiple of 10.";
                break;
            case 0x6A84:
                errorMessage = "Maximum balance exceeded. The balance should not exceed 10000 units.";
                break;
            case 0x6A86:
            	errorMessage = "Invalid deposit amount. The amount must be greater than 0."; 
            break ;    
            default:
                errorMessage = "Error during submission.";
                break;
        }
        JOptionPane.showMessageDialog(null, errorMessage, "Error during submission.", JOptionPane.ERROR_MESSAGE);}
    

    private static void Afficher_FRame_deposit(byte insDeposit) {
        // Créer la fenêtre principale
        JFrame depositFrame = new JFrame("Money deposit");
        depositFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        depositFrame.setSize(400, 300);
        depositFrame.setLocationRelativeTo(null); // Centrer la fenêtre à l'écran

        // Définir le fond de toute la fenêtre en bleu pastel
        depositFrame.getContentPane().setBackground(new Color(43, 84, 126));  // Bleu pastel

        // Créer un panneau principal avec GridBagLayout
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.setBackground(new Color(43, 84, 126)); // Fond bleu pastel pour le panneau

        depositFrame.setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titre
        JLabel titleLabel = new JLabel("Money deposit", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));  // Couleur bleu pastel pour le texte
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        backgroundPanel.add(titleLabel, gbc);

        // Ajouter le champ de saisie
        JLabel amountLabel = new JLabel("Enter the amount to deposit:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountLabel.setForeground(Color.WHITE);  // Texte en blanc pour le bon contraste
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(10);
        gbc.gridy = 2;
        backgroundPanel.add(amountField, gbc);

        // Ajouter les boutons avec des couleurs et bordures arrondies
        JButton validateButton = new JButton("Validate");
        validateButton.setBackground(new Color(60, 179, 113));  // Vert pastel
        validateButton.setForeground(Color.WHITE);
        validateButton.setFont(new Font("Arial", Font.BOLD, 14));
        validateButton.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
        validateButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Exit");
        cancelButton.setBackground(new Color(255, 69, 0));  // Rouge pastel
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 2));
        cancelButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);  // Rendre le panel transparent pour voir l'arrière-plan
        buttonPanel.add(validateButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // Actions des boutons
        validateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amountText = amountField.getText();
                try {
                    int amount = Integer.parseInt(amountText);
                    // Supposons que la méthode deposit retourne un booléen en fonction du succès du dépôt
                    if (deposit(cad, amount)) {
                        JOptionPane.showMessageDialog(depositFrame, "Successful deposit .");
                        		
                        depositFrame.dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(depositFrame, "Please enter a valid amount.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        cancelButton.addActionListener(e -> depositFrame.dispose());

        depositFrame.setVisible(true);
    }



    private static void Afficher_frame_draw(byte insDraw) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("ATM Simulator - Withdraw");

            mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            mainFrame.setSize(600, 300); // Increased size
            mainFrame.setLayout(new BorderLayout());
            mainFrame.setUndecorated(true);// Layout with padding
            mainFrame.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 4)); // ATM screen border
            mainFrame.setLocationRelativeTo(null);

            mainFrame.getContentPane().setBackground(new Color(43, 84, 126)); // Set background to blue

            // Set a title label with a bold and large font
            JLabel instructionLabel = new JLabel("Choose Withdrawal Amount", SwingConstants.CENTER);
            instructionLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bold and larger font
            instructionLabel.setForeground(Color.WHITE); // White text
            mainFrame.add(instructionLabel, BorderLayout.NORTH);

            // Create a panel with BorderLayout to place buttons on the borders
            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.setBackground(new Color(43, 84, 126));

            // Create the button arrays for left, right, top, and bottom
            String[] leftAmounts = {"10", "20", "50"};
            String[] rightAmounts = {"100", "Other"};

            // Method to handle button actions
            ActionListener buttonActionListener = e -> {
                JButton sourceButton = (JButton) e.getSource();
                String amountText = sourceButton.getText();

                if ("Other".equals(amountText)) {
                    createOtherAmountGUI(insDraw,mainFrame);
                } else {
                    try {
                        int amountInt = Integer.parseInt(amountText.replace("$", ""));
                        withdrawFunds(insDraw, amountInt);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

            // Panel for left-side buttons (vertical stack)
            JPanel leftPanel = new JPanel(new GridLayout(leftAmounts.length, 1, 5, 5)); // Added 5px gap between buttons
            for (String amount : leftAmounts) {
                JButton button = new JButton(amount);
                button.setPreferredSize(new Dimension(100, 40)); // Adjusted height for better fit
                button.setBackground(new Color(195, 224, 229)); // Set button color to #c3e0e5
                button.setForeground(new Color(0,48,96)); // Set text color to black
                button.setFont(new Font("Arial", Font.BOLD, 14)); // Improve button text font
                button.addActionListener(buttonActionListener);
                leftPanel.add(button);
            }

            // Panel for right-side buttons (vertical stack)
            JPanel rightPanel = new JPanel(new GridLayout(rightAmounts.length, 1, 5, 5)); // Added 5px gap between buttons
            for (String amount : rightAmounts) {
                JButton button = new JButton(amount);
                button.setPreferredSize(new Dimension(100, 40)); // Adjusted height for better fit
                button.setBackground(new Color(195, 224, 229)); // Set button color to #c3e0e5
                button.setForeground(new Color(0,48,96)); // Set text color to black
                button.setFont(new Font("Arial", Font.BOLD, 14)); // Improve button text font
                button.addActionListener(buttonActionListener);
                rightPanel.add(button);
            }
            
            /*//Cancel button
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(new Color(200, 0, 0)); // Red button
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setFocusPainted(false);
            cancelButton.setPreferredSize(new Dimension(120, 40));
            cancelButton.setFont(new Font("Arial", Font.BOLD, 14)); // Improved button font
            
         // Handle Cancel button click
            cancelButton.addActionListener(e -> mainFrame.dispose()); // Simply close the frame
          */

            // Cancel button
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(new Color(200, 0, 0)); // Red button
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setFocusPainted(false);
            cancelButton.setPreferredSize(new Dimension(120, 40)); // Fixed size
            cancelButton.setFont(new Font("Arial", Font.BOLD, 14)); // Improved button font

            // Handle Cancel button click
            cancelButton.addActionListener(e -> mainFrame.dispose()); // Simply close the frame

            // Panel to hold the Cancel button
            JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center alignment
            cancelPanel.setBackground(new Color(43, 84, 126)); // Match the background color
            cancelPanel.add(cancelButton);

            // Add the Cancel panel to the main frame
            mainFrame.add(cancelPanel, BorderLayout.SOUTH);

            // Set the panels to the border layout
            buttonPanel.add(leftPanel, BorderLayout.WEST);
            buttonPanel.add(rightPanel, BorderLayout.EAST);

            mainFrame.add(buttonPanel, BorderLayout.CENTER);
            //mainFrame.add(cancelButton,BorderLayout.AFTER_LAST_LINE);
            mainFrame.setVisible(true);
        });
    }

    // Method to retrieve balance from the card
    private static short checkBalance(CadT1Client cad) throws Exception {
        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = (byte) 0xB0; // CLA for ATM applet
        apdu.command[Apdu.INS] = 0x00;        // Command for checking balance
        apdu.command[Apdu.P1] = 0x00;
        apdu.command[Apdu.P2] = 0x00;
        apdu.setLe((byte) 2);                 // Expecting 2 bytes for balance

        cad.exchangeApdu(apdu);

        if (apdu.getStatus() == 0x9000) {
            return (short) ((apdu.dataOut[0] << 8) | (apdu.dataOut[1] & 0xFF)); // Combine bytes into a short
        } else {
            throw new Exception(String.format("Failed to check balance. Status: 0x%04X", apdu.getStatus()));
        }
    }

    private static void createOtherAmountGUI(byte insDraw, JFrame mainFrame) {

        mainFrame.dispose();
        JFrame otherFrame = new JFrame("Enter Custom Amount");
        otherFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        otherFrame.setSize(600, 300); // Larger size
        otherFrame.setLayout(new BorderLayout());
        otherFrame.setUndecorated(true);// Layout with padding
        otherFrame.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 4)); // ATM screen border
        otherFrame.setLocationRelativeTo(null); // Center the window on the screen
        //otherFrame.setResizable(false); // Prevent resizing

        otherFrame.setLayout(new GridLayout(4, 1, 10, 10)); // Adjusted for more buttons

        otherFrame.getContentPane().setBackground(new Color(43, 84, 126));

        // Set a title label with a bold and large font
        JLabel instructionLabel = new JLabel("Choose Withdrawal Amount", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bold and larger font
        instructionLabel.setForeground(Color.WHITE); // White text
        otherFrame.add(instructionLabel, BorderLayout.NORTH);// Set background color to blue

        // Create and customize the text field
        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(350, 40)); // Increased height for text field
        amountField.setFont(new Font("Arial", Font.PLAIN, 18));
        amountField.setHorizontalAlignment(JTextField.CENTER);// Larger font for the text field

        // Create the error label with red color
        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        //errorLabel.setPreferredSize(new Dimension(350, 30));

        // Panel for the text field and error label
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(new Color(43, 84, 126));
        centerPanel.add(amountField, BorderLayout.CENTER);
        centerPanel.add(errorLabel, BorderLayout.SOUTH);

        // Create Validate and Cancel buttons
        JButton validateButton = new JButton("Validate");
        validateButton.setPreferredSize(new Dimension(100, 50)); // Fixed size for buttons
        validateButton.setBackground(new Color(34, 139, 34)); // Set validate button color to green
        validateButton.setForeground(Color.WHITE); // Set text color to white
        validateButton.setFont(new Font("Arial", Font.BOLD, 14)); // Improved button font

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 50)); // Fixed size for buttons
        cancelButton.setBackground(new Color(200, 0, 0)); // Set cancel button color to red
        cancelButton.setForeground(Color.WHITE); // Set text color to white
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14)); // Improved button font

        // Bank Name and Logo
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon(MaClasse.class.getResource("bank_logo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledLogo));

        JLabel bankNameLabel = new JLabel("JC Bank", SwingConstants.LEFT);
        bankNameLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        bankNameLabel.setForeground(Color.WHITE);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setBackground(new Color(43, 84, 126));
        logoPanel.add(logoLabel);
        logoPanel.add(bankNameLabel);

        otherFrame.add(logoPanel, BorderLayout.WEST);

        // Handle Validate button click
        validateButton.addActionListener(e -> {
            String input = amountField.getText();
            try {
                int amount = Integer.parseInt(input);
                short currentBalance = checkBalance(cad);
                if (amount <= 0) {
                    errorLabel.setText("Amount must be greater than 0.");
                }
                else if (currentBalance - amount < 10) {
                    JOptionPane.showMessageDialog(null, "Error: Insufficient funds.");
                    return;}

                else if (amount > 500) {
                    JOptionPane.showMessageDialog(null, "Error: The amount cannot exceed 500.");
                    return;
                }

                else if (amount % 10 != 0) {
                    JOptionPane.showMessageDialog(null, "Error: The amount must be a multiple of 10.");
                    return;
                } else {
                    withdrawFunds(insDraw, amount);
                    otherFrame.dispose();
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid amount. Please enter a number.");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        // Panel for buttons (to align them horizontally)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Center buttons with spacing
        buttonPanel.setBackground(new Color(43, 84, 126)); // Match background color
        buttonPanel.add(validateButton);
        buttonPanel.add(cancelButton);

        // Handle Cancel button click
        cancelButton.addActionListener(e -> otherFrame.dispose()); // Simply close the frame

           /*otherFrame.add(amountField);
           otherFrame.add(buttonPanel);
           otherFrame.add(errorLabel);*/

        otherFrame.add(centerPanel, BorderLayout.CENTER); // Add the text field and error label to the center
        otherFrame.add(buttonPanel, BorderLayout.SOUTH);

        otherFrame.setVisible(true);
    }


    private static void withdrawFunds(byte insDraw, int amount) {
        try {
            short currentBalance = checkBalance(cad);
            if (currentBalance - amount < 10) {
                JOptionPane.showMessageDialog(null, "Error: Insufficient funds.");
                return;
            } }catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

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
class BackgroundPanel extends JPanel {
	private Image backgroundImage; 
	public BackgroundPanel(Image backgroundImage) { 
		this.backgroundImage = backgroundImage; 
		}
	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g); g.drawImage(backgroundImage, 0, 0, this); }
}
