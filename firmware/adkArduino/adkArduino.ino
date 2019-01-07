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

#include <Adafruit_GFX.h>
#include <Adafruit_DotStarMatrix.h>
#include <Adafruit_DotStar.h>
#ifndef PSTR
 #define PSTR // Make Arduino Due happy
#endif

//#include <stdio.h>

#define RWMax 16
#define RHMax 16
#define Depth 7

#define DATAPIN  4
#define CLOCKPIN 5

// MATRIX DECLARATION:
// Parameter 1 = width of NeoPixel matrix
// Parameter 2 = height of matrix
// Parameter 3 = pin number (most are valid)
// Parameter 4 = matrix layout flags, add together as needed:
//   NEO_MATRIX_TOP, NEO_MATRIX_BOTTOM, NEO_MATRIX_LEFT, NEO_MATRIX_RIGHT:
//     Position of the FIRST LED in the matrix; pick two, e.g.
//     NEO_MATRIX_TOP + NEO_MATRIX_LEFT for the top-left corner.
//   NEO_MATRIX_ROWS, NEO_MATRIX_COLUMNS: LEDs are arranged in horizontal
//     rows or in vertical columns, respectively; pick one or the other.
//   NEO_MATRIX_PROGRESSIVE, NEO_MATRIX_ZIGZAG: all rows/columns proceed
//     in the same order, or alternate lines reverse direction; pick one.
//   See example below for these values in action.
// Parameter 5 = pixel type flags, add together as needed:
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     Pixels are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     Pixels are wired for RGB bitstream (v1 FLORA pixels, not v2)


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

Adafruit_DotStarMatrix matrix = Adafruit_DotStarMatrix(
  16, 16, DATAPIN, CLOCKPIN,
  DS_MATRIX_TOP     + DS_MATRIX_RIGHT +
  DS_MATRIX_ROWS + DS_MATRIX_ZIGZAG,
  DOTSTAR_BRG);
  
/*
const uint16_t colors[] = {
  matrix.Color(255, 0, 0), matrix.Color(0, 255, 0), matrix.Color(0, 0, 255) };
*/  
uint16_t backGround;
uint16_t bmp3D[RWMax][RHMax][Depth];

int digitalOutMax=13;
/*

   Android
   +---------------------+ tweet
       ...
   +---------------------+ tweet max 132 char
     ^        |
     |        | send font at p
    ask       | to arduino and p++, 
    new       | if p>= tweet.length{ 
    font      |   if no next tweet, p=0
    from      |   else renew current tweet by the next and p=0
    arduino   | }
              |
              v
  at the Arduino
      if new font received,
      put the received font at lp, lp++;
      if(lp>=vfmax) lp=0;  
              
   dp (displaying position) = x/8
  
   initial ... x=0, lp=0, dp=0
   
  if lp=dp-1 {
     ask no font
  }
  else{
     ask new font
  }
  
 
                      Android ADK
                         | 'f', [font kind (0 or 1)], f[32]
                         v
                      +-------+
                      | 32byte| or 16 byte
   dp=x/8  char       +-------+
        |                | putFontAt ( char position)
        |                |
        |                | lp ... last received font position
        v                v
+-----------------------------------+ vfmax =VWMax/16
|     virtual matrix                |
|     width: 6x16= 96 dot           |
|     height: 16 dot                |
+-----------------------------------+
        |      |
        |x     |x+31
+-------+      |     copyVirtualPart2Real, x++,
|              |     if x>= VWMax, x=0;
v              v
****************   real matrix
**************** ^
**************** |
   ...           16 dot
**************** |
**************** v
****************
****************
 <--32 dot->

*/

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
//  printf("end clearVirtualGraphicsArea\n");
}

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

AndroidAccessory acc("Google, Inc.",
		     "uchiwaDe3D_ex1",
		     "Arduino Board for uchiwaDe3D_ex1",
		     "1.0",
		     "http://www.android.com",
		     "0000000012345000");
void setup();
void loop();

int realX;
int iter;
int dir; // last received font position on the virtualGraphicsArea
int dp; // current bitmap depth 
void setup()
{
  init_c2i();
   Serial.begin(57600);
   Serial.print("\r\nStart\n\r");
   delay(5); // add 2012 12/9
   Serial.print("acc.powerOn Start\n\r");
   acc.powerOn();
   Serial.print("acc.powerOn\n\r");
   matrix.begin();
   matrix.setBrightness(40); 
   backGround=matrix.Color(0,0,0); 
   initialBmp3D();
  /* */
  Serial.print("setup end\n");
  realX=0;
  dir=0;
  dp=0;
}


void readAcc(){
  byte inMsg[1024];
//     Serial.print("acc is connected\n"); //**
     int len = acc.read(inMsg, sizeof(inMsg), 1);
     int i,j;
     byte b;
     if(len<=0){
       return;
     }
//     Serial.print("inMsg[0]="); Serial.print(inMsg[0]); Serial.print("\n\r"); //**
     if(inMsg[0]=='a'){
     }
     else
     if(inMsg[0]=='d'){
     }
       /* */
     else
     if(inMsg[0]=='b'){
         byte dc=inMsg[2];
         int k=dc-'0';
         if(k>9) k=dc-'a'+10;
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
      /* */  
}

void loop()
{
  byte outMsg[4];
  if(iter>=16){
    if (acc.isConnected()) {
      readAcc();
     }
     iter=0;
     dp=0;
  }
  if(dp<=0){
    dir=0;
    dp=0;
  }
  if(dp>=6){
    dir=1;
    dp=6;
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
   delay(5);

   if(dir==0) dp++;
   if(dir==1) dp--;
}

