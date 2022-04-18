Include "..\..\lib\v1.0\gfx_fontlib.bb"
Include "..\..\lib\v1.0\gfx_images.bb"
;Include "..\lib\v1.1\highscore_lib.bb"
Include "..\..\lib\v1.0\keysdef.bb"
Include "bbgcolorspace.bb"

Const c_width=640
Const c_height=480

Const c_width2=320
Const c_height2=240
Const c_color1=32
Const c_color2=24
Const c_color3=16
Const c_mode=0
Const c_FPS=80
Const g_maximagesinsequence = 100

Const g_max3dsprites = 100 

Global g_FPSTIME#
Global g_lastmilliseconds
Global ground

Global g_shell
Global g_shell_mask


g_FPSTIME#=1000/c_FPS


Global movefactor#

Global g_nr_of_blocks=96

Dim imgblocks(1024)
Dim imgblocks_msk(1024)
Global block_cnt

;Dim blockptr(256)
;Global blockptr_max

Global tankimg
Global tankimg2
Global tankanim_damaged
Global tankimg_shadow
Global tankimg_msk

Global cannonimg
Global cannonimg_shadow
Global canonroof_green
Global canonroof_beige
Global canonroof_shadow

Global door_green
Global door_beige

Global flagimg1
Global flagimg2

Const c_road=16

Global g_lighttexture
Global g_smoketexture

Global gsfx_explosion1
Global gsfx_explosion2
Global gsfx_explosion3
Global gsfx_explosion4
Global gsfx_explosion5
Global gsfx_cannon
Global gsfx_mbang 
Global gsfx_tengine1

Dim floorcodetable(128)





Global frameTimer

Global g_camera

;shadow mask color
Global rs=112
Global gs=19
Global bs=35

Global titlefont.df_font
Global ingamefont1.df_font
Global ingamefont2.df_font
Global ingamefont3.df_font

Type blockdef
	Field image
	Field mskimage
	
	Field floorimage ;road, etc
	Field code
	
	Field blocktype 
	Field damage
	Field status
	
	Field overlayoffsetx
	Field overlayoffsety

	;Field effectlast
	Field effect

	Field effect2last
	Field effect2

	Field hasflag
	
	Field debug	
	Field debug1$
	Field debug2$
	
	Field alliedcode
	
	
	
End Type

Type blockpos

	Field xindex
	Field yindex

End Type

;Dim bytes(256)
Dim worldblks.blockdef(playfieldw-1,playfieldh-1)

Function InitLoad()


	DebugLog "c_FPS "+c_FPS 

	movefactor#=50
	movefactor#=movefactor/c_FPS 
	
	DebugLog "movefactor "+movefactor# 
	
		
	modecnt=0
    intModes=CountGfxModes()
	done=0
	t=1
	While t<=intModes
        If(GfxModeWidth(t)=c_width And GfxModeHeight(t)=c_height); And GfxModeDepth(t)=g_depth)
			Graphics3D c_width,c_height,c_color1,c_mode
			done=1
			Exit
        EndIf
		t=t+1
	Wend 

	t=1
	While t<=intModes And done=0
        If(GfxModeWidth(t)=c_width And GfxModeHeight(t)=c_height); And GfxModeDepth(t)=g_depth)
			Graphics3D c_width,c_height,c_color2,c_mode
			done=1
			Exit
        EndIf
		t=t+1
	Wend 

	t=1
	While t<=intModes And done=0
        If(GfxModeWidth(t)=c_width And GfxModeHeight(t)=c_height); And GfxModeDepth(t)=g_depth)
			Graphics3D c_width,c_height,c_color3,c_mode
			done=1
			Exit
        EndIf
		t=t+1
	Wend 
	
	If(done=0)
		Print "Could not open 640x480x16 or 640x480x32 video mode, sorry"
		End
	EndIf 
	
;Graphics c_width,c_height,c_colors,c_mode
HidePointer

SeedRnd MilliSecs() 

For x=0 To playfieldw-1

	For y=0 To playfieldh-1
		worldblks(x,y)=New blockdef
	Next 
	
Next 


	g_camera = CreateCamera()
	CameraClsMode g_camera,False,False


