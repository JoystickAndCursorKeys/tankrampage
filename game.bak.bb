Const playfieldw=64
Const playfieldh=64
Const blockw=100
Const blockh=100
Const blockwonscreen=c_width/blockw
Const blockhonscreen=c_height/blockw
Const screenw2=c_width/2
Const screenh2=c_height/2

Global lastblockx=-1
Global lastblocky=-1

Global thisblockx=-1
Global thisblocky=-1

Dim bytes(256)
Dim worldblks(playfieldw,playfieldh)

Function InitLevel(level)
	
	For x=0 To playfieldw
	
		For y=0 To playfieldh
			worldblks(x,y)=0
		Next 
	
	Next 
	
	For t=0 To 256
		bytes(t)=0
	Next 
	
;	map$="t"+level
;	filein = ReadFile(map$)
;	For x=0 To playfieldw-1
;		For y=0 To playfieldh-1
;			byte = ReadByte( filein )
;			bytes(byte)=bytes(byte)+1
;			;If(byte>(block_cnt-1)) Then byte=block_cnt-1
;			worldblks(x,y)=byte; Mod (block_cnt-1)
;		Next 
;	Next 
;	
;	CloseFile(filein)
;	
;	fileout=WriteFile("out."+map$+".txt");
;		 
;	For t=0 To 256
;		myLine$=ByteValtoString(bytes(t))+"    (" + t + ")"
;		If(bytes(t)>0) Then 
;			WriteLine  fileout, myLine$
;		EndIf 
;	Next 
;	
;	CloseFile (fileout)
	
End Function

Function ByteValtoString$(val)
	
	s$=val
	If(Len(s$)<3) Then s$=" "+s$
	If(Len(s$)<3) Then s$=" "+s$
	Return s$
End Function 

Function Game()

	level=1
	InitLevel(1)
	InitLevel(2)
	InitLevel(3)
	InitLevel(4)
	InitLevel(5)
	
	x=(playfieldw/2)*blockw
	y=(playfieldh/2)*blockh
	lastx=x
	lasty=y
	
	image=tankleft
	
	dx#=0
	dy#=0
	
	While Not KeyHit(1)
	
		dx=dx*.95
		dy=dy*.95
		
		If(KeyDown(key_cursup)) Then 
				dy=dy-.5
				image=tankup
		EndIf 

		If(KeyDown(key_cursdown)) Then 
				dy=dy+.5
				image=tankdown

		EndIf 
		
		If(KeyDown(key_cursleft)) Then 
				dx=dx-.5
				image=tankleft	
		EndIf 
		If(KeyDown(key_cursright)) Then 
				dx=dx+.5	
				image=tankright

		EndIf 

		If(Abs(dy)>Abs(dx)) Then
		
			If(dy<-0.1) Then 
					image=tankup
			Else 
				image=tankdown
			EndIf 
		Else
			If(dx<-0.1) Then 
				
				image=tankleft	
			Else
				
				image=tankright
			EndIf 
		EndIf 
		x=x+dx
		y=y+dy

		maxx=(playfieldw*blockw)
		maxy=(playfieldh*blockh)
		minx=1*blockw
		miny=1*blockh
		
		If(x<minx) Then x=minx : dx=Abs(dx)/2
		If(y<miny) Then y=miny : dy=Abs(dy)/2
		If(x>maxx) Then x=maxx : dx=-Abs(dx)/2
		If(y>maxy) Then y=maxy : dy=-Abs(dy)/2

		

		ClsColor 0,0,0
		Cls 
		DrawWorld(x,y)
		
		angle=Int(ATan2(dx#,dy#))
		angle=(angle+180) Mod 360
		frame=Int(((angle*8)/360) )
		

		Color 255,0,0
	    DrawImage(tankimg,screenw2,screenh2,frame)
	    Text 0,0, " x="+x+"  y="+y
		Text 0,10, " frame="+frame+ " ang " + angle
				
		If(lastblockx<>thisblockx Or lastblocky<>thisblocky)
			newblock=worldblks(thisblockx ,thisblocky)
			
			illegalblock=0
			If(newblock=12121210) Then 
			
				illegalblock=1
				
				x=lastx
				y=lasty
				
				;dx=0
				;dy=0
				dx=-((dx)/2)
				dy=-((dy)/2)

				
			Else
				lastblockx=thisblockx 
				lastblocky=thisblocky 				
			EndIf 
			
			
		EndIf 




		lastx=x
		lasty=y
		LimitFPS()
		Flip 
	Wend 

End Function


Function drawworld(xxpl,yypl)


	xx=xxpl-screenw2
	yy=yypl-screenh2
	
	basesrcy=yy/blockh
	basesrcx=xx/blockw
	offy=((basesrcy)*blockh)-yy
	offx=((basesrcx)*blockh)-xx
	
	
	For y=0 To blockhonscreen+1
	
		
		blockposy=offy + y*blockh
		
		

		srcy=basesrcy+y
		
		ymatch=((yypl / blockh) = srcy)
		If(ymatch) Then thisblocky=srcy
		
		
		If((srcy)<0 Or (srcy)>playfieldh) Then 
			;
		Else
			For x=0 To blockwonscreen+1
				
				srcx=basesrcx+x

				blockposx=offx + x*blockw
				
				xmatch=((xxpl / blockw) = srcx)
				If(xmatch) Then thisblockx=srcx
				
				If((srcx)<0 Or (srcx)>playfieldw) Then 
					;
				Else 
					blockimg=worldblks(srcx,srcy)
					;DrawBlock imgblocks(blockimg),blockposx,blockposy
					
					bDrawBlock=False
					
					Select blockimg				
					Case 15
						bDrawBlock=True
					Case 16
						bDrawBlock=True
					Case 13
						bDrawBlock=True
					Case 11
						bDrawBlock=True
					Case 5
						bDrawBlock=True
					Case 7
						bDrawBlock=True
					Case 8
						bDrawBlock=True
					Case 12
						bDrawBlock=True
					Case 6
						bDrawBlock=True
					Case 10
						bDrawBlock=True
						
						
						
					Case 65
						bDrawBlock=True
					Case 70
						bDrawBlock=True
					Case 66
						bDrawBlock=True
					Case 64
						bDrawBlock=True
					Case 71
						bDrawBlock=True
					Case 73
						bDrawBlock=True
					Case 87
						bDrawBlock=True
						
						
					End Select 
					
					If(bDrawBlock) Then
						DrawBlock imgblocks(blockimg),blockposx,blockposy
					Else
					    co1l=(blockimg Mod 10) * 25
						col2=((255-blockimg) Mod 25) * 10
						col3=(blockimg Mod 100) * 2	
	
						DrawBlock imgblocks(0),blockposx,blockposy

						Color col1 ,col2,col3			
						Rect blockposx+20,blockposy+20,60,60,1						
						
						Color 255,255,255
						Text blockposx,blockposy," "+blockimg
						Text blockposx,blockposy+10," "+srcx+","+srcy

					EndIf 
		
					Color 255,0,0
					If(ymatch And xmatch) Then
						Rect blockposx+2,blockposy+2,98,98,1
					EndIf 
				EndIf 
			Next 
		EndIf 
		
	Next 
End Function 