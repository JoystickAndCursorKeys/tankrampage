
Const edt_blockwonscreen=c_width/edt_blockw
Const edt_blockhonscreen=c_height/edt_blockh
Const screenw2=c_width/2
Const screenh2=c_height/2

Global lastblockx=-1
Global lastblocky=-1

Global thisblockx=-1
Global thisblocky=-1

Dim bytes(256)
Dim worldblks2(playfieldw,playfieldh)
Dim worldblks3(playfieldw,playfieldh)
Dim worldblksdebug(playfieldw,playfieldh)

Dim Structures(3,16)
;Dim Structures(0,16)
;Dim Structures(1,16)
;Dim Structures(2,16)

;from
;
;Const c_left=1
;Const c_right=2
;Const c_up=4
;Const c_down=8
;
;To
;
;Const c_left=1
;Const c_right=2
;Const c_up=4
;Const c_down=8

Const c_up=1
Const c_down=2
Const c_left=4
Const c_right=8

Const structures_count=3
Dim structname$(structures_count)
Dim structimgs(structures_count)

Global oldfname$
Global mapbuffer

Function InitEditor()

	Structures(0,00)=0
	Structures(0,01)=16
	Structures(0,02)=16
	Structures(0,03)=16
	Structures(0,04)=15
	Structures(0,05)=13
	Structures(0,06)=7
	Structures(0,07)=10
	Structures(0,08)=15
	Structures(0,09)=11
	Structures(0,10)=5
	Structures(0,11)=8
	Structures(0,12)=15
	Structures(0,13)=12
	Structures(0,14)=6
	Structures(0,15)=9
	
	Structures(1,00)=0
	Structures(1,01)=47 ; 2x
	Structures(1,02)=47 ; 2x
	Structures(1,03)=47 ; 2x
	Structures(1,04)=42 ; 2x
	Structures(1,05)=50
	Structures(1,06)=43
	Structures(1,07)=85
	Structures(1,08)=42     ;1+4 1+8 2+4 2+8
	Structures(1,09)=48
	Structures(1,10)=41
	Structures(1,11)=85
	Structures(1,12)=42
	Structures(1,13)=85
	Structures(1,14)=85
	Structures(1,15)=85

	Structures(2,00)=0
	Structures(2,01)=47 ; 2x
	Structures(2,02)=47 ; 2x
	Structures(2,03)=47 ; 2x
	Structures(2,04)=42 ; 2x
	Structures(2,05)=73
	Structures(2,06)=66
	Structures(2,07)=87
	Structures(2,08)=42     ;1+4 1+8 2+4 2+8
	Structures(2,09)=71
	Structures(2,10)=64
	Structures(2,11)=87
	Structures(2,12)=42
	Structures(2,13)=87
	Structures(2,14)=87
	Structures(2,15)=87
			
	
	For x=0 To playfieldw-1
	
		For y=0 To playfieldh-1
			worldblks2(x,y)=0
			worldblks3(x,y)=0
			worldblksdebug(x,y)=0
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
;			worldblks2(x,y)=byte; Mod (block_cnt-1)
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

Function getname$(textstring$,old$)
 
 FlushKeys 

 SetBuffer FrontBuffer()
 ClsColor 0,0,0
 Color 255,255,255
 Cls 
 If(old$<>"") Then
  	Print "<Enter> for '"+old$+"'"
 EndIf 
 this$=Input (textstring$+": ")
 If(this$="") Then this$=old$
 SetBuffer BackBuffer()
 Return this$
End Function 


