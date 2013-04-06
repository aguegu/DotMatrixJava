#include "SD.h"
#include "string.h"

Sd2Card card;
SdVolume volume;
const int chipSelect = 4;
File root;

void setup()
{
	Serial.begin(57600);

	pinMode(10, OUTPUT); // change this to 53 on a mega

	bool sd_ok = SD.begin(9);

	if (!sd_ok)
	{
		Serial.println("failed.");
		return;
	}

	root = SD.open("/");
}

void sendAnimation(byte *data)
{
	Serial.write(0xf3);
	Serial.write(data[1]);

	Serial.write(0xf4);
	Serial.write(data[2]);

	Serial.write(0xf5);
	Serial.write(data[3]);

	Serial.write(0xf2);
	for (byte i = 8; i < 72; i++)
	{
		Serial.write(data[i]);
	}

	delay(makeWord(data[4], data[5]));	
}

void readAnimation(File & file)
{
	byte data[72];
	while (file.available())
	{
		file.read(data, 72);
		sendAnimation(data);
	}
}

void loop(void)
{
	File file = root.openNextFile();
	if (file)
	{
		char *p = file.name();
		char *p_dot = strchr(p, '.');

		if (p_dot != NULL && strcmp(p_dot, ".DAT") == 0)
		{
			readAnimation(file);
		}

		file.close();
	}
	else
		root.rewindDirectory();
}


