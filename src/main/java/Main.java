import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Main {

    private static SerialPort serialPort;

    private static Map<String, byte[]> commands;

    static {
        byte[] up = {1,6,1,5,5,3,1};
        byte[] down = {1,6,1,5,5,3,2};
        byte[] left = {1,6,1,5,5,1,3};
        byte[] right = {1,6,1,5,5,2,3};
        commands = new HashMap<String, byte[]>();
        commands.put("up", up);
        commands.put("down", down);
        commands.put("left", left);
        commands.put("right", right);
    }

    public static void main(String[] args) {

        ViscaResponseReader responseReader = new ViscaResponseReader();
        setUpSerial();

        while (true){
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            String comand = null;
            try {
                comand = reader.readLine();
                String[] comands = comand.split(" ");
                for (int i = 0; i < comands.length; i++) {
                    byte[] data = commands.get(comands[i]);
                    setSpeed(comands, data);
                    sendCommand(data);
                    for (byte b : ViscaResponseReader.readResponse(serialPort)) {
                        System.out.print(b);
                    }
                    System.out.println();
                    sleep(5000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SerialPortException e) {
                e.printStackTrace();
            } catch (ViscaResponseReader.TimeoutException e) {
                e.printStackTrace();
            }
        }

    }

    private static void setSpeed(String[] comands, byte[] data) {
        try {
            data[3] = Byte.valueOf(comands[comands.length - 1]);
            data[4] = Byte.valueOf(comands[comands.length - 1]);
        }catch (NumberFormatException exp){
            System.out.println("Default speed 5");
        }catch (NullPointerException exp){
            System.out.println("Default speed 5");
        }
    }

    static void setUpSerial(){
        try {
            serialPort = new SerialPort("COM5");
            serialPort.openPort();
            serialPort.setParams(9600, 8,1,0);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

    }

    static void sendCommand(byte[] comadn){

        byte[] data = createData(comadn, (byte) 0, (byte) 1);
        try {
            serialPort.writeBytes(data);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

    }

    private static byte[] createData(byte[] comadn, byte sourceAdr, byte destinationAdr) {
        byte[] data = new byte[comadn.length + 1 + 1];
        byte head = (byte)( 128 | (sourceAdr << 4) |  destinationAdr);
        byte tail = -1;
        System.arraycopy(comadn, 0 , data,1, comadn.length);
        data[0]=head;
        data[comadn.length + 1] = tail;
        StringBuilder builder = new StringBuilder();
        for(byte b: comadn){
            builder.append(String.format("%02X ", b));
        }
        System.out.println(builder.toString());
        return data;
    }
}