Function Load()
	
	
	fname$=getname("Please type the name of the file to load",oldfname$)
	oldfname$=fname$
	
	filename$="data/level/"+fname$+".blp"
	filename2$="data/level/"+fname$

	
	filein = ReadFile(filename$)
	If(Not filein) Then filein =ReadFile(filename2$)
	
	If(Not filein) Then 
		FlushKeys()
	 	SetBuffer FrontBuffer()
	 	ClsColor 0,0,0
	 	Color 255,0,0
	 	Cls 
		Print "Load failed, File "+fname$+" does not exist!"
		  Print "<Any Key> to continue"
	
		While  GetKey() =0 
		SetBuffer BackBuffer()
		Wend 
		Return 0
	EndIf 
	
	header$=""
	header$=header$+Chr(ReadByte( filein ))
	header$=header$+Chr(ReadByte( filein ))
	header$=header$+Chr(ReadByte( filein ))
	header$=header$+Chr(ReadByte( filein ))
	header$=header$+Chr(ReadByte( filein ))
	header$=header$+Chr(ReadByte( filein ))
	header$=header$+Chr(ReadByte( filein ))
	header$=header$+Chr(ReadByte( filein ))
	
	
	For x=0 To playfieldw-1
	
		For y=0 To playfieldh-1
			worldblks2(x,y)=0
			worldblks3(x,y)=0
			worldblksdebug(x,y)=0
		Next 
	
	Next
		
	
	SetBuffer mapbuffer
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer() 
			
	If(header$<>"blkmap00") Then
	
		Cls
		Print "Not a blockmap file, trying to load 64x64 bytemap instead"
		SeekFile(filein,0)
	

		For x=0 To playfieldw-1
			For y=0 To playfieldh-1
				byte = ReadByte( filein )
	
				worldblks2(x,y)=byte
			Next 
		Next 
	
	Else
	
		maps=ReadByte(filein)
		playfieldw=ReadInt(filein)
		playfieldh=ReadInt(filein)
			
		For x=0 To playfieldw-1
			For y=0 To playfieldh-1
				byte = ReadByte( filein )
				worldblks2(x,y)=byte
				byte = ReadByte( filein )
				worldblks3(x,y)=byte				
			Next 
		Next
					
	EndIf 
	
	CloseFile (filein)
	
	Return 1
	
End Function

Function Save()
	fname$=getname("Please type the name of the file to Save",oldfname$)
	oldfname$=fname$
	
	filename$="data/level/"+fname$+".blp"
	
	fileout=WriteFile (filename$)
	If(Not fileout) Then
			FlushKeys()
		 	SetBuffer FrontBuffer()
		 	ClsColor 0,0,0
		 	Color 255,0,0
		 	Cls 
			Print "Save failed, File "+fname$+" cannot be created!"
			  Print "<Any Key> to continue"
		
			While  GetKey() =0 
			SetBuffer BackBuffer()
			Wend 
			Return 0
	EndIf 
	
	;write header
	header$="blkmap00"
	For t=1 To Len(header$)
	
		code=Asc(Mid(header$,t,1))
		WriteByte fileout,code
	
	Next 
	
	WriteByte fileout,2 ;2 maps
	WriteInt fileout,playfieldw
	WriteInt fileout,playfieldh
	
	For x=0 To playfieldw-1
		For y=0 To playfieldh-1
			WriteByte ( fileout,worldblks2(x,y) )
			WriteByte ( fileout,worldblks3(x,y) )
		Next 
	Next 
	
	
	CloseFile(fileout) 
	
	Return 1
End Function

