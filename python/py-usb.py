import sys
import usb.core


# To get to the endpoint we need to descend down the hierarchy of
# 1. Device
VENDOR_ID = 0x27BB
PRODUCT_ID = 0x3BCD

# 1. Device
device = usb.core.find(idVendor=VENDOR_ID, idProduct=PRODUCT_ID)
if device is None:
    print("BusyLight not connected")
    #sys.exit(1)
else:
    print("Found the BusyLight")
