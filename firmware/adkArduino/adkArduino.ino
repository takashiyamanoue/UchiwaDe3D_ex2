/*
  Arduino Sketch for the "Wearable LED Matrix Sign", which shows
  a Tweet of Twitter.
            29 April 2015, Takashi Yamanoue @ Fukuyama University. 
*/
#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

// Adafruit_NeoMatrix example for single NeoPixel Shield.
// Scrolls 'Howdy' across the matrix in a portrait (vertical) orientation.

#include <SPI.h>
#include <Adafruit_GFX.h>
#include <Adafruit_DotStarMatrix.h>
#include <Adafruit_DotStar.h>
#ifndef PSTR
 #define PSTR // Make Arduino Due happy
#endif

//#include <stdio.h>

#define RWMax 16
#define RHMax 16

#define Depth 8

#define DATAPIN  4
#define CLOCKPIN 5


// MATRIX DECLARATION:
// Parameter 1 = width of DotStar matrix
// Parameter 2 = height of matrix
// Parameter 3 = pin number (most are valid)
// Parameter 4 = matrix layout flags, add together as needed:
//   DS_MATRIX_TOP, DS_MATRIX_BOTTOM, DS_MATRIX_LEFT, DS_MATRIX_RIGHT:
//     Position of the FIRST LED in the matrix; pick two, e.g.
//     DS_MATRIX_TOP + DS_MATRIX_LEFT for the top-left corner.
//   DS_MATRIX_ROWS, DS_MATRIX_COLUMNS: LEDs are arranged in horizontal
//     rows or in vertical columns, respectively; pick one or the other.
//   DS_MATRIX_PROGRESSIVE, DS_MATRIX_ZIGZAG: all rows/columns proceed
//     in the same order, or alternate lines reverse direction; pick one.
//   See example below for these values in action.
// Parameter 5 = pixel type:
//   DOTSTAR_BRG  Pixels are wired for BRG bitstream (most DotStar items)
//   DOTSTAR_GBR  Pixels are wired for GBR bitstream (some older DotStars)
/* */
Adafruit_DotStarMatrix matrix = Adafruit_DotStarMatrix(
  16, 16, DATAPIN, CLOCKPIN,
  DS_MATRIX_TOP     + DS_MATRIX_RIGHT +
  DS_MATRIX_ROWS + DS_MATRIX_ZIGZAG,
  DOTSTAR_BRG);

uint16_t backGround;
uint16_t bmp3D[RWMax][RHMax][Depth];

int digitalOutMax=13;

byte i2c[64] = {
        ' ','-','+','/','=','#','!','$',
        '0','1','2','3','4','5','6','7',
        '8','9','A','B','C','D','E','F',
        'G','H','I','J','K','L','M','N',
        '0','P','Q','R','S','T','U','V',
        'W','X','Y','Z','a','b','c','d',
        'e','f','g','h','i','j','k','l',
        'm','n','o','p','q','r','s','t'
};

int c2i[128];

void init_c2i(){
    for(int i=0;i<64;i++){
          char x=i2c[i];
          c2i[(int)x]=i;
    }
}

uint16_t rgb2c(uint16_t r, uint16_t g, uint16_t b){
  uint16_t rtn=matrix.Color(r<<3,g<<3,b<<3);
  return rtn;
}

uint16_t irgb2c(uint16_t i){
   uint16_t r=(i & 0x7c00)>>11;
   uint16_t g=(i & 0x03e0)>>5;
   uint16_t b=(i & 0x001f);
   return rgb2c(r,g,b);
}

