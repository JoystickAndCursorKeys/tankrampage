Const csender_tank1=0
Const csender_tank2=1
Const csender_building1=4
Const csender_building2=5


Type bulletdef
	Field x#
	Field y#
	Field height
	Field dx#
	Field dy#
	
	Field sender
	Field used
	
	Field lifetime
	Field dec
	
	Field id
End Type 

Function newSimpleBullet.bulletdef(x#,y#,dx#,dy#, id)


	count=0
	found=0
	For b0.bulletdef= Each bulletdef
			
		If(b0\used=0) Then
			
				found=1
				b.bulletdef=b0
				Exit 
			
		EndIf 
		count=count+1
			
	Next 


	If(found) Then
		b0=b
	Else
		b.bulletdef = New bulletdef
		
	EndIf 
	
	newdbgx=x
	newdbgy=y
	
	b\x=x
	b\y=y
	b\dx=dx
	b\dy=dy

	
	dist#=Sqr ( (b\dx*b\dx) + (b\dy*b\dy))
	If(dist<1) Then 
		dist=1	
	EndIf 
	b\lifetime=450/dist
	

	b\height=200
	
	b\used=1
	
	b\id=id
	Return b
	
End Function 