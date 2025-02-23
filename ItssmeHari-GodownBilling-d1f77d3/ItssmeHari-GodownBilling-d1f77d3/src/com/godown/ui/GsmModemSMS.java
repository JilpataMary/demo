package com.godown.ui;

import gnu.io.*;
import gnu.io.CommPortIdentifier;
import java.io.OutputStream;
import java.util.Enumeration;

public class GsmModemSMS {
    private static final String PORT = "COM6"; // Change as per your setup
    private static final int BAUD_RATE = 9600;

    public static void sendSMS(String phoneNumber, String message) {
        try {
            CommPortIdentifier portId = null;
            Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();

            while (portList.hasMoreElements()) {
                CommPortIdentifier currentPortId = (CommPortIdentifier) portList.nextElement();
                if (currentPortId.getName().equals(PORT)) {
                    portId = currentPortId;
                    break;
                }
            }

            if (portId == null) {
                System.out.println("Port not found!");
                return;
            }

            SerialPort serialPort = (SerialPort) portId.open("SMSApp", 2000);
            serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            OutputStream outputStream = serialPort.getOutputStream();
            sendATCommand(outputStream, "AT");
            sendATCommand(outputStream, "AT+CMGF=1"); // Text mode
            sendATCommand(outputStream, "AT+CMGS=\"" + phoneNumber + "\""); // Dynamic phone number
            sendATCommand(outputStream, message + "\u001A"); // \u001A = Ctrl+Z to send

            outputStream.close();
            serialPort.close();
            System.out.println("SMS Sent to: " + phoneNumber);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendATCommand(OutputStream outputStream, String command) throws Exception {
        outputStream.write((command + "\r\n").getBytes());
        Thread.sleep(1000);
    }
}