void initialBmp3D(){
  int i,j,k;
//  printf("start clearVirtualGraphicsArea\n");
    for( i=0;i<RWMax;i++){
      for( j=0;j<RHMax;j++){
        for( k=0; k<Depth; k++){
            bmp3D[i][j][k]=backGround;
        }
      }
    }
    for(k=0;i<Depth;k++){
      bmp3D[0][0][k]=rgb2c(255,255,0);
      bmp3D[RWMax-1][0][k]=rgb2c(255,255,0);
      bmp3D[RWMax-1][RHMax-1][k]=rgb2c(255,255,0);
      bmp3D[0][RHMax-1][k]=rgb2c(255,255,0);
    }
    for(i=0;i<RWMax;i++){
      bmp3D[i][0][0]=rgb2c(255,255,0);
      bmp3D[i][RHMax-1][0]=rgb2c(255,255,0);
      bmp3D[i][0][Depth-1]=rgb2c(255,255,0);
      bmp3D[i][RHMax-1][Depth-1]=rgb2c(255,255,0);
    }
    for(j=0;j<RHMax;j++){
      bmp3D[0][j][0]=rgb2c(255,255,0);
      bmp3D[RWMax-1][j][0]=rgb2c(255,255,0);
      bmp3D[0][j][Depth-1]=rgb2c(255,255,0);
      bmp3D[RWMax-1][j][Depth-1]=rgb2c(255,255,0);
    }    
//  printf("end clearVirtualGraphicsArea\n");
}



/* */
AndroidAccessory acc("Google, Inc.",
         "uchiwaDe3Dex1",
         "Arduino Board for uchiwaDe3D_ex1",
         "1.0",
         "http://www.android.com",
         "0000000012346000");
/* */
/*
AndroidAccessory acc("Google, Inc.",
         "Twitter2NeoMatrixEx5",
         "Arduino Board for twitter2neomatrixex5",
         "1.0",
         "http://www.android.com",
         "0000000012345000");
         */
void setup();
void loop();

int iter;
int dir; // last received font position on the virtualGraphicsArea
int dp; // current bitmap depth 
const int speaker = 8;

#define analogInMax 8
#define digitalInMax 6
#define digitalOutMax 14
int analogIns[analogInMax];
int digitalIns[digitalInMax];
int digitalOuts[digitalOutMax];
int digitalVal;


void setup()
{
   #if defined(__AVR_ATtiny85__) && (F_CPU == 16000000L)
    clock_prescale_set(clock_div_1); // Enable 16 MHz on Trinket
   #endif

   Serial.begin(57600);
   Serial.print("\r\nStart\n\r");
   init_c2i();
   for(int i=0;i<digitalInMax;i++) digitalIns[i]=i;
   for(int i=0;i<digitalOutMax;i++) digitalOuts[i]=i;

   for(int i=0;i<digitalInMax;i++)
      pinMode(digitalIns[i],INPUT);
   for(int i=digitalInMax+1;i<digitalOutMax;i++)
      pinMode(digitalOuts[i],OUTPUT);
   
   analogIns[0]=A0;
   analogIns[1]=A1;
   analogIns[2]=A2;
   analogIns[3]=A3;
   analogIns[4]=A4;
   analogIns[5]=A5;
   analogIns[6]=A6;
   analogIns[7]=A7;
  
   Serial.print("init_c2i()\n\r");
   delay(5); // add 2012 12/9
   Serial.print("acc.powerOn Start\n\r");
   acc.powerOn();
   Serial.print("acc.powerOn\n\r");

   // スピーカーをつないだピンを出力に設定する
   pinMode(speaker, OUTPUT);

   
   matrix.begin();
   matrix.setBrightness(40); 
   backGround=matrix.Color(0,0,0); 
   initialBmp3D();
  /* */
  Serial.print("setup end\n");
  dir=0;
  dp=0;
  iter=0;
}
char readKind(){
  byte inMsg[5];
  Serial.println("readKind()");
  int len = acc.read(inMsg, sizeof(inMsg),1);
  Serial.print("len=");Serial.println(len);
  if(len<0) return ' ';
  if(inMsg[0]=='b'){
    if(inMsg[2]=='r') return 'r';
    if(inMsg[2]=='n') return 'n';
  }
  return ' ';
}