;imgblocks(0)=LoadImage ("data\tiles\ground01.png") 
;imgblocks(15)=LoadImage ("data\tiles\road15.png") 
;imgblocks(16)=LoadImage ("data\tiles\road16.png") 
;imgblocks(13)=LoadImage ("data\tiles\road13.png") 
;imgblocks(11)=LoadImage ("data\tiles\road11.png") 
;imgblocks(7 )=LoadImage ("data\tiles\road07.png") 
;imgblocks(5 )=LoadImage ("data\tiles\road05.png") 
;imgblocks(8 )=LoadImage ("data\tiles\road08.png")
;imgblocks(6 )=LoadImage ("data\tiles\road06.png") 
;imgblocks(12)=LoadImage ("data\tiles\road12.png") 
;imgblocks(10)=LoadImage ("data\tiles\road10.png") 
;imgblocks(4 )=LoadImage ("data\tiles\road04.png") 
;imgblocks(65)=LoadImage ("data\tiles\ground65.png") 
;imgblocks(70)=LoadImage ("data\tiles\ground70.png") 
;imgblocks(66)=LoadImage ("data\tiles\ground66.png") 
;imgblocks(64)=LoadImage ("data\tiles\ground64.png") 
;imgblocks(71)=LoadImage ("data\tiles\ground71.png") 
;imgblocks(73)=LoadImage ("data\tiles\ground73.png") 
;imgblocks(87)=LoadImage ("data\tiles\ground87.png") 


blocks=LoadAnimImage("data\tiles\tiles.png",128,128,0,g_nr_of_blocks) 
blocks_msk=LoadAnimImage("data\tiles\tiles_mask.bmp",64,64,0,g_nr_of_blocks) 

r1=bbcs_r1
g1=bbcs_g1
b1=bbcs_b1
r2=bbcs_r2
g2=bbcs_g2
b2=bbcs_b2

workimg=CreateImage(128,128)
workimg1=CreateImage(128,128)
workimg2=CreateImage(128,128)
SetBuffer ImageBuffer(workimg1)
Color r1,g1,b1
Rect 0,0,128,128,1

SetBuffer ImageBuffer(workimg2)
Color r2,g2,b2
Rect 0,0,128,128,1

;SetBuffer ImageBuffer(workimg2)
;Color r2,g2,b2
;Rect 0,0,128,128,1

transp=0
For x=0 To 127
transp=1-transp
For y=0 To 127
transp=1-transp
	If(transp=1) Then
		SetBuffer ImageBuffer(workimg1)
		Color 0,0,0
		Plot x,y
		SetBuffer ImageBuffer(workimg2)
		Color 0,0,0
		Plot x,y		
	EndIf 
Next 
Next 

If (editscale=1) Then
	SetBuffer BackBuffer()
	
	ClsColor 0,0,0
	Cls
	Color 0,0,255
	Text c_width2,c_height2,"Scaling images, please wait",True , True 
	Flip
	
	ClsColor 0,0,0
	Cls
	Color 0,0,255
	Text c_width2,c_height2,"Scaling images, please wait",True , True 
		
EndIf 

For t=0 To g_nr_of_blocks-1
	
	flip2=(t/16) Mod 2
	flip1=1-flip1
	myflip=flip1
	If(flip2=1) Then myflip=1-myflip	
	
	SetBuffer ImageBuffer(workimg)
	DrawBlock  (blocks,0,0,t)
	MaskImage workimg,rs,gs,bs
	
	img=CreateImage(128,128)
	imgbuf=ImageBuffer(img)	
	SetBuffer imgbuf
	If(myflip=0) Then 
		DrawBlock  (workimg1,0,0)
	Else
		DrawBlock  (workimg2,0,0)
	EndIf 

	DrawImage  (workimg,0,0)
	
	If (editscale=1) Then 
		ScaleImage 	img,edt_blockw/128.0,edt_blockh/128.0
	EndIf 
	;imginfo$(t)="f="+myflip+ "  F1="+flip1+" F2="+flip2+" F15="+flip15+"    "
	
	If(myflip=0) Then 
		MaskImage img,r1,g1,b1
	Else 
		MaskImage img,r2,g2,b2
	EndIf 
	
	;Color 255,0,0
	;Rect 0,0,128,128,0
	;Color 0,0,0
	
	If(debugcollission=1) Then 
		Color 255,255,255
		Text 0,0,t
	EndIf 
	
	imgblocks(t)=img
	
	img=CreateImage(64,64)
	imgbuf=ImageBuffer(img)
	SetBuffer imgbuf
	DrawImage(blocks_msk,0,0,t)
	MaskImage img,0,0,0
	imgblocks_msk(t)=img
	   