Function Editor()

	level=1
	InitEditor()

	
	x=(playfieldw/2)*edt_blockw
	y=(playfieldh/2)*edt_blockh
	lastx=x
	lasty=y
	
	image=tankleft
	
	dx#=0
	dy#=0
	
	blockset=0
	
	goneleft=0
	goneright=1
	goneup=0
	gonedown=0
	set=0
	

	map=CreateImage(playfieldw,playfieldh)
	mapbuffer=ImageBuffer(map)
	
	SetBuffer mapbuffer 
	Color 0 ,0,0
	Rect 0,0,playfieldw,playfieldh,1	
	SetBuffer BackBuffer()
	
	structname(0)="Roads"
	structimgs(0)=Structures(0,01)
	structname(1)="Walls1"
	structimgs(1)=Structures(1,01)
	structname(2)="Walls2"
	structimgs(2)=Structures(2,01)
	
	maxdraw=g_nr_of_blocks+structures_count
	curdraw#=0.0
	
	blink=0
	
	mode=0
	showcodes=0
	showgrid=0
	
	While Not (KeyHit(key_q) Or KeyHit(key_escape))
	
		speed=10
		

		If(KeyDown(key_f1)) Then 
				mode=2
	
		ElseIf(KeyHit(key_f2) And mode<>1) Then 
					curdraw=blockset
					mode=1				
		ElseIf(mode=2) Then 
			mode=0			
		EndIf 
		
		If(KeyHit(key_C)) Then
		
			showcodes=1-showcodes
		
		ElseIf(KeyHit(key_G)) Then
		
			showgrid=1-showgrid
		
		EndIf 
				
		If(mode=0) Then 
											
			If(KeyDown(key_ctrll) Or KeyDown(key_ctrlr)) Then
			
				If(KeyHit(key_cursup)) Then 
						blockset=blockset-1
						If(blockset<0) Then blockset=(structures_count+g_nr_of_blocks)-1
				ElseIf(KeyHit(key_cursdown)) Then 
						blockset=blockset+1
						If(blockset>=structures_count+g_nr_of_blocks) Then blockset=0
				ElseIf(KeyHit(key_1)) Then 
						blockset=0	
				ElseIf(KeyHit(key_2)) Then 
						blockset=0+structures_count	
														
				EndIf 
			
			Else
				

				If(KeyHit(key_L)) Then 
						 
						Load()	
															
				ElseIf(KeyHit(key_S)) Then 
						
						Save()
																			
				EndIf 

				
				If(KeyDown(key_shiftl) Or KeyDown(key_shiftl)) Then
					speed=speed*5	
				EndIf
				 
				If(KeyDown(key_cursup)) Then 
						dy=-speed
				ElseIf(KeyDown(key_cursdown)) Then 
						dy=speed
				ElseIf(KeyDown(key_cursleft)) Then 
						dx=-speed
				ElseIf(KeyDown(key_cursright)) Then 
						dx=speed	
				;ElseIf(KeyHit(key_space)) Then 
				;		set=1-set
				EndIf 	
			EndIf 
			
			If(KeyDown(key_space)) Then 
				set=1
			Else If(GetKey()=0)
				set=0
			EndIf 
			
			If(KeyDown(key_backspace)) Then 
				delblock=1
			Else If(GetKey()=0)
				delblock=0
			EndIf 			
						
			x=x+dx
			y=y+dy
	
			dx=0
			dy=0
					
			maxx=((playfieldw-1)*edt_blockw)
			maxy=((playfieldh-1)*edt_blockh)
			minx=0*edt_blockw
			miny=0*edt_blockh
			
			If(x<minx) Then x=minx : dx=Abs(dx)/2
			If(y<miny) Then y=miny : dy=Abs(dy)/2
			If(x>maxx) Then x=maxx : dx=-Abs(dx)/2
			If(y>maxy) Then y=maxy : dy=-Abs(dy)/2
	
			ClsColor 0,0,0
			Cls 
			
			DrawWorld(x,y,showcodes,showgrid)
			
			drawcursor(x,y,set)

			draw=0
			If(set=1 Or delblock=1) Then draw=1
			
			If(draw=1) Then
			
				If(set=1) Then
					thisblock=blockset
				Else
					thisblock=structures_count ; will be 0
				EndIf 
				
				If(blockset<=structures_count) Then 
					structtype=thisblock+1
					addstructure(x,y,structtype)
				Else
					block=thisblock-structures_count
					addblock(x,y,block)
					set=0
				EndIf 	
			EndIf 

			;preview block
			Color 0 ,0,0
			Rect 8,8,104,104,1
			Color 200 ,140,45
			Rect 10,10,100,100,1
			Color 200 ,140,45
			Rect 10,112,100,50,1		
					
			If(blockset<structures_count) Then
			
						imgix=structimgs(blockset)
						DrawImage imgblocks(imgix),10,10
						Color 255,255,255
						Text 10,10,"Structure:"
						Text 10,120," "+structname(blockset)
						
			Else
			
						imgix=blockset-structures_count
						DrawImage imgblocks(imgix),10,10
						Color 255,255,255
						Text 10,120,"Block:"+imgix
										
			EndIf 
					
			;overview map
			mapx=130
			mapy=10
			
			xx=x/edt_blockw
			yy=y/edt_blockh			

			If(draw=1) Then 
				
				SetBuffer mapbuffer 			
				
				If(worldblks2(xx,yy)>0) Then
						Color 255,255,255
						Plot xx,yy
				Else
						Color 0,0,0
						Plot xx,yy
				EndIf 
				
				SetBuffer BackBuffer()
							
			EndIf 
			
			Color 255 ,255,255
			Rect mapx-1,mapy-1,playfieldw+2,playfieldw+2,0			
			DrawBlock map,mapx,mapy
			Color blink,64,64
			blink=blink+12
			If(blink>255) Then blink=0
			Plot xx+mapx,yy+mapy
			
			
			Color 200 ,140,45
			Rect mapx,mapy+playfieldh+1,playfieldw,50,1
			Color 255,255,255			
			Text mapx,mapy+playfieldh+2,""+xx+","+yy+""	
			
			Text screenw2,2,"Cursor keys + space to draw"	
			Text screenw2,22,"Hold F1 for more keys"	
			
		Else If(mode=1) Then 
			
			Color 200 ,140,45	
			Cls
			
			Color 255,255,255
			Text c_width/2,10,"Select Tile"
			
			lastcurdraw#=curdraw#
			If(KeyDown(key_cursup)) Then 
					curdraw=curdraw-.1
					If(curdraw<0) Then curdraw=lastcurdraw
			ElseIf(KeyDown(key_cursdown)) Then 
					curdraw=curdraw+0.1
					If(curdraw>=structures_count+g_nr_of_blocks) Then curdraw=lastcurdraw
			ElseIf(KeyHit(key_cursright)) Then 
					curdraw=curdraw+12
					If(curdraw>=structures_count+g_nr_of_blocks) Then curdraw=lastcurdraw
			ElseIf(KeyHit(key_cursleft)) Then 
					curdraw=curdraw-12
					If(curdraw<0) Then curdraw=lastcurdraw
			ElseIf(Not KeyDown(key_f2)) Then 
					blockset=Floor (curdraw)
					mode=0		
					set=0
			EndIf
			
			xdraw=	(c_width/2)-50	
			
			For colum=1 To 7 
			
				coloffset=colum-4
				colcountoffset=(coloffset*12)
				curdraw2#=curdraw+colcountoffset
				xoffset=110*coloffset
				
				For tmpy= 1 To 12
				
					ix=(Floor(curdraw2)+tmpy)-2
					If(ix>=0) Then 
					
						offset#=1-(curdraw2-Floor (curdraw2))
						offset=((tmpy+offset)*128)-188
						
						If(ix=(Floor(curdraw2)) And coloffset=0) Then
						
							Color 255,0,0
							Rect xoffset+((xdraw)-2),offset-2,104,104
	
							Color 128,128,128
							Rect 	xoffset+(xdraw),offset,100,100
													
						EndIf 
						
						If(ix<structures_count) Then
							imgix=structimgs(ix)
							DrawImage imgblocks(imgix),xdraw+xoffset,offset
							Color 255,255,255
							Text xoffset+xdraw,offset,"Structure:"
							Text xoffset+xdraw,offset+20," "+structname(ix)
						Else
							If((ix-structures_count)<g_nr_of_blocks) Then 
								imgix=ix-structures_count
								DrawImage imgblocks(imgix),xoffset+xdraw,offset
								Color 255,255,255
								Text xoffset+xdraw,offset,"Block:"+imgix						
							EndIf 
						EndIf 
					
					EndIf 
				
			Next
			Next  
		
		Else 
		
			ClsColor 0,0,0
			Cls
			Color 255,255,255
		
			xpv=10
			ypv=20
			xoffset=100
			yincrease=20
			
			Color 255,255,255: Text xpv,ypv,"F1:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Show keys menu" 
			ypv=ypv+yincrease
			
			Color 255,255,255: Text xpv,ypv,"F2:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Select block screen" 
			ypv=ypv+yincrease				
							
														
			Color 255,255,255: Text xpv,ypv,"Cursor keys:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"move around playfield" 
			ypv=ypv+yincrease
			
			Color 255,255,255: Text xpv,ypv,"Space:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Hold to draw blocks" 
			ypv=ypv+yincrease
			
			Color 255,255,255: Text xpv,ypv,"Backspace:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Hold to delete block (draw block '0')" 
			ypv=ypv+yincrease	
					
			Color 255,255,255: Text xpv,ypv,"Control:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Hold with up or down to quickly choose block" 
			ypv=ypv+yincrease	
									
			Color 255,255,255: Text xpv,ypv,"Control + 1:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Hold to jump to first struct" 
			ypv=ypv+yincrease						
			
			Color 255,255,255: Text xpv,ypv,"Control + 2:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Hold to jump to first block" 
			ypv=ypv+yincrease	
									
			Color 255,255,255: Text xpv,ypv,"Shift:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Move around quickly (can be used together with space)" 
			ypv=ypv+yincrease
			
			Color 255,255,255: Text xpv,ypv,"C:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Show codes" 
			ypv=ypv+yincrease	
			
			Color 255,255,255: Text xpv,ypv,"G:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Show grid" 
			ypv=ypv+yincrease				
			
			Color 255,255,255: Text xpv,ypv,"S:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Save" 
			ypv=ypv+yincrease	

			Color 255,255,255: Text xpv,ypv,"L:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Load" 
			ypv=ypv+yincrease					
						
			Color 255,255,255: Text xpv,ypv,"Q:" 
			Color 200,200,255: Text xpv+xoffset,ypv,"Quit" 
			ypv=ypv+yincrease
									
						
		EndIf 
				
		LimitFPS()
		Flip 
	Wend 

	FreeImage map
	
