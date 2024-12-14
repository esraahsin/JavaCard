package packageatm;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;

public class AtmApplet extends Applet {
    public static final byte CLA_ATM_APPLET = (byte) 0xB0;

    // Instruction codes
    public static final byte INS_CHECK_BALANCE = 0x00;
    public static final byte INS_DEPOSIT_FUNDS = 0x01;
    public static final byte INS_WITHDRAW_FUNDS = 0x02;
    public static final byte INS_VERIFY_PIN = 0x03;

    private static final short MAX_BALANCE = 10000;
    private static final short MIN_BALANCE = 10;
    private static final byte MAX_ATTEMPTS = 3; // Maximum allowed attempts
    private byte failedAttempts; // Counter for failed attempts

    private short accountBalance;
    private byte[] encryptedPin;
    private Cipher cipher;
    private AESKey aesKey;

    private AtmApplet() {
        accountBalance = 10;
        aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        cipher = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);
        // Set a default AES key 
        byte[] defaultKey = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
                             (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F, (byte) 0x10};
        aesKey.setKey(defaultKey, (short) 0);
        encryptedPin = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
        byte[] defaultPin = {1, 2, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // Pad to 16 bytes
        cipher.init(aesKey, Cipher.MODE_ENCRYPT);
        cipher.doFinal(defaultPin, (short) 0, (short) 16, encryptedPin, (short) 0);
    }

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

        if (byteRead != 4) { // PIN must be 4 bytes
            ISOException.throwIt((short) 0x63C1); // Incorrect length
        }

        apdu.setIncomingAndReceive();

        // Decrypt and compare the received PIN
        byte[] receivedPin = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
        byte[] paddedPin = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
        Util.arrayCopy(buffer, ISO7816.OFFSET_CDATA, paddedPin, (short) 0, (short) 4);

        cipher.init(aesKey, Cipher.MODE_ENCRYPT);
        cipher.doFinal(paddedPin, (short) 0, (short) 16, receivedPin, (short) 0);

        if (Util.arrayCompare(encryptedPin, (short) 0, receivedPin, (short) 0, (short) 16) != 0) {
            failedAttempts++;
            if (failedAttempts >= MAX_ATTEMPTS) {
                ISOException.throwIt((short) 0x6A88); // Card blocked after too many failed attempts
            }
            ISOException.throwIt((short) 0x6301); // Verification failed
        }

        failedAttempts = 0; // Reset on success
        ISOException.throwIt((short) 0x9000); // Success
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
        
        if (depositAmount <= 0) {
        	ISOException.throwIt((short) 0x6A86);} // Montant de dépôt invalide (doit être > 0)
        
        if (depositAmount > 500) {
            ISOException.throwIt((short) 0x6A83); // Montant de dépôt trop élevé
        }

        if (depositAmount % 10 != 0) {
            ISOException.throwIt((short) 0x6A82); // Montant de dépôt invalide
        }

        if ((short) (accountBalance + depositAmount) > 10000) {
            ISOException.throwIt((short) 0x6A84); // Balance maximale dépassée
        }

        accountBalance += depositAmount;

        ISOException.throwIt((short) 0x9000); // Succès du dépôt
    }
    private void withdrawFunds(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        apdu.setIncomingAndReceive();

        short withdrawAmount = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
        if ((short) (accountBalance - withdrawAmount) < MIN_BALANCE) {
            ISOException.throwIt((short) 0x6A85); // Insufficient funds
        }

        accountBalance -= withdrawAmount;

        ISOException.throwIt((short) 0x9000); // Withdrawal success
    }
}
