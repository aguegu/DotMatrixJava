#include "SD.h"
#include "string.h"

File root;

void setup()
{
	Serial.begin(9600);

	pinMode(10, OUTPUT); // SS pin must be output, change this to 53 on a mega

	bool sd_ok = SD.begin(9); // chip select pin

	if (!sd_ok)
	{
		Serial.println("failed.");
		return;
	}

	root = SD.open("/");
}

void sendAnimation(byte *data)
{
	Serial.write(0xf2); // batch update supported
	for (byte i = 8; i < 72; i++)
	{
		Serial.write(data[i]);
	}

	delay(20);	
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

		if (p_dot != NULL && strcmp(p_dot, ".dat") == 0)
		{
			readAnimation(file);
		}

		file.close();
	}
	else
		root.rewindDirectory();
}


