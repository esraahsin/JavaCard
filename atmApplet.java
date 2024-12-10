package packageatm;

import javacard.framework.*;

public class AtmApplet extends Applet {
    public static final byte CLA_ATM_APPLET = (byte) 0xB0;

    // Instruction codes
    public static final byte INS_CHECK_BALANCE = 0x00;
    public static final byte INS_DEPOSIT_FUNDS = 0x01;
    public static final byte INS_WITHDRAW_FUNDS = 0x02;
    public static final byte INS_VERIFY_PIN = 0x03;
    public static final byte INS_UPDATE_PIN = 0x04;

    private static final short MAX_BALANCE = 10000;
    private static final short MIN_BALANCE = 0;

    private short accountBalance;
    private byte[] defaultPin; // PIN par défaut (sans cryptage)

    // Private constructor
    private AtmApplet() {
        accountBalance = 0;

        // PIN par défaut "1234"
        defaultPin = JCSystem.makeTransientByteArray((short) 4, JCSystem.CLEAR_ON_RESET);
        defaultPin[0] = 1;
        defaultPin[1] = 2;
        defaultPin[2] = 3;
        defaultPin[3] = 4;
    }

    // Méthode d'installation de l'applet
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new AtmApplet().register();
    }

    public void process(APDU apdu) {
        byte[] buffer = apdu.getBuffer();

        if (selectingApplet()) {
            return;
        }

        if (buffer[ISO7816.OFFSET_CLA] != CLA_ATM_APPLET) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        switch (buffer[ISO7816.OFFSET_INS]) {
            case INS_VERIFY_PIN:
                verifyPin(apdu);
                break;

            case INS_UPDATE_PIN:
                updatePin(apdu);
                break;

            case INS_CHECK_BALANCE:
                checkBalance(apdu);
                break;

            case INS_DEPOSIT_FUNDS:
                depositFunds(apdu);
                break;

            case INS_WITHDRAW_FUNDS:
                withdrawFunds(apdu);
                break;

            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private void verifyPin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte byteRead = (byte) (buffer[ISO7816.OFFSET_LC] & 0xFF);

        // Journalisation de la longueur du PIN
        if (byteRead != 4) { // PIN doit être de 4 octets
            ISOException.throwIt((short) 0x63C1); // Longueur incorrecte
        }

        apdu.setIncomingAndReceive();

        // Récupération et copie du PIN reçu
        byte[] receivedPin = JCSystem.makeTransientByteArray((short) 4, JCSystem.CLEAR_ON_RESET);
        Util.arrayCopy(buffer, ISO7816.OFFSET_CDATA, receivedPin, (short) 0, (short) 4);

        // Journalisation : Comparaison du PIN
        if (Util.arrayCompare(defaultPin, (short) 0, receivedPin, (short) 0, (short) 4) != 0) {
            ISOException.throwIt((short) 0x6301); // Vérification échouée
        }

        // Journalisation : PIN correct
        ISOException.throwIt((short) 0x9000); // Succès
    }

    private void updatePin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte byteRead = (byte) (buffer[ISO7816.OFFSET_LC] & 0xFF);

        if (byteRead != 4) { // Nouveau PIN doit être de 4 octets
            ISOException.throwIt((short) 0x63C2); // Longueur incorrecte pour mise à jour
        }

        apdu.setIncomingAndReceive();
        Util.arrayCopy(buffer, ISO7816.OFFSET_CDATA, defaultPin, (short) 0, (short) 4);

        // Journalisation : PIN mis à jour
        ISOException.throwIt((short) 0x9001); // Succès de mise à jour
    }

    private void checkBalance(APDU apdu) {
        byte[] buffer = apdu.getBuffer();

        // Encode the account balance into two bytes (high byte and low byte)
        buffer[0] = (byte) (accountBalance >> 8); // High byte
        buffer[1] = (byte) (accountBalance & 0xFF); // Low byte

        // Prepare the APDU response
        apdu.setOutgoing();
        apdu.setOutgoingLength((byte) 2);
        apdu.sendBytes((short) 0, (short) 2);
    }



    private void depositFunds(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        apdu.setIncomingAndReceive();

        short depositAmount = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
        if ((short) (accountBalance + depositAmount) > MAX_BALANCE) {
            ISOException.throwIt((short) 0x6A84); // Balance maximale dépassée
        }

        accountBalance += depositAmount;

        // Journalisation : Dépôt effectué
        ISOException.throwIt((short) 0x9003); // Succès du dépôt
    }

    private void withdrawFunds(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        apdu.setIncomingAndReceive();

        short withdrawAmount = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
        if ((short) (accountBalance - withdrawAmount) < MIN_BALANCE) {
            ISOException.throwIt((short) 0x6A85); // Fonds insuffisants
        }

        accountBalance -= withdrawAmount;

        // Journalisation : Retrait effectué
        ISOException.throwIt((short) 0x9004); // Succès du retrait
    }
}
