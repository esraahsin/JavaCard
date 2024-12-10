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


	private static Object Afficher_FRame_deposit(byte insDeposit) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void Afficher_frame_draw(byte insDraw) {
		// TODO Auto-generated method stub
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
