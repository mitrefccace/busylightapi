
package org.mitre.busylightapi;

import org.hid4java.HidDevice;
import org.hid4java.HidException;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.HidServicesSpecification;
import org.hid4java.ScanMode;
import org.hid4java.event.HidServicesEvent;

public class BusyLightAPI implements HidServicesListener {

	//vendor IDs
	public static enum Vendor { PLENOM };
	private static int[] vendors = new int[]{ 0x27BB };

	//product IDs
	public static enum Product { PRODUCT_OMEGA_ID, PRODUCT_ALPHA_ID, PRODUCT_UC_ID, PRODUCT_KUANDO_BOX_ID, PRODUCT_BOOTLOADER_ID}; 
	private static int[] products = new int[]{ 0x3BCD, 0x3BCA, 0x3BCB, 0x3BCC, 0x3BC0 };

	//colors
	public static enum BLColor { RED, GREEN, BLUE, YELLOW, PINK, AQUA, WHITE };
	private static short[][] colors = new short[][] {
		{0x50, 0x00, 0x00},
		{0x00, 0x50, 0x00},
		{0x00, 0x00, 0x50},
		{0x50, 0x50, 0x00},
		{0x50, 0x00, 0x50},
		{0x00, 0x50, 0x50},
		{0x50, 0x50, 0x50}
	};

	//sounds
	public static enum Ringtone { TONE_RISING, TONE_PHONE, TONE_SIMON, TONE_ALTERNATIVE, TONE_CLASSIC, TONE_ALIEN, TONE_OFFICE, TONE_LIVEWIRE, TONE_OLD, TONE_TRON, TONE_DISCO } 
	public static short[] ringtones = new short[]{ 0b10100011, 0b11000011,0b10010011, 0b10001011, 0b10110011, 0b10011011 , 0b10111011, 0b11101011, 0b11001011, 0b11010011, 0b10101011 };	

	private static final int PACKET_LENGTH = 64;

	private HidServices hidServices;
	private HidDevice hidDevice;
	private boolean bSound = false;
	private Ringtone ringTone = Ringtone.TONE_RISING; //default
	private short volume = 3;

	public BusyLightAPI() {
		initHidServices();
	}

	public static void main(String[] args) throws HidException, InterruptedException {

		BusyLightAPI light = new BusyLightAPI(); //my light
		light.detectBusyLight();
		light.initDevice(Vendor.PLENOM, Product.PRODUCT_OMEGA_ID, null);

		light.stop();
		//light.steadyColor(Color.BLUE);

		Thread.sleep(26000);


		light.shutdown();
		System.out.println("done.");
	}

