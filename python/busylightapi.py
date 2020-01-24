from enum import Enum
import sys
import usb.core
import time


class BusyLightAPI:

    def __init__(self):
        print("In BusyLightAPI constructor")

    # vendor IDs
    PRODUCT_STRING = "BUSYLIGHT"
    VENDOR_NAME = ["PLENOM"]
    VENDOR_IDS = [0x27BB]

    # 0x27BB = 10171

    # product IDs
    PRODUCT_NAMES = [
        "PRODUCT_OMEGA_ID",
        "PRODUCT_ALPHA_ID",
        "PRODUCT_UC_ID",
        "PRODUCT_KUANDO_BOX_ID",
        "PRODUCT_BOOTLOADER_ID"]

    PRODUCT_IDS = [0x3BCD, 0x3BCA, 0x3BCB, 0x3BCC, 0x3BC0]

    # [15309, 15306, 15307, 15308, 15296

    # colors
    class BLColor(Enum):
        RED = 0
        GREEN = 1
        BLUE = 2
        ORANGE = 3
        YELLOW = 4
        PINK = 5
        AQUA = 6
        WHITE = 7
        INDIGO = 8
        VIOLET = 9

    colors = [
        [0x64, 0x00, 0x00],
        [0x00, 0x64, 0x00],
        [0x00, 0x00, 0x64],
        [0x64, 0x32, 0x00],
        [0x64, 0x64, 0x00],
        [0x64, 0x00, 0x64],
        [0x00, 0x64, 0x64],
        [0x64, 0x64, 0x64],
        [29, 0, 51],
        [93, 51, 93]]

    # sounds
    class Ringtone(Enum):
        TONE_RISING = 0
        TONE_PHONE = 1
        TONE_SIMON = 2
        TONE_ALTERNATIVE = 3
        TONE_CLASSIC = 4
        TONE_ALIEN = 5
        TONE_OFFICE = 6
        TONE_LIVEWIRE = 7
        TONE_OLD = 8
        TONE_TRON = 9
        ONE_DISCO = 10

    ringtones = [
        0b10100011,
        0b11000011,
        0b10010011,
        0b10001011,
        0b10110011,
        0b10011011,
        0b10111011,
        0b11101011,
        0b11001011,
        0b11010011,
        0b10101011]

    PACKET_LENGTH = 64

    usb_device = None

    #	private HidServices hidServices;
    #	private HidDevice hidDevice;
    #	private boolean bSound = false;
    #	private Ringtone ringTone = Ringtone.TONE_RISING; //default
    #	private short volume = 3;
    #	private KeepAliveThread kaThread;

    #	public BusyLightAPI() {
    #		kaThread = new KeepAliveThread("kat");
    #		initHidServices();
    #	}

    # Return the vendor and product indexes for the detected BusyLight device, note it is
    # in dictionary format (https://www.w3schools.com/python/python_dictionaries.asp)
    @property
    def detect_busy_light(self):
        device_params = [0, 0]

        i = 0

        for usb_device in usb.core.find(find_all=True):
            # Retrieves all USB devices, need to look for the BusyLight vendor and product IDs

            #print("usb_device.idVendor: ", hex(usb_device.idVendor))
            #print("usb_device.idProduct: ", hex(usb_device.idProduct))
            # print("usb_device.bConfigurationValue", usb_device.__getattribute__('bConfigurationValue'))
            # print("usb_device.bConfigurationValue: ", usb_device[i]["bConfigurationValue"])
            # i += 1

            if usb_device.idVendor == 0x27BB and usb_device.idProduct in self.PRODUCT_IDS:
                print("Found BusyLight")
                print("\n###############")
                print(usb_device)
                print("###############")

                device_params[0] = usb_device.idVendor
                device_params[1] = usb_device.idProduct
                # device_params[2] = usb_device.bConfigurationValue
                # usb_device.set_configuration(self, device_params[2])
                print("usb_device.bLength: ", hex(usb_device.bLength))
                print("usb_device.bDescriptorType: ", hex(usb_device.bDescriptorType))
                print("usb_device.bcdUSB: ", hex(usb_device.bcdUSB))
                print("usb_device.bDeviceClass: ", hex(usb_device.bDeviceClass))
                print("usb_device.bDeviceProtocol: ", hex(usb_device.bDeviceProtocol))
                print("usb_device.bMaxPacketSize0: ", hex(usb_device.bMaxPacketSize0))
                print("usb_device.idVendor: ", hex(usb_device.idVendor))
                print("usb_device.idProduct: ", hex(usb_device.idProduct))
                print("usb_device.bcdDevice: ", hex(usb_device.bcdDevice))
                print("usb_device.iManufacturer: ", hex(usb_device.iManufacturer))
                print("usb_device.iProduct: ", hex(usb_device.iProduct))
                print("usb_device.iSerialNumber: ", hex(usb_device.iSerialNumber))
                print("usb_device.bNumConfigurations: ", hex(usb_device.bNumConfigurations))

                # Config 1
                print("usb_device[0].bLength: ", hex(usb_device[0].bLength))
                print("usb_device[0].bDescriptorType: ", hex(usb_device[0].bDescriptorType))
                print("usb_device[0].wTotalLength: ", hex(usb_device[0].wTotalLength))
                print("usb_device[0].bNumInterfaces: ", hex(usb_device[0].bNumInterfaces))
                print("usb_device[0].bConfigurationValu: ", hex(usb_device[0].bConfigurationValue))
                print("usb_device[0].iConfiguration: ", hex(usb_device[0].iConfiguration))
                print("usb_device[0].bAttributes: ", hex(usb_device[0].bmAttributes))
                print("usb_device[0].bMaxPower: ", hex(usb_device[0].bMaxPower))

                # Interface 0
                # print("usb_device[1].bLength: ", hex(usb_device[1].bLength))
                #print("usb_device.getItem: ", usb_device.__getitem__(1, 1))
                #print("usb_device: ", hex(usb_device.get_string))















        return device_params

    def init_device(self, vendor, product, serialNo):

        print("Entering init_device")

        print("vendor: ", vendor)
        print("product: ", product)

        #hidDevice = hid.Device(vendor, product, serialNo)

        #print("hidDevice.mfgr", hidDevice.manufacturer)

        # Open the device device by Vendor ID and Product ID with wildcard serial number
        #if hidDevice == None:
        #    print("Error getting HID device: ", vendor, product)
        #    return false

        #return true  # device successfully initialized

    def rainbow(self):
        print("Entering rainbow()")

        sleepTime = 1

        self.steadyColor(0x64, 0x00, 0x00)
        time.sleep(sleepTime)

        #self.steadyColor(self.colors[self.BLColor.ORANGE])
        #time.sleep(sleepTime)

        #self.steadyColor(self.colors[self.BLColor.YELLOW])
        #time.sleep(sleepTime)

        #self.steadyColor(self.colors[self.BLColor.GREEN])
        #time.sleep(sleepTime)

        #self.steadyColor(self.colors[self.BLColor.BLUE])
        #time.sleep(sleepTime)

        #self.steadyColor(self.colors[self.BLColor.INDIGO])
        #time.sleep(sleepTime)

        #stopLight()


    # takes a standard HEX RGB color, converts it to PWM
    def steadyColor(self, r, g, b):
        print("Entering steadyColor()")
        print("r: ", r)
        print("g: ", g)
        print("b: ", b)


		#short[] pwmcolor = convertHexToPWM(self, g, b);

		#if (kaThread != null && kaThread.isAlive())
		#	kaThread.interrupt();

		#if (hidDevice == null) {
		#	System.err.println("Error- HID device is null");
		#	return;
		#}

		#// Ensure device is open after an attach/detach event
		#if (!hidDevice.isOpen()) {
		#	hidDevice.open();
		#}

        soundByte = 0x80

		#if (bSound) {
			#soundByte = ringtones[ringTone.ordinal()];
	#		//set volume
	#		soundByte = (short) ((soundByte & 0xF8) + volume);
	#	}

        message = [
            0x11, 0x00, r, g, b, 0xFF, 0x00, soundByte,  # step 0
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  # step 1
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  # step 2
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x93 ] # last two bytes are the MSB and LSB of the 16-bit checksum

        print("message: ", message)

        #retval = self.hidDevice.write(self, message)

        #print("retval: ", retval)

	#	if (sendBytes(message)) {
	#		//keep alive
	#		if (kaThread != null && !kaThread.isAlive()) {
	#			kaThread.interrupt();
	#			kaThread = new KeepAliveThread("kat");
	#			kaThread.start();
	#		}
	#	}
	#}

