Include "bbgcolorspace.bb"

img=LoadImage("data\tiles\tiles.png") 

;road=LoadImage("Data\tiles\roadblock.bmp")
;MaskImage road,255,0,255

Include "bbgcolorspace.bb"

r1=bbcs_r1_old
g1=bbcs_g1_old
b1=bbcs_b1_old
r2=bbcs_r2_old
g2=bbcs_g2_old
b2=bbcs_b2_old

c1=r1+(g1*256) + (b1*256*256)
c2=r2+(g2*256) + (b2*256*256)

r1=bbcs_r1
g1=bbcs_g1
b1=bbcs_b1
r2=bbcs_r2
g2=bbcs_g2
b2=bbcs_b2

bw=128
bh=128

imgbuffer=ImageBuffer(img)

Flip1=0

SetBuffer imgbuffer

For x=0 To 2048 Step 128
For y=0 To 768 Step 128
	flip1=1-flip1

	If(flip1=0) Then 
				rr=r1
				gg=g1
				bb=b1
	Else 
				rr=r2
				gg=g2
				bb=b2
	EndIf 
	
	For yy=0 To 127 Step 1
		For xx=0 To 127 Step 1

		xxx=xx+x
		yyy=yy+y
		
		GetColor(xxx,yyy)
		r=ColorRed()
		g=ColorGreen()
		b=ColorBlue()
		c=r+(g*256) + (b*256*256)
		
		If(c=c1 Or c=c2) Then
			Color rr,gg,bb
			Plot xxx,yyy			
		EndIf 
			
		Next 
	Next 
	;Rect x,y,128,128,1
	;t=t+1
	
Next 
Next 

SaveImage img,"Data\tiles\tiles_test.bmp"