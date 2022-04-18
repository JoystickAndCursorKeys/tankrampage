Include "loading.bb"
Include "title.bb"

Include "game.bb"

Include "gameover.bb"
Include "loading.bb"
Include "lib_BWbullets.bb"
Include "lib_BWpixparticles.bb"
Include "../..\lib/v1.0/keysdef.bb"
Include "../..\lib/v1.3/lib_gfx3dsprites.bb"


Const c_trace=0
Global g_debugstop=0
Const debugcollission=0
Global firsttime=1

Global cop_a1
Const cop_a1step=-5
Const cop_a1step2=2

Global cop_a2
Const cop_a2step=11
Const cop_a2step2=3

Global cop_a3
Const cop_a3step=2
Const cop_a3step2=6

Global cop_a4
Const cop_a4step=-10
Const cop_a4step2=23
Const xStep=4

Global coppersbufferimg
Global coppersbuffer

Const c_copheight=100
Const c_cheightextra=80
Const c_cheight=(c_copheight+c_cheightextra)

Global copperimage 
Global copperimage2
Global copperimage3

Global titleimage
;
; ideas
;  weather, sandstorm, cloud shadows, wind (tumbleweeds)
;  shake whole viewport, when collissions or big explosions
;  trophies - when shootinh down a building, a trophy can be collected from the 
;     building (some buildings)
;  cant blow up flag house or garage
;  make visible tank has flag
;  dashboard
;  choose tank
; when tank blows up, leave alot of dirt(=track) particles behind
; make cannan bullets explode at estimated distance of (you from cannon), also the explode noise
; make tank moove pixel backwards when shooting
; idea, different colour palletes  (when it gets darker)


;todo
;  
; clear the border issues (shooting, particles, etc)
;
; network play -  multiple tanks (more then two)
;
; better "rubble" gfx
; fix steering, so it becomes much harder to get stuck
; harder to shoot stuff
; helicopters
;
; editor for editing masks, w/o destroying previous work
;
; You can drive over the desert but this slows the tank down


;-----------------------------------------------------------
;remember
; changing beige buildings to green
; Hue/Sat/Lightness -> Hue36  , Sat -35
;
;
;
;


InitLoad()

Cls
SetBuffer FrontBuffer()
Color 255,255,255
;

While 1=1
	
	quit=Title()
	If(1=quit) Then End 
	
	Game()

Wend 

Function displayimageloading(str1$)
End Function 


Function Title()

	
	
	CoppersInit()
	titleimage=LoadImage ("data\gfx\titlebg.png")
	
	While 1=1
		
		SetBuffer BackBuffer()
		;ClsColor 0,0,0
		;Cls 
		
		DrawBlock titleimage,0,0
		
		SetBuffer ImageBuffer(copperimage2)
		CoppersArea(0,0)
		DrawImage copperimage ,0,0
		SetBuffer BackBuffer()
		DrawImage copperimage3,0,150
		DrawImage copperimage2,0,150

		;DrawBlock title,0,0
		
		If(KeyHit(1)) Then Return 1
		If(KeyHit (key_space)) Then
			Return 0
		EndIf 
		
		myText$="PRESS SPACE TO START"
		DrawFontCenter 252, myText$ ,ingamefont1
		
		LimitFPS()
		Flip 
		
	Wend 
	
	CoppersFree()
	
End Function 


Function CoppersInit()

 cop_a1=0
 cop_a2=0

  
  coppersbufferimg=CreateImage (c_width,c_cheight)
  coppersbuffer=ImageBuffer(coppersbufferimg)

  copperimage=LoadImage ("data\gfx\titletext.png")
  w=ImageWidth(copperimage)
  h=ImageHeight(copperimage)
  MaskImage copperimage,255,255,255
 
  copperimage3=LoadImage ("data\gfx\titletext2.png")
  w=ImageWidth(copperimage3)
  h=ImageHeight(copperimage3)
  MaskImage copperimage3,255,255,255
   
  copperimage2=CreateImage (ImageWidth(copperimage),ImageHeight(copperimage))

End Function 



Function CoppersFree()

  FreeImage (coppersbufferimg)

End Function 

Function CoppersArea(x,y)
  
  oldbuffer=GraphicsBuffer()
  SetBuffer coppersbuffer

  cop_a1=(cop_a1+cop_a1step) Mod 360 
  cop_a2=(cop_a2+cop_a2step) Mod 360 
  cop_a3=(cop_a3+cop_a3step) Mod 360 
  cop_a4=(cop_a4+cop_a4step) Mod 360 
  Lcop_a1=cop_a1
  Lcop_a2=cop_a2
  Lcop_a3=cop_a3
  Lcop_a4=cop_a4

  For t=0 To c_cheight Step 1

	c0#=((Sin(Lcop_a1)+1)/2)*100
    Lcop_a1=Lcop_a1+cop_a1step2

	c1#=((Sin(Lcop_a2)+1)/2)*100
    Lcop_a2=Lcop_a2+cop_a2step2


	c2#=((Sin(Lcop_a3)+1)/2)*100
    Lcop_a3=Lcop_a3+cop_a3step2

	cx#=(c0+c1+c2)/3
	cx2#=100-cx#
	
	r#=(255 * cx) + (200*cx2)
	g#=(255 * cx) + (0*cx2)	
	b#=(0 * cx) + (0*cx2)
    

	;r#=cx*1.0
	;g#=cx*.7
	;b#=cx*.1;c2
	Color r/100,g/100,b/100
	
	Rect 0,t,c_width,1,1
	
  Next 

  SetBuffer oldbuffer


  For t=1 To c_width Step xStep
	yoff1#=((Sin(Lcop_a3)+1)/2)*(c_cheightextra*.5)    
	yoff2#=((Sin(Lcop_a4)+1)/2)*(c_cheightextra*.5)
	   
    Lcop_a3=Lcop_a3+cop_a3step2
    Lcop_a4=Lcop_a4+cop_a4step2
    CopyRect t,0+yoff1+yoff2,8,c_copheight,t+x,0+y,coppersbuffer,oldbuffer
  Next 

  
End Function 