End Function


Function drawworld(xxpl,yypl,showcodes,showgrid)


	xx=xxpl-screenw2
	yy=yypl-screenh2
	
	basesrcy=yy/edt_blockh
	basesrcx=xx/edt_blockw
	offy=((basesrcy)*edt_blockh)-yy
	offx=((basesrcx)*edt_blockh)-xx
	
	
	For y=0 To edt_blockhonscreen+1
	
		
		blockposy=offy + y*edt_blockh
		
		srcy=basesrcy+y
		
		ymatch=((yypl / edt_blockh) = srcy)
		If(ymatch) Then thisblocky=srcy
		
		
		If((srcy)<0 Or (srcy)>=playfieldh) Then 
			;
		Else
			For x=0 To edt_blockwonscreen+1
				
				srcx=basesrcx+x

				blockposx=offx + x*edt_blockw
				
				xmatch=((xxpl / edt_blockw) = srcx)
				If(xmatch) Then thisblockx=srcx
				
				If((srcx)<0 Or (srcx)>=playfieldw) Then 
					;
				Else 
					blockimg=worldblks2(srcx,srcy)
					;DrawBlock imgblocks(blockimg),blockposx,blockposy
					
					bDrawBlock=True
					
					Color 200 ,140,45			
					Rect blockposx,blockposy,edt_blockw,edt_blockh,1	
											
					
					If(blockimg<>-1) Then
						DrawImage imgblocks(blockimg),blockposx,blockposy
					EndIf 
					
					If(showcodes) Then
					
						Color 255,255,255
						;Text blockposx,blockposy+10,"X("+srcx+"),Y("+srcy+")"
						;Text blockposx,blockposy+10,"Dir   ("+worldblksdebug(srcx,srcy)+")"
						;Text blockposx,blockposy+30,"Struct("+worldblks3(srcx,srcy)+")"
						;Text blockposx,blockposy+50,"Image ("+worldblks2(srcx,srcy)+")"
						Text blockposx,blockposy,worldblks2(srcx,srcy)
					EndIf 
					
					If(showgrid) Then 
						
						Color 0,0,0
						Line  blockposx,blockposy,blockposx,blockposy+(edt_blockh-1)
						Line  blockposx,blockposy,blockposx+(edt_blockw-1),blockposy
						Plot blockposx,blockposy
						
					EndIf 
					
					;Text blockposx,blockposy+10," "+srcx+","+srcy					
		
 
				EndIf 
			Next 
		EndIf 
		
	Next 
