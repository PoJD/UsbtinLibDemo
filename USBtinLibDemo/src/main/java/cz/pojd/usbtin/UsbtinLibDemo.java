package cz.pojd.usbtin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.fischl.usbtin.CANMessage;
import de.fischl.usbtin.CANMessageListener;
import de.fischl.usbtin.USBtin;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class UsbtinLibDemo {
	public static void main(String[] args) throws Exception {
		testUsingNrJavaSerial();
		//testUsingUsbTinLib();
	}

	private static void testUsingNrJavaSerial() throws Exception {

		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/ttyACM2");
		SerialPort serialPort = portIdentifier.open("test", 2000);
        serialPort.setSerialPortParams(115200, 8, 1, 0);
        serialPort.enableReceiveThreshold(1);
        serialPort.disableReceiveTimeout();
        OutputStreamWriter serialOutput = new OutputStreamWriter(serialPort.getOutputStream(), "US-ASCII");
        BufferedReader serialInput = new BufferedReader(new InputStreamReader(serialPort.getInputStream(), "US-ASCII"));

        serialPort.addEventListener(new SerialPortEventListener() {
			public void serialEvent(SerialPortEvent ev) {
				System.out.println("received stuff" + ev);
			}
        });
        serialPort.notifyOnDataAvailable(true);

        serialOutput.write("test");

		System.in.read();

		serialPort.close();
	}

	private static void testUsingUsbTinLib() throws Exception {
		USBtin usbtin = new USBtin();
		usbtin.connect("/dev/ttyACM2");

		usbtin.addMessageListener(new CANMessageListener() {
			public void receiveCANMessage(CANMessage arg0) {
				System.out.println("Received traffic. canID=" + arg0.getId() + ". data: " + arg0.getData()[0]);
			}
		});

		usbtin.openCANChannel(10000, USBtin.OpenMode.ACTIVE);

		usbtin.send(new CANMessage(0x01, new byte[] { 0x07 }));
		usbtin.send(new CANMessage(0x01, new byte[] { 0x07 }));

		Thread.sleep(1000);
		usbtin.closeCANChannel();
		usbtin.disconnect();
	}
}
