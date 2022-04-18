Const resolution=2

Include "bbgcolorspace.bb"

MakeMask("data\tiles\tiles_mask.src.png","data\tiles\tiles_mask.bmp",1)
;MakeMask("data\sprites\tankanim.bmp","data\sprites\tankanim_mask.bmp",0)

Function makemask (srcfile$,dstfile$,mode)

blocks=LoadImage(srcfile$) 
blocksbuffer=ImageBuffer(blocks)
img2=CreateImage(ImageWidth(blocks)/resolution,ImageHeight(blocks)/resolution)
img2buf=ImageBuffer(img2)

r1=bbcs_r1
g1=bbcs_g1
b1=bbcs_b1
r2=bbcs_r2
g2=bbcs_g2
b2=bbcs_b2

For x=0 To ImageWidth(blocks) Step resolution
For y=0 To ImageHeight(blocks) Step resolution
	x2=x/resolution
	y2=y/resolution


	SetBuffer blocksbuffer	
	GetColor(x,y)
	r=ColorRed() : g=ColorGreen() : b=ColorBlue() 
	
	If mode=1 Then 
		If( (r=r1 And g=g1 And b=b1) Or (r=r2 And g=g2 And b=b2) Or (r=112 And g=19 And b=35)) Then
			;
		Else
			SetBuffer img2buf
			Color 255,255,255
			Plot x2,y2
		EndIf 
	Else 
		If( (r=253 And g=67 And b=251)) Then
			;
		Else
			SetBuffer img2buf
			Color 255,0,0
			Plot x2,y2
		EndIf 		
	EndIf 
	
Next 
Next 

SaveImage(img2,dstfile$)

FreeImage blocks
FreeImage img2

End Function 