End Function 

Function drawcursor(xxpl,yypl,set)


	xx=xxpl-screenw2
	yy=yypl-screenh2
	
	basesrcy=yy/edt_blockh
	basesrcx=xx/edt_blockw
	offy=((basesrcy)*edt_blockh)-yy
	offx=((basesrcx)*edt_blockh)-xx
	
	
	For y=0 To edt_blockhonscreen+1
	
		
		blockposy=offy + y*edt_blockh
		
		srcy=basesrcy+y
		
		ymatch=((yypl / edt_blockh) = srcy)
		If(ymatch) Then thisblocky=srcy
		
		
		If((srcy)<0 Or (srcy)>playfieldh) Then 
			;
		Else
			For x=0 To edt_blockwonscreen+1
				
				srcx=basesrcx+x

				blockposx=offx + x*edt_blockw
				
				xmatch=((xxpl / edt_blockw) = srcx)
				If(xmatch) Then thisblockx=srcx
				
				If((srcx)<0 Or (srcx)>playfieldw) Then 
					;
				Else 
							
					Color 255,0,0
					
					If(ymatch And xmatch) Then
						Rect blockposx,blockposy,2,edt_blockh-2,2
						Rect blockposx+(edt_blockw-2),blockposy,2,edt_blockw-2,2
						
						Rect blockposx,blockposy,edt_blockw-2,2,2
						Rect blockposx,blockposy+(edt_blockh-2),edt_blockw-2,2,2
						
						;Color 255,255,255
						;Text blockposx,blockposy+70,"x("+srcx+"),y("+srcy+")"						
						
					EndIf 
				EndIf 
			Next 
		EndIf 
		
	Next 
