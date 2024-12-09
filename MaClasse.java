import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class MaClasse {
    // Codes APDU pour interagir avec l'applet ATM
    public static final byte CLA_ATM_APPLET = (byte) 0xB0;
    public static final byte INS_CHECK_BALANCE = 0x00;
    public static final byte INS_DEPOSIT_FUNDS = 0x01;
    public static final byte INS_WITHDRAW_FUNDS = 0x02;
    public static final byte INS_VERIFY_PIN = 0x03;
    public static final byte INS_UPDATE_PIN = 0x04;

    public static void main(String[] args) {
        CadT1Client cad = null;

        try {
            // Connexion au simulateur JavaCard
            Socket socket = new Socket("localhost", 9025);
            BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
            cad = new CadT1Client(input, output);
            cad.powerUp();

            // Sélection de l'applet ATM
            Apdu apdu = new Apdu();
            apdu.command[Apdu.CLA] = 0x00;
            apdu.command[Apdu.INS] = (byte) 0xA4;
            apdu.command[Apdu.P1] = 0x04;
            apdu.command[Apdu.P2] = 0x00;
            byte[] appletAID = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00 };
            apdu.setDataIn(appletAID);
            cad.exchangeApdu(apdu);

            if (apdu.getStatus() != 0x9000) {
                throw new Exception("Erreur : impossible de sélectionner l'applet.");
            }

            // Menu principal
            Scanner scanner = new Scanner(System.in);
            boolean continuer = true;

            while (continuer) {
                System.out.println("\nATM - Application Client");
                System.out.println("1. Vérifier le solde");
                System.out.println("2. Déposer des fonds");
                System.out.println("3. Retirer des fonds");
                System.out.println("4. Vérifier le PIN");
                System.out.println("5. Mettre à jour le PIN");
                System.out.println("6. Quitter");
                System.out.print("Choisissez une option : ");
                int choix = scanner.nextInt();

                switch (choix) {
                    case 1:
                        checkBalance(cad);
                        break;
                    case 2:
                        depositFunds(cad, scanner);
                        break;
                    case 3:
                        withdrawFunds(cad, scanner);
                        break;
                    case 4:
                        verifyPin(cad, scanner);
                        break;
                    case 5:
                        updatePin(cad, scanner);
                        break;
                    case 6:
                        continuer = false;
                        break;
                    default:
                        System.out.println("Choix invalide.");
                }
            }

            // Mise hors tension de la carte
            cad.powerDown();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkBalance(CadT1Client cad) throws Exception {
        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
        apdu.command[Apdu.INS] = INS_CHECK_BALANCE;
        apdu.command[Apdu.P1] = 0x00;
        apdu.command[Apdu.P2] = 0x00;
        apdu.setLe((byte) 2);
        cad.exchangeApdu(apdu);

        if (apdu.getStatus() == 0x9000) {
            short balance = (short) ((apdu.dataOut[0] << 8) | (apdu.dataOut[1] & 0xFF));
            System.out.println("Solde du compte : " + balance + " unités.");
        } else {
            System.out.println("Erreur lors de la récupération du solde.");
        }
    }

    private static void depositFunds(CadT1Client cad, Scanner scanner) throws Exception {
        System.out.print("Montant à déposer : ");
        short montant = scanner.nextShort();

        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
        apdu.command[Apdu.INS] = INS_DEPOSIT_FUNDS;
        apdu.command[Apdu.P1] = 0x00;
        apdu.command[Apdu.P2] = 0x00;
        apdu.setDataIn(new byte[]{(byte) (montant >> 8), (byte) montant});
        cad.exchangeApdu(apdu);

        if (apdu.getStatus() == 0x9000) {
            System.out.println("Dépôt effectué avec succès.");
        } else {
            System.out.println("Erreur lors du dépôt.");
        }
    }

    private static void withdrawFunds(CadT1Client cad, Scanner scanner) throws Exception {
        System.out.print("Montant à retirer : ");
        short montant = scanner.nextShort();

        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
        apdu.command[Apdu.INS] = INS_WITHDRAW_FUNDS;
        apdu.command[Apdu.P1] = 0x00;
        apdu.command[Apdu.P2] = 0x00;
        apdu.setDataIn(new byte[]{(byte) (montant >> 8), (byte) montant});
        cad.exchangeApdu(apdu);

        if (apdu.getStatus() == 0x9000) {
            System.out.println("Retrait effectué avec succès.");
        } else {
            System.out.println("Erreur lors du retrait.");
        }
    }

    private static void verifyPin(CadT1Client cad, Scanner scanner) throws Exception {
        System.out.print("Saisissez le PIN (16 caractères) : ");
        String pin = scanner.next();

        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
        apdu.command[Apdu.INS] = INS_VERIFY_PIN;
        apdu.command[Apdu.P1] = 0x00;
        apdu.command[Apdu.P2] = 0x00;

        byte[] pinBytes = Arrays.copyOf(pin.getBytes(), 16);
        apdu.setDataIn(pinBytes);
        cad.exchangeApdu(apdu);

        if (apdu.getStatus() == 0x9000) {
            System.out.println("PIN vérifié avec succès.");
        } else {
            System.out.println("Échec de la vérification du PIN.");
        }
    }

    private static void updatePin(CadT1Client cad, Scanner scanner) throws Exception {
        System.out.print("Saisissez le nouveau PIN (16 caractères) : ");
        String newPin = scanner.next();

        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_ATM_APPLET;
        apdu.command[Apdu.INS] = INS_UPDATE_PIN;
        apdu.command[Apdu.P1] = 0x00;
        apdu.command[Apdu.P2] = 0x00;

        byte[] newPinBytes = Arrays.copyOf(newPin.getBytes(), 16);
        apdu.setDataIn(newPinBytes);
        cad.exchangeApdu(apdu);

        if (apdu.getStatus() == 0x9000) {
            System.out.println("PIN mis à jour avec succès.");
        } else {
            System.out.println("Erreur lors de la mise à jour du PIN.");
        }
    }
}
