tankimg_shadow=LoadImage ("data\sprites\cannonroof_green.bmp") 
SetBuffer ImageBuffer(tankimg_shadow)

GetColor(0,0)
rt=ColorRed()
gt=ColorGreen()
bt=ColorBlue()

For t=0 To 7
	SetBuffer ImageBuffer(tankimg_shadow)
	transp=0
	For x=0 To 127
	transp=1-transp
	For ly=0 To 127
		y=t*128 + ly

		transp=1-transp
		
		
		GetColor(x,y)
		If(Not (rt=ColorRed() And gt=ColorGreen() And bt=ColorBlue())) Then 
			If(transp=1) Then
				Color 0,0,0
				Plot x,y
			Else 
				Color rt,gt,bt
				Plot x,y	
			EndIf 
		EndIf 
	Next 
Next 
Next 

SaveImage (tankimg_shadow,"data\sprites\cannonroof_shadow.bmp")