#    def short[] convertHexToPWM(int r, int g, int b):
#        short[] ret = new short[] {0, 0, 0};

#    if (r < 0 | | r > 255 | | g < 0 | | g > 255 | | b < 0 | | b > 255)
#{
#System.err.println("jbusylightapi:convertHexToPWM() - error: invalid RGB value(s): " + r + " , " + g + " , " + b);
#return ret;
#}

#ret[0] = (short)
#Math.round((r / 255.0) * 100);
#ret[1] = (short)
#Math.round((g / 255.0) * 100);
#ret[2] = (short)
#Math.round((b / 255.0) * 100);

#return ret;
#}



# int[] ret = new int[2];
# if (hidServices != null) {
#	for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
#		if (hidDevice.getProduct() != null && hidDevice.getProduct().equalsIgnoreCase("busylight")) {
#			ret[0] = getVendorIndex(hidDevice.getVendorId());
#			ret[1] = getProductIndex(hidDevice.getProductId());
#		}
#	}
# }
# return ret;
# }

#	public int getVendorIndex(int val) {
#		int ret = -1;
#		for (int i=0; i < vendors.length; i++) {
#			if (vendors[i] == val) {
#				ret = i;
#				break;
#			}
#		}
#		return ret;
#	}

#	public int getProductIndex(int val) {
#		int ret = -1;
#		for (int i=0; i < products.length; i++) {
#			if (products[i] == val) {
#				ret = i;
#				break;
#			}
#		}
#		return ret;
#	}

