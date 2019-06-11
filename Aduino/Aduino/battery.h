#ifndef _BATTERY_H_
#define _BATTERY_H_

#define DEF_BATTERY_PIN    0 //A0
#define DEF_BATTERY_REG_R1 3.3f //REG to VIN
#define DEF_BATTERY_REG_R2 1.2f //REG to GND
#define BATTERY_CHECK_TIME 200 //ms

#define MAX_BATTERY_BUF 10
#define BATTERY_SKIP_DATA_COUNT 2
#define BATTERY_AVERAGE_COUNT (MAX_BATTERY_BUF-(BATTERY_SKIP_DATA_COUNT*2))

class battery{
private:  
  int batteryPin;
  uint16_t batteryBuf[MAX_BATTERY_BUF];
  uint8_t batteryCounter;
  uint32_t prevCheckTime;
  float battertVal;
  
  // VIN / (R1 + R2) * R2 = ADC Voltage 
  // ADC * (R1 + R2) / R2 = VIN
  float R1; //reg to VIN, default 3.3k
  float R2; //reg to GND default 1.2k

  void calBattery();
public:
  battery(void);
  battery(int pin);
  battery(int pin, float r1, float r2);

  void init(int pin, float r1, float r2);
  uint8_t updateBattery();
  float readBattery();
};
#endif