	/* return the vendor and product indexes for the detected BusyLight device */
	public int[] detectBusyLight() {
		int[] ret = new int[2];
		if (hidServices != null) {
			for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
				if (hidDevice.getProduct() != null && hidDevice.getProduct().equalsIgnoreCase("busylight")) {
					ret[0] = getVendorIndex(hidDevice.getVendorId());
					ret[1] = getProductIndex(hidDevice.getProductId());
				}

			}
		}
		return ret;
	}

	public int getVendorIndex(int val) {
		int ret = -1;
		for (int i=0; i < vendors.length; i++) {
			if (vendors[i] == val) {
				ret = i;
				break;
			}
		}
		return ret;
	}

	public int getProductIndex(int val) {
		int ret = -1;
		for (int i=0; i < products.length; i++) {
			if (products[i] == val) {
				ret = i;
				break;
			}
		}
		return ret;
	}	

	public void steadyColor(BLColor c) {

		if (hidDevice == null) {
			System.err.println("Error- HID device is null");
			return;
		}

		// Ensure device is open after an attach/detach event
		if (!hidDevice.isOpen()) {
			hidDevice.open();
		}

		short soundByte = 0x80; //off
		if (bSound) {
			soundByte = ringtones[ringTone.ordinal()];
			//set volume
			soundByte = (short) ((soundByte & 0xF8) + volume);
		}

		short[] thecolor = colors[c.ordinal()]; 
		short[] message = new short[]{
				0x11, 0x00,thecolor[0], thecolor[1], thecolor[2], 0xFF, 0x00, soundByte,  //step 0
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 1
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 2
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x93  //last two bytes are the MSB and LSB of the 16-bit checksum
		}; 

		//calculate checksum
		int checksum = 0;
		for (int i=0; i < 62; i++)
			checksum += message[i];

		//add checksum value
		int msb = checksum >> 8;
		int lsb = checksum & 0x00FF;
		message[62] = (short)msb;
		message[63] = (short)lsb;

		//convert to byte array
		byte[] message2 = new byte[message.length];
		for (int i=0; i < message.length; i++)
			message2[i] = (byte)message[i];

		int val = hidDevice.write(message2, PACKET_LENGTH, (byte) 0x00);
		if (val >= 0) {
			//System.out.println("rc: " + val );
		} else {
			System.err.println("error: " + hidDevice.getLastErrorMessage());
		}
	}	

	public void keepAlive() {
		//keeps alive for max 11 seconds; send again to extend duration

		if (hidDevice == null) {
			System.err.println("Error- HID device is null");
			return;
		}

		// Ensure device is open after an attach/detach event
		if (!hidDevice.isOpen()) {
			hidDevice.open();
		}

		short[] message = new short[]{
				0x8F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 0
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 1
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 2
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00  //last two bytes are the MSB and LSB of the 16-bit checksum
		}; 

		//calculate checksum
		int checksum = 0;
		for (int i=0; i < 62; i++)
			checksum += message[i];

		//add checksum value
		int msb = checksum >> 8;
		int lsb = checksum & 0x00FF;
		message[62] = (short)msb;
		message[63] = (short)lsb;

		//convert to byte array
		byte[] message2 = new byte[message.length];
		for (int i=0; i < message.length; i++)
			message2[i] = (byte)message[i];

		int val = hidDevice.write(message2, PACKET_LENGTH, (byte) 0x00);
		if (val >= 0) {
			//System.out.println("rc: " + val );
		} else {
			System.err.println("error: " + hidDevice.getLastErrorMessage());
		}
	}

	public void stop() {

		if (hidDevice == null) {
			return;
		}

		// Ensure device is open after an attach/detach event
		if (!hidDevice.isOpen()) {
			hidDevice.open();
		}

		short[] message = new short[]{
				0x10, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00, 0x80,  //step 0
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 1
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 2
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x93  //last two bytes are the MSB and LSB of the 16-bit checksum
		}; 

		//calculate checksum
		int checksum = 0;
		for (int i=0; i < 62; i++)
			checksum += message[i];

		//add checksum value
		int msb = checksum >> 8;
		int lsb = checksum & 0x00FF;
		message[62] = (short)msb;
		message[63] = (short)lsb;

		//convert to byte array
		byte[] message2 = new byte[message.length];
		for (int i=0; i < message.length; i++)
			message2[i] = (byte)message[i];

		int val = hidDevice.write(message2, PACKET_LENGTH, (byte) 0x00);
		if (val >= 0) {
			//System.out.println("rc: " + val );
		} else {
			System.err.println("error: " + hidDevice.getLastErrorMessage());
		}
	}	

	//time on and time off are in tenths of a second
	public void blinkColor(BLColor c, int timeOn, int timeOff) {

		if (hidDevice == null) {
			System.err.println("Error- HID device is null");
			return;
		}

		//time on and time off must be in the range 0 - 255 inclusive
		if (timeOn < 0)
			timeOn = 0;
		else if (timeOn > 255)
			timeOn = 255;

		if (timeOff < 0)
			timeOff = 0;
		else if (timeOff > 255)
			timeOff = 255;		

		// Ensure device is open after an attach/detach event
		if (!hidDevice.isOpen()) {
			hidDevice.open();
		}

		short soundByte = 0x80; //off
		if (bSound) {
			soundByte = ringtones[ringTone.ordinal()];
			//set volume
			soundByte = (short) ((soundByte & 0xF8) + volume);
		}

		short[] thecolor = colors[c.ordinal()]; 
		short[] message = new short[]{
				0x11, 0x01, thecolor[0], thecolor[1], thecolor[2], (short)timeOn, 0x00, soundByte,
				0x10, 0x01, 0x00, 0x00, 0x00, (short)timeOff, 0x00, 0xA0,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x93  //last two bytes are the MSB and LSB of the 16-bit checksum
		}; 

		//calculate checksum
		int checksum = 0;
		for (int i=0; i < 62; i++)
			checksum += message[i];

		//add checksum value
		int msb = checksum >> 8;
		int lsb = checksum & 0x00FF;
		message[62] = (short)msb;
		message[63] = (short)lsb;

		//convert to byte array
		byte[] message2 = new byte[message.length];
		for (int i=0; i < message.length; i++)
			message2[i] = (byte)message[i];

		int val = hidDevice.write(message2, PACKET_LENGTH, (byte) 0x00);
		if (val >= 0) {
			//System.out.println("rc: " + val );
		} else {
			System.err.println("error: " + hidDevice.getLastErrorMessage());
		}
	}	

	public void initHidServices() throws HidException {
		// Configure to use custom specification
		HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();
		hidServicesSpecification.setAutoShutdown(true);
		hidServicesSpecification.setScanInterval(500);
		hidServicesSpecification.setPauseInterval(5000);
		hidServicesSpecification.setScanMode(ScanMode.SCAN_AT_FIXED_INTERVAL_WITH_PAUSE_AFTER_WRITE);

		// Get HID services using custom specification
		hidServices = HidManager.getHidServices(hidServicesSpecification);
		hidServices.addHidServicesListener(this);

		// Start the services
		hidServices.start();
	}

	public void initDevice(Vendor v, Product p, String serialNo) throws HidException {
		// Open the device device by Vendor ID and Product ID with wildcard serial number
		hidDevice = hidServices.getHidDevice(vendors[v.ordinal()], products[p.ordinal()], serialNo);
		if (hidDevice == null) {
			System.err.println("Error getting HID device: " + v.toString() + " , " + p.toString());
		}
	}	

	public void shutdown() {
		if (hidDevice != null)
			hidDevice.close();
		if (hidServices != null)
			hidServices.shutdown();
	}

	@Override
	public void hidDeviceAttached(HidServicesEvent event) {
		//event called when a HID device is attached
	}

	@Override
	public void hidDeviceDetached(HidServicesEvent event) {
		//event called when a HID device is detached
	}

	@Override
	public void hidFailure(HidServicesEvent event) {
		//event called when a HID device has a failure
	}

	public boolean isSoundEnabled() {
		return bSound;
	}

	public void setSoundEnabled(boolean bSound) {
		this.bSound = bSound;
	}

	public Ringtone getRingTone() {
		return ringTone;
	}

	public void setRingTone(Ringtone ringTone) {
		this.ringTone = ringTone;
	}

	public short getVolume() {
		return volume;
	}

	public void setVolume(short volume) {
		if (volume > 7 || volume < 0) {
			System.err.println("Error - volume must be in the range 0-7.");
			volume = 3;
		}
		this.volume = volume;
	}




}
