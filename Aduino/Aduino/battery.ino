#include "battery.h"
#include <SoftwareSerial.h>

SoftwareSerial BTSerial(2, 3); 

// 기본 A0
// 배터리와 연결되는 저항은 3.3K
// GND와 연결되는 저항은 1.2K
battery myBattery = battery(); 

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);
}

void loop() {
  if(myBattery.updateBattery() == 1 || BTSerial.available()){
    Serial.print("Battery Power : ");
    Serial.print(myBattery.readBattery()-7.3);
    Serial.println("V");
    //BTSerial.write(myBattery.readBattery());
    BTSerial.println(myBattery.readBattery()-7.3);
    delay(100);
  }
}
