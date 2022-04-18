Include "bbgcolorspace.bb"

img=LoadImage("src2d\cannon.bmp") 

bw=62
bh=62

;imgbuffer=ImageBuffer(img)
;SetBuffer imgbuffer

large=CreateImage(bw,bh*16)
largebf=ImageBuffer(large)



;SaveImage img,"src2d\cannon_1.bmp"
MaskImage(img,0,0,0)

SetBuffer largebf


ClsColor 253,67,251
Cls 
DrawImage(img,0,0)



For t=2 To 16
	a#=t-1
	a=a*22.5
	newimage=CopyImage(img)
	MaskImage(newimage,0,0,0)
	
	ClsColor 0,0,0
	RotateImage (newimage,a)
	
	;SaveImage newimage,"src2d\cannon_"+t+".bmp"

	w=ImageWidth(newimage)
	h=ImageHeight(newimage)

	xoff=-((bw-w)/2)
	yoff=-((bh-h)/2)
	HandleImage(newimage,xoff,yoff)

	SetBuffer largebf

	y=(t-1)*bh
	
	DrawImage(newimage,0,y)
	
	Color 255,255,255
	;Text x,0,"w="+w+" h="+h
Next 

SaveImage large,"data\sprites\cannonanim.bmp"