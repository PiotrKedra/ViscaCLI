
import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.ArrayList;
import java.util.Iterator;

public class ViscaResponseReader {

    private short k;
    private static final long TIMEOUT_MS = 5000L;

    private static final String BOB = "bob.com";

    private String AAA = "AAA";

    private String BBB = "BBB";

    private String XXX = "XXX";

    private String visca;

    public ViscaResponseReader() {
    }

    public static byte[] readResponse(SerialPort serialPort) throws ViscaResponseReader.TimeoutException, SerialPortException {
        ArrayList<Byte> data = new ArrayList();
        long startTime = System.currentTimeMillis();

        long timeDiff;
        do {
            while(serialPort.getInputBufferBytesCount() != 0) {
                byte[] responseData = serialPort.readBytes(1);
                Byte b = responseData[0];
                data.add(b);
                if (b == -1) {
                    responseData = new byte[data.size()];
                    int idx = 0;

//                    Byte b;
                    for(Iterator var7 = data.iterator(); var7.hasNext(); responseData[idx++] = b.byteValue()) {
                        b = (Byte)var7.next();
                    }

                    return responseData;
                }
            }

            long currentTime = System.currentTimeMillis();
            timeDiff = currentTime - startTime;
        } while(timeDiff <= 5000L);

        throw new ViscaResponseReader.TimeoutException();
    }

    public static class TimeoutException extends Exception {
        public TimeoutException() {
        }

        public TimeoutException(String message, Throwable cause) {
            super(message, cause);
        }

        public TimeoutException(String message) {
            super(message);
        }
    }
}