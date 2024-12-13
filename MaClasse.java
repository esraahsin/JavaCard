package packageclient;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;

import javax.swing.*;
import java.awt.*;
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
        JFrame frame = new JFrame("ATM Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(4, 1));

        JTextField pinField = new JTextField();
        JButton verifyPinButton = new JButton("Vérifier le PIN");

        frame.add(new JLabel("Veuillez entrer votre code PIN (4 caractères) :"));
        frame.add(pinField);
        frame.add(verifyPinButton);

        frame.setVisible(true);

        try {
            Socket socket = new Socket("localhost", 9025);
            BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
            cad = new CadT1Client(input, output);
            cad.powerUp();

            verifyPinButton.addActionListener(e -> {
                String pin = pinField.getText();
                try {
                    if (verifyPin(cad, pin)) {
                        frame.dispose(); // Fermer la fenêtre actuelle
                        showTransactionOptions(); // Ouvrir la nouvelle fenêtre avec les options de transaction
                    } else {
                        JOptionPane.showMessageDialog(frame, "Code PIN incorrect.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean verifyPin(CadT1Client cad, String pin) throws Exception {
        if (!pin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(null, "Le PIN doit contenir exactement 4 chiffres.");
            return false;
        }

        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
        apdu.command[Apdu.INS] = INS_VERIFY_PIN;
        apdu.command[Apdu.P1] = 0x00;
        apdu.command[Apdu.P2] = 0x00;

        // Convertir le PIN en valeurs décimales avant d'envoyer
        byte[] pinBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            pinBytes[i] = (byte) (pin.charAt(i) - '0'); // Convertit chaque caractère ASCII en valeur décimale
        }
        apdu.setDataIn(pinBytes);

        // Journaliser la commande APDU envoyée
        logApduCommand(apdu);

        cad.exchangeApdu(apdu);

        // Journaliser la réponse APDU reçue
        logApduResponse(apdu);

        return apdu.getStatus() == 0x9000;
    }

    private static void showTransactionOptions() {
        JFrame transactionFrame = new JFrame("Options de Transaction");
        transactionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        transactionFrame.setSize(400, 300);
        transactionFrame.setLayout(new GridLayout(4, 1));

        JButton drawButton = new JButton("Retirer de l'argent");
        JButton depositButton = new JButton("Déposer de l'argent");
        JButton checkBalanceButton = new JButton("Vérifier le solde");

        transactionFrame.add(drawButton);
        transactionFrame.add(depositButton);
        transactionFrame.add(checkBalanceButton);

        drawButton.addActionListener(e -> Afficher_frame_draw(INS_DRAW));
        depositButton.addActionListener(e -> Afficher_FRame_deposit(INS_DEPOSIT));
        checkBalanceButton.addActionListener(e -> Afficher_Frame_checkbalance(INS_CHECK_BALANCE));

        transactionFrame.setVisible(true);
    }

    private static Object Afficher_Frame_checkbalance(byte insCheckBalance) {
        try {
            Apdu apdu = new Apdu();
            apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
            apdu.command[Apdu.INS] = insCheckBalance;
            apdu.command[Apdu.P1] = 0x00;
            apdu.command[Apdu.P2] = 0x00;
            // Journaliser la commande APDU envoyée
            logApduCommand(apdu);

            cad.exchangeApdu(apdu);

            // Journaliser la réponse APDU reçue
            logApduResponse(apdu);

            if (apdu.getStatus() == 0x9000) {
                byte[] balanceBytes = apdu.getDataOut();
                int balance = ((balanceBytes[0] & 0xFF) << 8) | (balanceBytes[1] & 0xFF);
                JOptionPane.showMessageDialog(null, "Solde actuel: " + balance + " unités.");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de la vérification du solde.");
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