void readAcc(){
  byte inMsg[1024];
     Serial.println("readAcc()"); //**
     int len = acc.read(inMsg, sizeof(inMsg), 1);
     Serial.print("len=");Serial.println(len);
     if(len<0) return;     
     int i,j;
     byte b;
     if(inMsg[0]=='b'){
         byte dc=inMsg[2];
         int k=dc-'0';
         if(k>9) k=dc-'a'+10;
         if(k>=Depth) k=Depth;
         int p=4;
         for(i=0;i<16;i++){
          for(j=0;j<16;j++){
            byte c1=inMsg[p];
            int i1=(c2i[(int)c1]) & 0x001f;
            byte c2=inMsg[p+1];
            int i2=c2i[(int)c2] & 0x001f;            
            byte c3=inMsg[p+2];
            int i3=c2i[(int)c3] & 0x001f;           
            p+=3;
            bmp3D[i][j][k]=(((i1<<5)|i2)<<5)|i3;
          }
         }
     }
     else
     if(inMsg[0]=='c'){ // color inMsg['c','r'|'d',r,g,b]
                          // r: rainbow, d: direct r-g-b
                          // r,g,b... red, green, blue
     }
}
void writeAcc(){
  byte outMsg[4];
     digitalVal=0;
     for(int i=0;i<digitalInMax;i++){
       int b=0;
       if(digitalRead(digitalIns[i])==HIGH)
          b=1;
       digitalVal=digitalVal<<1 | b;
     }
     Serial.print("digitalVal="); Serial.println(digitalVal);
     outMsg[0]='d';
     outMsg[1]=digitalVal & 0xff;
     outMsg[2]=0;
     outMsg[3]=0;
     acc.write(outMsg,4);
     Serial.println("end_writeAcc()");
}

void sendRequest(){
  Serial.println("sendRequest()");
     byte outMsg[4];
     digitalVal=0;
     outMsg[0]='r';
     outMsg[1]=0;
     outMsg[2]=0;
     outMsg[3]=0;
     acc.write(outMsg,4);
     Serial.println("end_sendRequest");
  
}

void hiSound()
{
  int i = 0;
  while(i<50) {
      digitalWrite(speaker, HIGH);
      delay(1);
      digitalWrite(speaker, LOW);
      delay(1);
      i = i + 1;
  }
}

// 低い音を出す関数
void lowSound()
{
  int i = 0;
  while(i<50) {
      digitalWrite(speaker, HIGH);
      delay(10);
      digitalWrite(speaker, LOW);
      delay(10);
      i = i + 10;
  }
}    

#define VWMax 16
void loop()
{
  /* */
  if(iter%Depth==0){
    if(acc.isConnected()){
//          Serial.print("iter=");Serial.print(iter);
//          Serial.println(",iter%Depth==0");
      writeAcc();
      
    }  
  }
 /* */
  if(iter%(Depth*4)==0){
//    Serial.print("iter%(Depth*16)==0\n\r");
     if (acc.isConnected()) {
        Serial.print("acc.isConnected()\n\r");
        sendRequest();
        if(readKind()=='r'){
           for(int i=0;i<Depth;i++){
              readAcc();
           }
        }
     }
     iter=0;
  }
  if(iter>=Depth*16)
  {
     iter=0;
  }
  
  if(dp<=0){
    hiSound();        
    dir=0;
    dp=0;
  }
  if(dp>=Depth){
    lowSound();        
    dir=1;
    dp=Depth-1;
  }
//  Serial.print("neo matrix\n\r");   
  /* */
  matrix.fillScreen(0);
  
  for(int i=0;i<RWMax;i++){
     for(int j=0;j<RHMax;j++){
       uint16_t cx=bmp3D[i][j][dp];
//       printf("%d\n",c);
       matrix.drawPixel(i,j,irgb2c(cx));
     }
  }

  matrix.show();   
  /* */   
//   delay(1);

   if(dir==0) dp++;
   if(dir==1) dp--;
   iter++;
}