Next 

FreeImage workimg
FreeImage workimg1
FreeImage workimg2

ground=LoadImage("data\tiles\groundtile0.png")

SetBuffer BackBuffer ()



tankimg=LoadAnimImage ("data\sprites\tankanim.bmp",64,64,0,16) 
HandleImage tankimg,ImageWidth(tankimg)/2,ImageHeight(tankimg)/2
SetBuffer ImageBuffer(tankimg)
GetColor(0,0)
MaskImage tankimg, ColorRed(), ColorGreen(), ColorBlue()

tankanim_damaged=LoadAnimImage ("data\sprites\tankanim-damaged.bmp",64,64,0,16) 
HandleImage tankanim_damaged,ImageWidth(tankanim_damaged)/2,ImageHeight(tankanim_damaged)/2
SetBuffer ImageBuffer(tankanim_damaged)
GetColor(0,0)
MaskImage tankanim_damaged, ColorRed(), ColorGreen(), ColorBlue()


tankimg2=LoadAnimImage ("data\sprites\tankanim2.bmp",64,64,0,16) 
HandleImage tankimg2,ImageWidth(tankimg2)/2,ImageHeight(tankimg2)/2
SetBuffer ImageBuffer(tankimg2)
GetColor(0,0)
MaskImage tankimg2, ColorRed(), ColorGreen(), ColorBlue()


tankimg_shadow=LoadAnimImage ("data\sprites\tankanim_shadow.bmp",64,64,0,16) 
HandleImage tankimg_shadow,ImageWidth(tankimg_shadow)/2,ImageHeight(tankimg_shadow)/2
SetBuffer ImageBuffer(tankimg_shadow)
GetColor(0,0)
MaskImage tankimg_shadow, ColorRed(), ColorGreen(), ColorBlue()


tankimg_msk=LoadAnimImage ("data\sprites\tankanim_mask.bmp",32,32,0,16) 
HandleImage tankimg_msk,ImageWidth(tankimg_msk)/2,ImageHeight(tankimg_msk)/2
SetBuffer ImageBuffer(tankimg_msk)
GetColor(0,0)
MaskImage tankimg_msk, ColorRed(), ColorGreen(), ColorBlue()

g_shell=LoadImage ("data\sprites\shell.bmp")
SetBuffer ImageBuffer (g_shell)
GetColor 0,0
MaskImage g_shell,ColorRed(),ColorGreen(),ColorBlue()
HandleImage g_shell,ImageWidth (g_shell)/2, ImageHeight (g_shell)/2

g_shell_mask=LoadImage ("data\sprites\shell_mask.bmp")
SetBuffer ImageBuffer (g_shell_mask)
GetColor 0,0
;MaskImage g_shell_mask,ColorRed(),ColorGreen(),ColorBlue()
HandleImage g_shell_mask,ImageWidth (g_shell_mask)/2, ImageHeight (g_shell_mask)/2


;cannon
xoff=-((128/2) - 62/2)
yoff=xoff
cannonimg=LoadAnimImage ("data\sprites\cannonanim.bmp",62,62,0,16) 
HandleImage cannonimg,xoff,yoff
SetBuffer ImageBuffer(cannonimg)
GetColor(0,0)
MaskImage cannonimg, ColorRed(), ColorGreen(), ColorBlue()

cannonimg_shadow=LoadAnimImage ("data\sprites\cannonanim_shadow.bmp",62,62,0,16) 
HandleImage cannonimg_shadow,xoff,yoff
SetBuffer ImageBuffer(cannonimg_shadow)
GetColor(0,0)
MaskImage cannonimg_shadow, ColorRed(), ColorGreen(), ColorBlue()

canonroof_green=LoadImage ("data\sprites\cannonroof_green.bmp")
HandleImage canonroof_green,0,0
SetBuffer ImageBuffer (canonroof_green)
GetColor 0,0
MaskImage canonroof_green, ColorRed(), ColorGreen(), ColorBlue()

