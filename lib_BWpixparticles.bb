
Const c_maxpixcount=2500

Type pixpartdef0
	Field x#
	Field y#
	Field height,dalley
	Field dx#
	Field dy#
	
	Field used
	
	Field id
End Type 

Type pixpartdef
	Field x#
	Field y#
	Field height,dalley
	Field dx#
	Field dy#
	
	Field used
	
	Field id
End Type 

Function newSimplePixParticle0.pixpartdef0(x#,y#,dx#,dy#, id,height,dalley)


	count=0
	found=0
	For b0.pixpartdef0= Each pixpartdef0
			
		If(b0\used=0) Then
			
				found=1
				b.pixpartdef0=b0
				Exit 
			
		EndIf 
		count=count+1
			
	Next 


	If(found=1) Then
		b0=b
	Else
		If(count < c_maxpixcount) Then 
			b.pixpartdef0 = New pixpartdef0
		Else
	
			count=0
			rndfound= Rand(0,c_maxpixcount-1)
			
			For b0.pixpartdef0= Each pixpartdef0
			
				If(count=rndfound) Then	
					found=1
					b.pixpartdef0=b0
					Exit 
				Else
					count=count+1			
				EndIf 
			
			Next 
		count=count+1
			
	
		EndIf 		
	EndIf 
	
	newdbgx=x
	newdbgy=y
	
	b\x=x
	b\y=y
	b\dx=dx
	b\dy=dy

	b\height=height
	b\dalley=dalley

	b\used=1
	
	b\id=id
	Return b
	
End Function 

Function newSimplePixParticle.pixpartdef(x#,y#,dx#,dy#, id,height,dalley)


	count=0
	found=0
	For b0.pixpartdef= Each pixpartdef
			
		If(b0\used=0) Then
			
				found=1
				b.pixpartdef=b0
				Exit 
			
		EndIf 
		count=count+1
			
	Next 


	If(found=1) Then
		b0=b
	Else
		If(count < c_maxpixcount) Then 
			b.pixpartdef = New pixpartdef
		Else
	
			count=0
			rndfound= Rand(0,c_maxpixcount-1)
			
			For b0.pixpartdef= Each pixpartdef
			
				If(count=rndfound) Then	
					found=1
					b.pixpartdef=b0
					Exit 
				Else
					count=count+1			
				EndIf 
			
			Next 
		count=count+1
			
	
		EndIf 		
	EndIf 
	
	newdbgx=x
	newdbgy=y
	
	b\x=x
	b\y=y
	b\dx=dx
	b\dy=dy

	b\height=height
	b\dalley=dalley

	b\used=1
	
	b\id=id
	Return b
	
End Function 