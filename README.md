8x8x8 Led Cube control program
---------

PC control program for generic 8x8x8 3D LED Cube found on eBay
(e.g. 3D LightSquared 8x8x8 LED Cube DIY kit, ideasoft, etc.)

![Program view](/help/program_view.png)

* Written in Java (requires Java RE, download here: http://java.com)
* Supports direct animation playback through Serial/UART interface (rxtx library)
* Various GUI usability enhancements

##### Firmware
This program can control any packet format compatible LED cube (see below).
Example firmware of a compatible LED cube with an STC12C5A60S2 MCU can be found here: [Source Code](https://github.com/tomazas/ledcube8x8x8)

##### Using the program
* Run run_x32.bat for 32-bit Windows
* Run run_x64.bat for 64-bit Windows

Check this YouTube video for example: https://youtu.be/UplJi7pdV_Y
[![Using program](http://img.youtube.com/vi/UplJi7pdV_Y/0.jpg)](https://youtu.be/UplJi7pdV_Y)
 
##### LED Cube control packet format

8x8x8 LED Cubes that support below packet format can be controlled with the program via Serial console or other MCU such as an Atmega/Arduino.
Example UART/Serial packet (in hex):
```
F2
00 00 00 00 00 00 00 FF
00 00 00 00 00 00 00 FF
00 00 00 00 00 00 00 FF
00 00 00 00 00 00 00 FF
00 00 00 00 00 00 00 FF
00 00 00 00 00 00 00 FF
00 00 00 00 00 00 00 FF
00 00 00 00 00 00 00 FF
```

* F2 - denotes packet header (aka. batch update)
* next 64 bytes - (8x8x8 bits) of LED light states
  * one byte - controls a LED row (8 LEDs)
  * can be any value in range: 00-FF (i.e. 00 - all 8 LEDs in row are off, FF - all 8 LEDs are on)
  * a single line (e.g. 00 00 00 00 00 00 00 FF) denotes a 64 LED layer

`Implementation note:` to save energy/current consumption only a single layer (64 LEDs) in a LED cube is ON at one time. This is done for all layers and so fast (using a hardware timer), that the human eye does not recognize this. This hack allows to view the cube (all layers) as fully lit. 
