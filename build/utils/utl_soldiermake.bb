Include "bbgcolorspace.bb"

Global bw=40
Global bh=160
Global bh0=40

RotateSoldier("running1.bmp")
RotateSoldier("running2.bmp")

Function RotateSoldier(name$)

	aimg=LoadAnimImage("src2d\soldier\"+name$,40,40,0,4) 
	
	large=CreateImage(bw,bh*16)
	largebf=ImageBuffer(large)
	
	MaskImage(aimg,0,0,0)
	
	SetBuffer largebf
	
	ClsColor 253,67,251
	Cls 
	DrawImage(aimg,0,0,0)
	DrawImage(aimg,0,40,1)
	DrawImage(aimg,0,80,2)
	DrawImage(aimg,0,120,3)
	
	
	For t=2 To 16
		a#=t-1
		a=a*22.5
		newimage=CopyImage(aimg)
		MaskImage(newimage,0,0,0)
		
		ClsColor 0,0,0
		RotateImage (newimage,a)
	
	
		w=ImageWidth(newimage)
		h=ImageHeight(newimage)
	
		xoff=-((bw-w)/2)
		yoff=-((bh0-h)/2)
		HandleImage(newimage,xoff,yoff)
	
		SetBuffer largebf
	
		y=(t-1)*bh
		
		DrawImage(newimage,0,y,0)
		DrawImage(newimage,0,y+40,1)
		DrawImage(newimage,0,y+80,2)
		DrawImage(newimage,0,y+120,3)
		
		Color 255,255,255
		
		FreeImage newimage
	Next 
	
	SaveImage large,"data\sprites\"+name$
	FreeImage large 

End Function 