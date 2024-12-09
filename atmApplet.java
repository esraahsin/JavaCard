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
    public static final byte INS_UPDATE_PIN = 0x04;

    private static final short MAX_BALANCE = 10000;
    private static final short MIN_BALANCE = 0;

    private short accountBalance;
    private byte[] encryptedPin;
    private Cipher cipher;
    private AESKey aesKey;

    private AtmApplet() {
        accountBalance = 0;

        // Initialize a 16-byte AES key (128-bit)
        aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        byte[] keyData = new byte[16];
        Util.arrayFillNonAtomic(keyData, (short) 0, (short) keyData.length, (byte) 0x01); // Example key data
        aesKey.setKey(keyData, (short) 0);

        cipher = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);

        // Encrypt the default PIN "1234" (padded to 16 bytes)
        byte[] defaultPin = new byte[16];
        defaultPin[0] = 1; defaultPin[1] = 2; defaultPin[2] = 3; defaultPin[3] = 4;
        encryptedPin = new byte[16];
        encryptPin(defaultPin, encryptedPin);
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

        if (byteRead != 16) { // PIN must be 16 bytes after padding
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        apdu.setIncomingAndReceive();
        byte[] receivedPin = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
        decryptPin(encryptedPin, receivedPin);

        if (Util.arrayCompare(buffer, ISO7816.OFFSET_CDATA, receivedPin, (short) 0, (short) 16) != 0) {
            ISOException.throwIt((short) 0x6300); // PIN verification failed
        }
    }

    private void updatePin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte byteRead = (byte) (buffer[ISO7816.OFFSET_LC] & 0xFF);

        if (byteRead != 16) { // New PIN must be 16 bytes
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        apdu.setIncomingAndReceive();
        encryptPin(buffer, ISO7816.OFFSET_CDATA, encryptedPin);
    }

    private void checkBalance(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) 2);
        Util.setShort(buffer, (short) 0, accountBalance);
        apdu.sendBytes((short) 0, (short) 2);
    }

    private void depositFunds(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        apdu.setIncomingAndReceive();

        short depositAmount = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
        if ((short) (accountBalance + depositAmount) > MAX_BALANCE) {
            ISOException.throwIt((short) 0x6A84);
        }

        accountBalance += depositAmount;
    }

    private void withdrawFunds(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        apdu.setIncomingAndReceive();

        short withdrawAmount = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
        if ((short) (accountBalance - withdrawAmount) < MIN_BALANCE) {
            ISOException.throwIt((short) 0x6A85);
        }

        accountBalance -= withdrawAmount;
    }

    private void encryptPin(byte[] inputPin, short offset, byte[] outputEncryptedPin) {
        cipher.init(aesKey, Cipher.MODE_ENCRYPT);
        cipher.doFinal(inputPin, offset, (short) 16, outputEncryptedPin, (short) 0);
    }

    private void decryptPin(byte[] encryptedPin, byte[] outputPin) {
        cipher.init(aesKey, Cipher.MODE_DECRYPT);
        cipher.doFinal(encryptedPin, (short) 0, (short) 16, outputPin, (short) 0);
    }
}