canonroof_beige=LoadImage ("data\sprites\cannonroof_beige.bmp")
HandleImage canonroof_beige,0,0
SetBuffer ImageBuffer (canonroof_beige)
GetColor 0,0
MaskImage canonroof_beige, ColorRed(), ColorGreen(), ColorBlue()

canonroof_shadow=LoadImage ("data\sprites\cannonroof_shadow.bmp")
HandleImage canonroof_shadow,0,0
SetBuffer ImageBuffer (canonroof_shadow)
GetColor 0,0
MaskImage canonroof_shadow, ColorRed(), ColorGreen(), ColorBlue()

door_green=LoadImage ("data\sprites\door_green.bmp")
HandleImage door_green,0,0
SetBuffer ImageBuffer (door_green)
GetColor 0,0
MaskImage door_green, ColorRed(), ColorGreen(), ColorBlue()
ShadowImage(door_green,rs,gs,bs,ColorRed(), ColorGreen(), ColorBlue())

door_beige=LoadImage ("data\sprites\door_beige.bmp")
HandleImage door_beige,0,0
SetBuffer ImageBuffer (door_beige)
GetColor 0,0
MaskImage door_beige, ColorRed(), ColorGreen(), ColorBlue()
ShadowImage(door_beige,rs,gs,bs,ColorRed(), ColorGreen(), ColorBlue())

flagimg1=LoadImage ("data\sprites\flag1.bmp")
HandleImage flagimg1,ImageWidth(flagimg1)/2,ImageHeight(flagimg1)/2
SetBuffer ImageBuffer (flagimg1)
GetColor 0,0
MaskImage flagimg1, ColorRed(), ColorGreen(), ColorBlue()

flagimg2=LoadImage ("data\sprites\flag2.bmp")
HandleImage flagimg2,ImageWidth(flagimg2)/2,ImageHeight(flagimg2)/2
SetBuffer ImageBuffer (flagimg2)
GetColor 0,0
MaskImage flagimg2, ColorRed(), ColorGreen(), ColorBlue()

gsfx_explosion1=LoadSound ("data\sfx\explosion.wav")
gsfx_explosion2=LoadSound ("data\sfx\explosion2.wav")
gsfx_explosion3=LoadSound ("data\sfx\explosion_small.wav")
gsfx_explosion4=LoadSound ("data\sfx\explosion_big.wav")
gsfx_explosion5=LoadSound ("data\sfx\explosion3.wav")
gsfx_cannon= LoadSound ("data\sfx\cannon4.wav")
gsfx_mbang= LoadSound ("data\sfx\METALBAN3.wav")

gsfx_tengine1= LoadSound ("data\sfx\tank-Engine.wav")
SoundVolume gsfx_tengine1,0.4


;pseudo 3d stuff
Init3DSprites(g_max3dsprites)
ClearTextureFilters
	
textflag=1+2
;TextureFilter "",1+8

g_lighttexture = LoadTexture("data\gfx\flare-red.png",textflag)
g_smoketexture = LoadTexture("data\gfx\smokey-grey.png",textflag)


;fonts
titlefont=InitFontDrawer("data\font\neonfont.png",-1,-1)
ingamefont1=InitFontDrawer("data\font\neonfont1.png",-1,-1)
ingamefont3=InitFontDrawer("data\font\neonfont2.png",-1,-1)

ingamefont2=InitFontDrawer("data\font\bigrbfont.bmp",-1,-1)

	
SetBuffer BackBuffer()
DebugLog "block_cnt"+block_cnt

FreeImage loadingpic

frameTimer=CreateTimer(50)

End Function 


Function LimitFPS()
	WaitTimer(frameTimer) 
End Function

;Function displayimageloading(texttext$)
;End Function 

Function ShadowImage(image,rs,gs,bs,r_bg,g_bg,b_bg)

w=ImageWidth (image)
h=ImageHeight(image)

transp=0
x=0
While x<w
transp=1-transp
y=0
While y<h
transp=1-transp
	GetColor(x,y)
	cr=ColorRed()
	cg=ColorGreen()
	cb=ColorBlue()
	
	If(cr=rs And cg=gs And cb=bs) Then  
	If(transp=0) Then
		Color r_bg,g_bg,b_bg
		Plot x,y		
	Else
		Color 0,0,0
		Plot x,y
	EndIf 
	
	EndIf 
	y=y+1
Wend
x=x+1
Wend 

End Function 