#	public void steadyColor(BLColor c) {
#
#		if (kaThread != null && kaThread.isAlive())
#			kaThread.interrupt();
#
#		if (hidDevice == null) {
#			System.err.println("Error- HID device is null");
#			return;
#		}
#
#		// Ensure device is open after an attach/detach event
#		if (!hidDevice.isOpen()) {
#			hidDevice.open();
#		}
#
#		short soundByte = 0x80; //off
#		if (bSound) {
#			soundByte = ringtones[ringTone.ordinal()];
#			//set volume
#			soundByte = (short) ((soundByte & 0xF8) + volume);
#		}
#
#		short[] thecolor = colors[c.ordinal()];
#		short[] message = new short[]{
#				0x11, 0x00,thecolor[0], thecolor[1], thecolor[2], 0xFF, 0x00, soundByte,  //step 0
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 1
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 2
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x93  //last two bytes are the MSB and LSB of the 16-bit checksum
#		};
#
#		if (sendBytes(message)) {
#			//keep alive
#			if (kaThread != null && !kaThread.isAlive()) {
#				kaThread.interrupt();
#				kaThread = new KeepAliveThread("kat");
#				kaThread.start();
#			}
#		}
#	}
#
#	//takes a standard HEX RGB color, converts it to PWM
#	public void steadyColor(int r, int g, int b) {
#
#		short[] pwmcolor = convertHexToPWM(r,g,b);
#
#		if (kaThread != null && kaThread.isAlive())
#			kaThread.interrupt();
#
#		if (hidDevice == null) {
#			System.err.println("Error- HID device is null");
#			return;
#		}
#
#		// Ensure device is open after an attach/detach event
#		if (!hidDevice.isOpen()) {
#			hidDevice.open();
#		}
#
#		short soundByte = 0x80; //off
#		if (bSound) {
#			soundByte = ringtones[ringTone.ordinal()];
#			//set volume
#			soundByte = (short) ((soundByte & 0xF8) + volume);
#		}
#
#		short[] message = new short[]{
#				0x11, 0x00,pwmcolor[0], pwmcolor[1], pwmcolor[2], 0xFF, 0x00, soundByte,  //step 0
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 1
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 2
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x93  //last two bytes are the MSB and LSB of the 16-bit checksum
#		};
#
#		if (sendBytes(message)) {
#			//keep alive
#			if (kaThread != null && !kaThread.isAlive()) {
#				kaThread.interrupt();
#				kaThread = new KeepAliveThread("kat");
#				kaThread.start();
#			}
#		}
#	}
#
#	public void keepAlive() {
#		//keeps alive for max 11 seconds; send again to extend duration
#
#		if (hidDevice == null) {
#			System.err.println("Error- HID device is null");
#			return;
#		}
#
#		// Ensure device is open after an attach/detach event
#		if (!hidDevice.isOpen()) {
#			hidDevice.open();
#		}
#
#		short[] message = new short[]{
#				0x8F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 0
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 1
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 2
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00  //last two bytes are the MSB and LSB of the 16-bit checksum
#		};
#
#		if (!sendBytes(message)) {
#			System.err.println("keepAlive failed");
#		}
#
#	}
#
#	public void stopLight() {
#
#		//stop the keep alive thread if it exists
#		if (kaThread != null && kaThread.isAlive()) {
#			kaThread.interrupt();
#		}
#
#		if (hidDevice == null) {
#			return;
#		}
#
#		// Ensure device is open after an attach/detach event
#		if (!hidDevice.isOpen()) {
#			hidDevice.open();
#		}
#
#		short[] message = new short[]{
#				0x10, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00, 0x80,  //step 0
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 1
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //step 2
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x93  //last two bytes are the MSB and LSB of the 16-bit checksum
#		};
#
#		if (!sendBytes(message)) {
#			System.err.println("stop failed");
#		}
#	}
#
#	//time on and time off are in tenths of a second
#	public void blinkColor(BLColor c, int timeOn, int timeOff) {
#
#		if (kaThread != null && kaThread.isAlive())
#			kaThread.interrupt();
#
#		if (hidDevice == null) {
#			System.err.println("Error- HID device is null");
#			return;
#		}
#
#		//time on and time off must be in the range 0 - 255 inclusive
#		if (timeOn < 0)
#			timeOn = 0;
#		else if (timeOn > 255)
#			timeOn = 255;
#
#		if (timeOff < 0)
#			timeOff = 0;
#		else if (timeOff > 255)
#			timeOff = 255;
#
#		// Ensure device is open after an attach/detach event
#		if (!hidDevice.isOpen()) {
#			hidDevice.open();
#		}
#
#		short soundByte = 0x80; //off
#		if (bSound) {
#			soundByte = ringtones[ringTone.ordinal()];
#			//set volume
#			soundByte = (short) ((soundByte & 0xF8) + volume);
#		}
#
#		short[] thecolor = colors[c.ordinal()];
#		short[] message = new short[]{
#				0x11, 0x01, thecolor[0], thecolor[1], thecolor[2], (short)timeOn, 0x00, soundByte,
#				0x10, 0x01, 0x00, 0x00, 0x00, (short)timeOff, 0x00, 0xA0,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
#				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x93  //last two bytes are the MSB and LSB of the 16-bit checksum
#		};
#
#		if (sendBytes(message)) {
#			//keep alive
#			if (kaThread != null && !kaThread.isAlive()) {
#				kaThread.interrupt();
#				kaThread = new KeepAliveThread("kat");
#				kaThread.start();
#			}
#		}
#	}
#
#	//time on and time off are in tenths of a second
#	public void blinkColor(int r, int g, int b, int timeOn, int timeOff) {
#
#		if (kaThread != null && kaThread.isAlive())
#			kaThread.interrupt();
#
#		if (hidDevice == null) {
#			System.err.println("Error- HID device is null");
#			return;
#		}
#
#		short[] pwmcolor = convertHexToPWM(r,g,b);
#
#		//time on and time off must be in the range 0 - 255 inclusive
#		if (timeOn < 0)
#			timeOn = 0;
#		else if (timeOn > 255)
#			timeOn = 255;
#
#		if (timeOff < 0)
#			timeOff = 0;
#		else if (timeOff > 255)
#			timeOff = 255;
#
#		// Ensure device is open after an attach/detach event
#		if (!hidDevice.isOpen()) {
#			hidDevice.open();
#		}
#
#		short soundByte = 0x80; //off
#		if (bSound) {
#			soundByte = ringtones[ringTone.ordinal()];
#			//set volume
#			soundByte = (short) ((soundByte & 0xF8) + volume);
#		}
'''
		short[] message = new short[]{
				0x11, 0x01, pwmcolor[0], pwmcolor[1], pwmcolor[2], (short)timeOn, 0x00, soundByte,
				0x10, 0x01, 0x00, 0x00, 0x00, (short)timeOff, 0x00, 0xA0,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x93  //last two bytes are the MSB and LSB of the 16-bit checksum
		};

		if (sendBytes(message)) {
			//keep alive
			if (kaThread != null && !kaThread.isAlive()) {
				kaThread.interrupt();
				kaThread = new KeepAliveThread("kat");
				kaThread.start();
			}
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

	public boolean initDevice(Vendor v, Product p, String serialNo) throws HidException {
		// Open the device device by Vendor ID and Product ID with wildcard serial number
		hidDevice = hidServices.getHidDevice(vendors[v.ordinal()], products[p.ordinal()], serialNo);
		if (hidDevice == null) {
			System.err.println("Error getting HID device: " + v.toString() + " , " + p.toString());
			return false;
		}
		return true; //device successfully initialized
	}

	public void ping() {
		new PingThread("pingthread").start();
	}

	public void rainbow() {
		long ms = 75;

		try {
			steadyColor(BLColor.RED); Thread.sleep(ms);
			steadyColor(BLColor.ORANGE); Thread.sleep(ms);
			steadyColor(BLColor.YELLOW); Thread.sleep(ms);
			steadyColor(BLColor.GREEN); Thread.sleep(ms);
			steadyColor(BLColor.BLUE); Thread.sleep(ms);
			steadyColor(BLColor.INDIGO); Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stopLight();
	}

	public void shutdown() {
		if (kaThread != null) {
			if (kaThread.isAlive())
				kaThread.interrupt();
			kaThread = null;
		}
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

	public static short[] convertHexToPWM(int r, int g, int b) {
		short[] ret = new short[]{0,0,0};

		if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
			System.err.println("jbusylightapi:convertHexToPWM() - error: invalid RGB value(s): " + r + " , " + g + " , " + b);
			return ret;
		}

		ret[0] = (short)Math.round( (r / 255.0) * 100 );
		ret[1] = (short)Math.round( (g / 255.0) * 100 );
		ret[2] = (short)Math.round( (b / 255.0) * 100 );

		return ret;
	}

	public boolean sendBytes(short[] message) {

		if (message == null) {
			System.err.println("message is null");
			return false;
		}

		if (message.length != 64) {
			System.err.println("message is not length 64");
			return false;
		}

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
			//good
			return true;
		} else {
			System.err.println("error: " + hidDevice.getLastErrorMessage());
			return false;
		}
	}

	class PingThread extends Thread {
		public PingThread(String name) {
			super(name);
		}
		@Override
		public void run() {
			steadyColor(BLColor.GREEN);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			stopLight();
		}
	}

	class KeepAliveThread extends Thread {
		private static final int FREQUENCY_SECS = 8;
		private boolean bAlive;
		public KeepAliveThread(String name) {
			super(name);
			bAlive = true;
		}
		@Override
		public void run() {
			while (bAlive) {
				keepAlive();
				try {
					Thread.sleep(FREQUENCY_SECS * 1000);
				} catch (InterruptedException e) {
					bAlive = false;
				}
			}
		}
	}
}
'''

light = BusyLightAPI()
device_params = light.detect_busy_light
print("FOUND: ", device_params[0], device_params[1])

light.init_device(device_params[0], device_params[1], None)

# test
#light.rainbow()
# time.sleep(3)

# light.stopLight()
# light.shutdown()