End Function 


Function addstructure(x,y,structure)

	xx=x/edt_blockw
	yy=y/edt_blockh
	

	worldblks3(xx,yy)=structure

	reRenderStructures()
	
End Function

Function addblock(x,y,block)

	xx=x/edt_blockw
	yy=y/edt_blockh
	

	worldblks2(xx,yy)=block
	worldblks3(xx,yy)=0

	;reRenderStructures()
	
End Function


Function getstructurecode(structtype,this)


	rc=0
	For t=1 To 15
		If(t=this) Then 
			If(structtype=1) Then
				rc= Structures(0,t)
			Else If(structtype=2) Then
				rc= Structures(1,t)
			Else If(structtype=3) Then
				rc= Structures(2,t)
			EndIf 
			
		EndIf 
	Next 
	
	Return rc 
End Function 

Function reRenderStructures()

	For x=0 To playfieldw
	
		For y=0 To playfieldh
			
			structure=worldblks3(x,y)
			If(structure<>0) Then
			
				code=AppendingStructureCode(structure,x,y)
				
				worldblksdebug(x,y)=code
				worldblks2(x,y)=getstructurecode(structure,code)
				
			EndIf 
		Next 
	
	Next 

End Function


Function AppendingStructureCode(structure,x,y)

	If((x  )>=0 And (x+1)<playfieldw) Then
		If(worldblks3(x+1,y)=structure) Then 
			code=code+c_right
		EndIf 
	EndIf 
	
	If((x-1)>=0 And (x  )<playfieldw) Then
		If(worldblks3(x-1,y)=structure) Then 
			code=code+c_left
		EndIf 
	EndIf 
	
	If((y  )>=0 And (y+1)<playfieldh) Then
		If(worldblks3(x,y+1)=structure) Then 
			code=code+c_down
		EndIf 
	EndIf 
	
	If((y-1)>=0 And (y  )<playfieldh) Then
		If(worldblks3(x,y-1)=structure) Then 
			code=code+c_up
		EndIf 
	EndIf 			

	Return code
	
End Function