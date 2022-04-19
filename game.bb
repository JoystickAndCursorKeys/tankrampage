Global g_3ds_win_width=300
Global g_3ds_win_height=400


Global g_winwidth=300
Global g_winwidth2=150
Global g_winheight=400
Global g_winheight2=200

Const playfieldw=64
Const playfieldh=64
Global playfieldmaxx=playfieldw-1
Global playfieldmaxy=playfieldh-1

Global maxx
Global maxy

Const blockw=128
Const blockh=128

Const blockw2=64
Const blockh2=64

Global blockwonscreen=g_winwidth/blockw
Global blockhonscreen=g_winheight/blockw
Global blockwonscreen2=g_winwidth/blockw2
Global blockhonscreen2=g_winheight/blockw2

Global lastblockx=-1
Global lastblocky=-1

Global thisblockx=-1
Global thisblocky=-1

Global testangle#=0

Global newdbgx,newdbgy

Const effect_tree=1
Const effect_smoke=10

Const c_dieing_timer=250

Dim keyset_up(3)
Dim keyset_down(3)
Dim keyset_left(3)
Dim keyset_right(3)
Dim keyset_fire(3)

Dim players.playerdef(100)
Global players_numberoff

Const door_open_thresshold=50
Const door_closed_mask=45


Type Playerdef
	Field lastx,lasty
	Field x#,y#
	
	Field camerax,cameray
	Field camerax0,cameray0
	Field camerax1,cameray1

	Field dx#,dy#
	Field angle#, lastangle#
	Field speed#
	Field lives
	Field damage
	Field maxdamage
	Field turning
	Field alliedcode
	Field hasflag,flagblock.blockdef
	Field homecode 
	Field tanksoundchannel1
	Field tanksoundchannel2

	;when draw several times, calculate only once
	Field drawmem_frame 
	Field drawmem_x
	Field drawmem_y

	;playfield window	
	Field pf_x0
	Field pf_y0
	Field pf_w
	Field pf_h
	Field pf_wd2
	Field pf_hd2
	
	Field dieing
	
	
End Type



Type collisiondef
	Field collided.blockdef
	Field driveover.blockdef
End Type



Global colisioninfo.collisiondef

Const bkt_cannon=1
Const bkt_tree=2
Const bkt_building=3
Const bkt_door=4

Const bkt_rubble=40

Function InitLevel(level)
	
	
	keyset_fire(1)=key_space
	keyset_up(1)=		key_cursup
	keyset_down(1)=		key_cursdown
	keyset_left(1)=		key_cursleft
	keyset_right(1)=	key_cursright

	;keyset for player 2
	keyset_fire(2)=		30; a
	keyset_up(2)=     	19; r
	keyset_down(2)=		33; f
	keyset_left(2)=		32; d
	keyset_right(2)=	34; g


	;alternate keyset for player 2
	;keyset_fire(2)=		82
	;keyset_up(2)=     	72
	;keyset_down(2)=		80;80 or 76
	;keyset_left(2)=		75
	;keyset_right(2)=	77


	
	newdbgx=0
	newdbgy=0
	;0-15 = road
	;	up=1
	;	down=2
	;	left=4
	;	right=8
	floorcodetable( 0)=0 ; does not exist
	floorcodetable( 1)=0 ; does not exist
	floorcodetable( 2)=0 ; does not exist
	floorcodetable( 3)=16 ;vertical
	floorcodetable( 4)=0
	floorcodetable( 5)=13
	floorcodetable( 6)=7
	floorcodetable( 7)=10
	floorcodetable( 8)=0 ; does not exist
	floorcodetable( 9)=11
	floorcodetable(10)=5
	floorcodetable(11)=8
	floorcodetable(12)=15 ; horizontal
	floorcodetable(13)=12
	floorcodetable(14)=6
	floorcodetable(15)=0  ; does not exist
	
	
	mapfile$="t"+level
	LoadOldLevel("data\level\"+mapfile$)
	


End Function



Function LoadOldLevel(filename$)
	filein = ReadFile(filename$)
	
	;clear blocks
	For x=0 To playfieldw-1
	
		For y=0 To playfieldh-1
			
			worldblks(x,y)\image=0
			worldblks(x,y)\mskimage=0
			worldblks(x,y)\floorimage=0

			worldblks(x,y)\blocktype=0; 0 means nothing
			worldblks(x,y)\status=0
			worldblks(x,y)\damage=0
			
			worldblks(x,y)\overlayoffsetx=0
			worldblks(x,y)\overlayoffsety=0
			
			worldblks(x,y)\effect=0
			
			worldblks(x,y)\effect2last=0
			worldblks(x,y)\effect2=0
						
			worldblks(x,y)\alliedcode=-1
			
			worldblks(x,y)\hasflag=0

			
			
		Next 
	
	Next 
		
	For x=0 To playfieldw-1
		For y=0 To playfieldh-1
			byte = ReadByte( filein )

			worldblks(x,y)\debug=byte
			worldblks(x,y)\code=byte

			justfloor=0
			If( (byte >=5 And byte <=16))  ;the road
			
				worldblks(x,y)\floorimage=byte
				justfloor=1
				
			ElseIf (byte=44 Or byte=67)    ;buildings with road

				worldblks(x,y)\floorimage=15

			EndIf 
			
			If(justfloor=0) Then
			
				worldblks(x,y)\image=byte
				worldblks(x,y)\mskimage=byte
				
			EndIf 
			
			;flag building
			If byte=89 Or byte=92 Then
					worldblks(x,y)\hasflag=1
					
					If(byte=89 )
				
						worldblks(x,y)\alliedcode=1
					
					Else 
				
						worldblks(x,y)\alliedcode=2
				
					EndIf 
										
			EndIf 
			
			;doors
			If byte=44 Or byte=67 Then
					
					worldblks(x,y)\blocktype=bkt_door
					If(byte=44 )
				
						worldblks(x,y)\alliedcode=1
					
					Else 
				
						worldblks(x,y)\alliedcode=2
				
					EndIf 
					
					;Stop 
					worldblks(x,y)\mskimage=door_closed_mask	
					;worldblks(x,y)\image=70				
			EndIf 			
							
			;cannons
			If(byte=27 Or  byte=39 Or byte=41 Or byte=43 Or byte=48 Or byte=50 Or byte=64 Or byte=66 Or byte=71 Or byte=73 Or byte=85 Or byte=87)
			
				worldblks(x,y)\blocktype=bkt_cannon
		
				If(byte=27 Or byte=41 Or byte=43 Or byte=48 Or byte=50 Or byte=85)
				
					worldblks(x,y)\alliedcode=1
					
				Else 
				
					worldblks(x,y)\alliedcode=2
				
				EndIf 
				
				xoff=0
				yoff=0
				
				If(byte=50 Or byte=48 Or byte=43 Or byte=85) Then
						xoff=-3
						yoff=2
				EndIf 					


				If(byte=41) Then
						xoff=-4
				EndIf 
				If(byte=43) Then
						xoff=-4
				EndIf 
				If(byte=64 Or byte=73 Or byte=87 Or byte=66 Or byte=71) Then
						xoff=-5
						yoff=0
				EndIf 
				
				
								
				worldblks(x,y)\overlayoffsetx=xoff
				worldblks(x,y)\overlayoffsety=yoff
				
			EndIf 
			
			If(byte=17 Or  byte=18 Or byte=19 Or byte=20 Or byte=22 Or byte=24 Or byte=25 Or byte=29 Or byte=30 Or byte=31 Or byte=32 Or byte=34 Or byte=36 Or byte=37 Or 	byte=89 Or byte=92)
			
				worldblks(x,y)\blocktype=bkt_building
				
			EndIf 
			
			If(byte=2 Or  byte=3 Or byte=4)
				
				worldblks(x,y)\blocktype=bkt_tree
				
				
				
			EndIf 
		Next 
	Next 
End Function 


Function findblockpos(code,bpos.blockpos)

	bpos\xindex=0
	bpos\yindex=0
						
	For x=0 To playfieldw-1
		For y=0 To playfieldh-1
			If(worldblks(x,y)\code=code) Then
					
				bpos\xindex=x
				bpos\yindex=y
				Return 
				
			EndIf 
		Next 
	Next 

	bpos\xindex=0
	bpos\yindex=0
	
End Function 

Function InitPlayer(player.playerdef)


	;Stop 			
	If(player\hasflag>0) Then
			player\flagblock\hasflag=1
	EndIf 
	player\hasflag=0

	bpos.blockpos=New blockpos

	player\dx#=0
	player\dy#=0
	player\angle#=90
	player\speed#=0
	player\lives=3
	
	home=20
	If(player\alliedcode<>1) Then 
		home=32
	EndIf 
	
	findblockpos(home,bpos)
	player\x= (bpos\xindex*blockw) + blockw/2
	player\y= (bpos\yindex*blockh) + blockh
	player\lastx=player\x
	player\lasty=player\y
	player\damage=0
	player\maxdamage=2
	player\hasflag=0
	player\homecode=home
	
	If(player\tanksoundchannel1<> 0 ) Then
		StopChannel (player\tanksoundchannel1) 
		StopChannel (player\tanksoundchannel2) 
		
		player\tanksoundchannel1=0
		player\tanksoundchannel2=0

	EndIf 
	
	ch1=PlaySound (gsfx_tengine1)
	player\tanksoundchannel1=ch1
	
	ch2=PlaySound (gsfx_tengine1)
	player\tanksoundchannel2=ch2
	
	player\dieing=0
	
	
	Delete bpos 
	
End Function 

Function maketrackparticles(player.playerdef)

		tspeed#=player\speed ; Sqr ((player\dx*player\dx) + (player\dy*player\dy))
		
		ticker=6
		
		If((tspeed>5 Or player\turning=1) And ticker>3) Then
		
			ticker=0
			For t=1 To 3
			
			a45=Rand(0,1)
			If(a45=0) Then
			 	a45=20
			Else
				a45=-20
			EndIf 
			
			pa#=ATan2 (player\dx,player\dy)

			ang#=ATan2 (player\dx,player\dy)
			ang=180+(ang)+a45
			
			off=20
			If(player\turning=1) Then off=Rand(-15,10)
			
			xoffset#=Sin(ang)*(off+t)
			yoffset#=Cos(ang)*(off+t)
			
			a2=45+Rand(-5,5)
			dist#=Rand(1,10)

			xs#=Sin(a2)*dist
			ys#=Cos(a2)*dist		

			newSimplePixParticle0.pixpartdef0(player\x+xoffset,player\y+yoffset,xs,ys, 1,0,Rand(2000)+1000)
					
			Next 
		EndIf 
End Function 

Function makedustparticles(player.playerdef)
		tspeed#=player\speed 
		
		ticker=6
		
		If((tspeed>3) And ticker>3) Then
		
		
			offx1=0
			offy1=0
			
			ticker=0
			For t=1 To 360 
			
			dist#=Rand(0,400) / 10
			xoffset=offx1+Sin(t)*dist
			yoffset=offy1+Cos(t)*dist 
			
			xs#=.5+Sin(t)/2
			ys#=.5+Cos(t)/2
			
			newSimplePixParticle.pixpartdef(player\x+g_winwidth2+xoffset,player\y+g_winheight2+yoffset,xs,ys, 1,100+Rand(100),Rand(2000)+1000)
					
			Next 
		EndIf 
End Function 


Function SetPlayerCamera(player.playerdef,xoff,yoff)
	
	player\camerax=player\x-xoff
	player\cameray=player\y-yoff
	
	player\camerax0=player\camerax-g_winwidth2
	player\cameray0=player\cameray-g_winheight2
	
End Function 
Function Game()
 
   Game_2P()
 
End Function 



 


Function Game_1P()

dashboardbg=LoadImage ("data\gfx\dashboardbg2.png")

	level=4
	;InitLevel(1)
	;InitLevel(2)
	;InitLevel(3)
	InitLevel(Rand(1,5))
	;InitLevel(5)
	
	player1.playerdef = New playerdef 
	player1\tanksoundchannel1=0
	player1\tanksoundchannel2=0
	
	player1\alliedcode=1
	Initplayer(player1)	
	
	player1\pf_x0=7
	player1\pf_y0=10
	player1\pf_w=626
	player1\pf_h=350
	player1\pf_wd2=314
	player1\pf_hd2=175
		
	
	colisioninfo=New collisiondef
	
	image=tankleft
	
	maxx=(playfieldw*blockw)-1
	maxy=(playfieldh*blockh)-1
	minx=1*blockw
	miny=1*blockh
	
	players(0)=player1
	players_numberoff=1
	
	
	;Stop 
	ticker=0
	While Not KeyHit(key_escape)

		If(KeyHit(28)) Then g_debugstop=1

		If(KeyHit(16)) Then
			
			effects_explode(player1\x,player1\y)
			
		EndIf 
	
		;draw--------------------------------------------
		DrawDashBoard(dashboardbg)
		DrawPlayfield(player1)
		;DrawPlayfield(player2)
		
		ResetViewPort()
		
		HandlePlayer(player1)
		;HandlePlayer(player2)
		
			
		movebullets()
		movepixparticles(player1\x,player1\y)	
		Move3DSprites(-player1\x,-player1\y,0,0,False )	
				
		LimitFPS()
		Flip 
		
	Wend 

	FreeImage dashboardbg 
	
	If(player1\tanksoundchannel1<> 0 ) Then
		StopChannel (player1\tanksoundchannel1) 
		StopChannel (player1\tanksoundchannel2) 
		
		player1\tanksoundchannel1=0
		player1\tanksoundchannel2=0

	EndIf 
	



End Function

Function Game_2P()


	dashboardbg=LoadImage ("data\gfx\dashboardbg2.png")

	level=4
	;InitLevel(1)
	;InitLevel(2)
	;InitLevel(3)
	InitLevel(Rand(1,5))
	;InitLevel(5)
	
	player1.playerdef = New playerdef 
	player1\tanksoundchannel1=0
	player1\tanksoundchannel2=0
	
	player1\alliedcode=1
	Initplayer(player1)	
	
	player1\pf_x0=7
	player1\pf_y0=10
	player1\pf_w=310
	player1\pf_h=350
	player1\pf_wd2=150
	player1\pf_hd2=175
		
	player2.playerdef = New playerdef 
	player2\tanksoundchannel1=0
	player2\tanksoundchannel2=0

	player2\alliedcode=2
	Initplayer(player2)	
	
	player2\pf_x0=323
	player2\pf_y0=10
	player2\pf_w=310
	player2\pf_h=350
	player2\pf_wd2=150
	player2\pf_hd2=175

	colisioninfo=New collisiondef
	
	image=tankleft
	
	maxx=(playfieldw*blockw)-1
	maxy=(playfieldh*blockh)-1
	minx=1*blockw
	miny=1*blockh
	
	players(0)=player1
	players(1)=player2
	players_numberoff=2
	
	
	;Stop 
	ticker=0
	While Not KeyHit(key_escape)

		If(KeyHit(28)) Then g_debugstop=1

		If(KeyHit(16)) Then
			
			effects_explode(player1\x,player1\y)
			
		EndIf 
	
		;draw--------------------------------------------
		DrawDashBoard(dashboardbg)
		DrawPlayfield(player1)
		DrawPlayfield(player2)
		
		ResetViewPort()
		
		HandlePlayer(player1)
		HandlePlayer(player2)
		
			
		movebullets()
		movepixparticles(player1\x,player1\y)	
		Move3DSprites(-player1\x,-player1\y,-player2\x,-player2\y,True )	
				
		LimitFPS()
		Flip 
		
	Wend 

	FreeImage dashboardbg 
	
	If(player1\tanksoundchannel1<> 0 ) Then
		StopChannel (player1\tanksoundchannel1) 
		StopChannel (player1\tanksoundchannel2) 
		
		player1\tanksoundchannel1=0
		player1\tanksoundchannel2=0

	EndIf 
	
	If(player2\tanksoundchannel1<> 0 ) Then
		StopChannel (player2\tanksoundchannel1) 
		StopChannel (player2\tanksoundchannel2) 
		
		player2\tanksoundchannel1=0
		player2\tanksoundchannel2=0

	EndIf 
		
End Function

Function handletileevents(player.playerdef,playerid,driveoverblock.blockdef)

	xx=player\camerax0
	yy=player\cameray0

	
	basesrcy=yy/blockh
	basesrcx=xx/blockw
	bw2=blockw/2
	bh2=blockh/2

	offy=((basesrcy)*blockh);-yy
	offx=((basesrcx)*blockw);-xx
	
	For y=0 To blockhonscreen+1
		
		blockposy=offy + y*blockh
		
		;srcy=basesrcy+y
		srcy=TiledRange(basesrcy+y,0,playfieldmaxy)

		
		If((srcy)<0 Or (srcy)>playfieldh) Then 
			;
		Else
			For x=0 To blockwonscreen+1
				
				;srcx=basesrcx+x
				srcx=TiledRange(basesrcx+x,0,playfieldmaxx)


				blockposx=offx + x*blockw
				
				
				If(Not((srcx)<0 And (srcx)>playfieldw)) Then 

					block.blockdef=worldblks(srcx,srcy)
					
					
					If(block\effect2last) Then
						
						block\effect2last=block\effect2last-1
						
						If(block\effect2last=0) Then
							block\effect2=0
						EndIf 
									
					EndIf 
				
				
					If(block\blocktype=bkt_door And driveoverblock<>block)
								
							block\status=block\status-1
							If(block\status<0) Then block\status=0
							
							If(block\status<door_open_thresshold) Then
							
								block\mskimage=door_closed_mask
						
							EndIf
					EndIf 		
							
					If(block\effect2=effect_smoke) Then
					
						;Stop 
						If(block\effect2last Mod 5 = 0) Then
						
							xefx=(srcx*blockw)+blockw2
							yefx=(srcy*blockh)+blockh2

							;xefx=xxpl+(blockposx+blockw2)
							;yefx=yypl+(blockposy+blockh2)
							size=Rand(20,50)
							
							h=New3DSprite(xefx,yefx,size,size,g_smoketexture,0 ,0,1.0,Rand(0,360),1)
							Set3DSpriteVelocity(h,.7,.7)
							Set3DSpriteFade(h,0.984)
							Set3DSpriteBlowup(h,1.001)

						EndIf 
					
					EndIf 
					;
					If(block\blocktype=bkt_cannon) Then 
						
						dx#=(blockposx+bw2)-(player\x)
						dy#=(blockposy+bh2)-(player\y)
						
						a#=ATan2(dy,dx)+(180)
						
						If(a#<0) Then a#=a#+360
						If(a#>360) Then a#=a#-360
						;a#=a#/45
						
						
						
						dist#=Sqr((dx*dx)+(dy*dy))
						block\effect=Int(a#)
						;Stop 
						If(dist<250 And (playerid <> block\alliedcode)) Then

							
							dontshoot=dontshoot-1
							
							If(dontshoot<0) Then 
							
								dontshoot=50
								
								a2#=((Int (block\effect) / 45) * 45)
								
								dx#=Cos(a2)*6
								dy#=Sin(a2)*6
								
								xoff=xxpl + Cos(a2)*50
								yoff=yypl + Sin(a2)*50
	
								
								b.bulletdef=newSimpleBullet(xoff+blockposx+bw2,yoff+blockposy+bh2,dx,dy,0)
								PlaySound gsfx_cannon
							
								
							EndIf 
							
						
						EndIf 
					
						
					EndIf 

						
				EndIf 
			Next 
		EndIf 
		
	Next 
End Function


Function HandlePlayer(player.playerdef)

	;handle input-------------------------------------
		
	If(player\dieing=0) Then 
	
		handleTankInput(player,player\alliedcode)
		
		maketrackparticles(player)
			
		player\x=TiledRange(player\x,0,maxx)
		player\y=TiledRange(player\y,0,maxy)
					
		;collisionss - tank with world
		collission=collidePlayerWorld(player)
		handlecollision(player,collission)

	Else
		player\dieing=player\dieing-1
		If(player\dieing=0) Then
			InitPlayer(player)
		EndIf 	
	EndIf 

					
	;collisions - bullets with world	
	collidebulletsworld1(player)
		
	;handle events-------------------------------------
	handletileevents(player,player\alliedcode,colisioninfo\driveover)

End Function 	

Function handleTankInput(player.playerdef,keyset)
 
	breaking=0
	turning=0
	driving=0
	slowing=1
	changetarget=0
	backing=0

	tdx=0
	tdy=0
	
	targetangle#=0
	
	player\lastx=player\x
	player\lasty=player\y
	player\lastangle=player\angle
			
;	keyset_fire(1)=key_space
;	keyset_up(1)=		key_cursup
;	keyset_down(1)=		key_cursdown
;	keyset_left(1)=		key_cursleft
;	keyset_right(1)=	key_cursright
		
	If(InputPressed(keyset_fire(keyset))) Then 
		
		speed#=15 
		dx#=Cos(player\angle) * speed
		dy#=Sin(player\angle) * speed 
		
		xoff#=Cos(player\angle) * (35 + player\speed)
		yoff#=Sin(player\angle) * (35 + player\speed)

		
		b.bulletdef=newSimpleBullet(xoff+player\x,yoff+player\y,dx,dy,1)
		
		PlaySound(gsfx_explosion5)
		
	EndIf 	

	If(InputDown(keyset_left(keyset))) Then 
			tdx=-1
			changetarget=1	
	EndIf 
	If((InputDown(keyset_right(keyset)))) Then 
			tdx=1
			changetarget=1
	EndIf
			
	updown=0
	If((InputDown(keyset_up(keyset)))) Then 
			tdy=-1
			changetarget=1
			
			
	Else If(InputDown(keyset_down(keyset))) Then 
			tdy=1
			leftright=-leftright
			changetarget=1
			
	EndIf 

	targetangle#=ATan2(tdy,tdx)
	difangle#=Abs(AngleCompare(targetangle,player\angle))

	If(changetarget>0) Then
	
		slowing=0
		
		driving=1
		
		If(difangle>175) Then
		
			backing=1
			player\speed=-1
		
		Else 
			If(difangle > 80) Then
				
				driving=0
				
				If(player\speed# > 0.5)
				
					breaking=1
				
				Else 
					turning =1 	
					
				EndIf 
				
			ElseIf(difangle > 0.5) Then
			
				player\speed=player\speed*.9
				turning=1
				
			EndIf 
		EndIf 

	EndIf 
	
	If(slowing=1) Then
		
		player\speed=player\speed*.95
		
	ElseIf(breaking =1) Then

		player\speed=player\speed*.57

	ElseIf(driving=1) Then

		player\speed#=(player\speed# +.3 )
		If(player\speed > 10) Then
			player\speed=10
		EndIf 

	EndIf 
	
	
	If(turning=1) Then
		
		player\angle=Turn(player\angle, targetangle, 5)
				
	EndIf 

	player\dx#=Cos(player\angle)* player\speed
	player\dy#=Sin(player\angle)* player\speed 		
	player\turning=turning
	
	rev2#=player\speed Mod 20
	gear=player\speed/20
	If(rev>10) Then rev2=rev2+(2*gear)
	hertz#=4000 + (rev2*500)
 
       ChannelPitch player\tanksoundchannel1, hertz 
       ChannelPitch player\tanksoundchannel2, hertz +2000
	
	player\x=player\x+player\dx
	player\y=player\y+player\dy		

	SetPlayerCamera(player,0,0)

	
End Function 




Function handleblockdamage(block.blockdef )
	
	If(block\blocktype=bkt_tree) Then
		block\floorimage=63	
	
	Else
	
		block\damage=block\damage+1
		
		block\effect2=effect_smoke
		block\effect2last=5000
		
		If(block\damage > 5) Then 
			If(block\blocktype=bkt_building) Then
			
				If(block\code=37 Or block\code=25)  Then

					block\floorimage=54
					block\image=0
					block\mskimage=0
					block\effect=0
					
				Else If(block\code=22 Or block\code=34)  Then

					block\floorimage=55
					block\image=0
					block\mskimage=0
					block\effect=0
					
				
				Else
				
					If(Rand(0,1)=1) Then 	
						block\floorimage=51
					Else
						block\floorimage=57

					EndIf 
					
					block\image=0
					block\mskimage=0
					block\effect=0				
				EndIf 
			
			Else If(block\code=44 Or block\code=67)  Then
				
					block\floorimage=59
					block\image=0
					block\mskimage=0
					block\effect=0
			Else
			
				block\floorimage=52
				block\image=0
				block\mskimage=0
					
			EndIf 
			
			block\blocktype=bkt_rubble
							
			PlaySound(gsfx_explosion4)
		Else
		
			If(block\code=44 Or block\code=67)  Then
			
				block\floorimage=58	
			Else
				block\floorimage=53
			EndIf 		
			
			
			If(Rand(0,1)=1) Then
				PlaySound(gsfx_explosion3)
			Else
				PlaySound(gsfx_explosion1)
			EndIf 
		EndIf 
	EndIf 
End Function 


Function handlecollision(player.playerdef,collission)



		bpos.blockpos=New blockpos
		If(collission=1  ) Then
			
			If(colisioninfo\driveover\blocktype=bkt_tree And player\speed>1)
			;colisioninfo.collided=Null
				colisioninfo\driveover\effect2last=5
				colisioninfo\driveover\effect2=effect_tree 

			ElseIf(colisioninfo\driveover\blocktype=bkt_door)

				If(colisioninfo\driveover\alliedcode=player\alliedcode) Then
					
					colisioninfo\driveover\status=colisioninfo\driveover\status+1
					If(colisioninfo\driveover\status>100) Then colisioninfo\driveover\status=100
					
					If(colisioninfo\driveover\status>door_open_thresshold) Then
					
						colisioninfo\driveover\mskimage=colisioninfo\driveover\image
						
					EndIf 
										
				EndIf
				
			EndIf 
			
		EndIf 


		If(collission=2) Then
		
			player\x=player\lastx - player\dx
			player\y=player\lasty - player\dy
			player\angle=player\lastangle
			
			If(player\speed>0.3) Then
			
			
				volume#=player\speed / 10

				If(volume# < 0) Then volume=-volume
				If(volume# > 1) Then volume=1

				channel=PlaySound(gsfx_mbang)
				
  		        ChannelVolume channel, volume	


			EndIf 
			
			player\speed=0
			
		EndIf 
		If(collission=3) Then
		
			player\damage=player\damage+1
			
			If(player\damage > player\maxdamage) Then
				player\lives=player\lives-1
	
				player\dieing=c_dieing_timer
				
	
				effects_explode(player\x,player\y)

				PlaySound(gsfx_explosion4)				
				
			Else 
				
				PlaySound(gsfx_explosion3)
				
			EndIf 
				
		EndIf 				
		

				
End Function 

Function ResetViewport()

	Viewport 0,0,c_width,c_height
	Origin 0,0 			

End Function 


Function DrawDashboard(dashboardbg)

		;make players a global array
		;make playmode global (1p, dual, network)
		
		;draw dashboard		
		SetBuffer  BackBuffer() 

		DrawBlock dashboardbg,0,0

End Function 


Function DrawPlayfield(player.playerdef)

	;set the global playfield 
	g_winwidth=player\pf_w
	g_winwidth2=player\pf_wd2
	g_winheight=player\pf_h
	g_winheight2=player\pf_hd2
	
	;setup the 3d sprites
	g_3ds_win_width=player\pf_w
	g_3ds_win_height=player\pf_h

	x0=player\pf_x0
	y0=player\pf_y0
	
	blockwonscreen=g_winwidth/blockw
	blockhonscreen=g_winheight/blockw
	blockwonscreen2=g_winwidth/blockw2
	blockhonscreen2=g_winheight/blockw2


	Viewport 0,0,c_width,c_height
	Origin 0,0 	
	Color 0,0,0 
	Rect x0-1,y0-1,	g_winwidth+2,g_winheight+2
	Rect x0-2,y0-2,	g_winwidth+4,g_winheight+4

	
	;Draw 2d playfield
    Viewport x0,y0,g_winwidth,g_winheight
	Origin x0,y0
	
	drawworldfloor(player)
	drawpixparticles0(player\camerax0,player\cameray0)
	
	DrawPlayers(player)	
		
	drawworldtiles (player)
	drawpixparticles(player\camerax0,player\cameray0)

	drawbullets(player)

   	CameraViewport g_camera,x0,y0,g_winwidth,g_winheight
	SetRenderPosition3DSprites(-player\camerax0,-player\cameray0)
	RenderWorld()
	
	If c_trace=1 Then 
	
			drawmsktiles2(player\x,player\y,frame)
			
	EndIf 	

	Color 255,255,255
		
End Function 

Function DrawPlayers(me.playerdef)

	;Stop 
	playfieldpixh=playfieldw*blockw
	playfieldpixw=playfieldw*blockw
	yborderdetectionthresshold=(playfieldpixh) / 2
	xborderdetectionthresshold=(playfieldpixw) / 2

	
	ix=0
	While ix<players_numberoff
	
		player.playerdef=players(ix)
	
		frame=Int(((AngleNormalize((player\angle)+90)*16)/360) )
		off=0:off=(Int(player\x) + Int(player\y)) Mod 2
		
		x=((g_winwidth2)-me\x)+player\x
		y=((g_winheight2)-me\y)+player\y
		
		
		they0=y
		
		If(player<>me) Then 
			If(  (player\y-me\y) > yborderdetectionthresshold) Then 
			
				y=y-playfieldpixh
			
			ElseIf((player\y-me\y) < -yborderdetectionthresshold) Then 
				
				y=y+playfieldpixh	
				
			EndIf 
			
			If(  (player\x-me\x) > xborderdetectionthresshold) Then 
			
				x=x-playfieldpixw
			
			ElseIf((player\x-me\x) < -xborderdetectionthresshold) Then 
				
				x=x+playfieldpixw	
				
			EndIf 
						
		EndIf 
		
		;remember for next loop (saves calculation time)
		player\drawmem_x=x
		player\drawmem_y=y
		player\drawmem_frame=frame
		
		DrawImage(tankimg_shadow,x+4,y+2+off,frame)
		
		ix=ix+1
	Wend 

	ix=0
	While ix<players_numberoff
	
		player.playerdef=players(ix)
		
		If(player\dieing>0) Then
		
			DrawImage(tankanim_damaged,player\drawmem_x,player\drawmem_y,player\drawmem_frame)
			
		Else
			If(player\alliedcode=1) Then
				DrawImage(tankimg,player\drawmem_x,player\drawmem_y,player\drawmem_frame)
			Else
				DrawImage(tankimg2,player\drawmem_x,player\drawmem_y,player\drawmem_frame)
			EndIf 
		EndIf 
		
		ix=ix+1
	Wend 



End Function

Function drawbullets(player.playerdef)

	For b.bulletdef= Each bulletdef
			
			If(b\used=1) Then
			
				x=b\x - player\camerax0
				y=b\y -	player\cameray0
				
				DrawImage g_shell,x,y
			EndIf 
			
	Next 

End Function 


Function drawpixparticles(xoffset,yoffset)
	
	c=0
	For b.pixpartdef= Each pixpartdef
			
			If(b\used=1) Then
			
				x=b\x - xoffset
				y=b\y - yoffset
				
				If(b\height>0) Then
					
					xyoff=(b\height)/20
					
					brightness=Rand(100,255)
					Color 0,0,0	
					Plot x+xyoff,y+xyoff
			
					Color brightness,85,81	
					Plot x,y			
							
				Else
					brightness=Rand(100,255)

					Color brightness,32,0							
					Plot x,y
					
				EndIf 
			EndIf 
		
			c=c+1	
	Next 

End Function 

Function drawpixparticles0(xoffset,yoffset)
	
	c=0
	For b.pixpartdef0= Each pixpartdef0
			
			If(b\used=1) Then
			
				x=b\x - xoffset
				y=b\y - yoffset
				
				If(b\height>0) Then
					
					xyoff=(b\height)/20
					
					
					Color 0,0,0	
					Plot x+xyoff,y+xyoff
			
					Color 137,85,81	
					Plot x,y			
							
				Else
					Color 32,32,0							
					Plot x,y
					
				EndIf 
			EndIf 
		
			c=c+1	
	Next 
	

End Function 

Function drawworldfloor(player.playerdef)

	xxpl=player\camerax0
	yypl=player\cameray0
		
	
	If(yypl=4460) Then
	EndIf 
	xoffset=-(xxpl Mod 256)
	yoffset=-(yypl Mod 256)
		
	;draw ground background images
	x=-256 + xoffset
	While(x<g_winwidth)
		y=-256 + yoffset
		While(y<=g_winheight)
	
			DrawBlock  ground,x,y
				
			y=y+256
				
		Wend

		x=x+256
	Wend  

	xx=xxpl
	yy=yypl
	
	basesrcy=yy/blockh
	basesrcx=xx/blockw
	offy=((basesrcy)*blockh)-yy
	offx=((basesrcx)*blockw)-xx
	
	
	
	For y=0 To blockhonscreen+1
		
		blockposy=offy + y*blockh
		
		srcy=TiledRange(basesrcy+y,0,playfieldmaxy)
		
			
		
		If((srcy)<0 Or (srcy)>playfieldh) Then 
			;
		Else
			For x=0 To blockwonscreen+1
				
				srcx=TiledRange(basesrcx+x,0,playfieldmaxx)

				blockposx=offx + x*blockw
				
								
				If(Not ((srcx)<0 And (srcx)>playfieldw)) Then 
					
				
					block.blockdef=worldblks(srcx,srcy)
					
					If(block\floorimage >0) Then 
							
						DrawImage  imgblocks(block\floorimage),blockposx,blockposy
						
						If c_trace=1 Then 
							Color 255,0,0
							Text blockposx,blockposy,"Flr "+block\debug
						EndIf 					

					EndIf 
				
				EndIf 
			Next 
		EndIf 
		
	Next 

End Function 

Function drawworldtiles(player.playerdef)

	xxpl=player\camerax0
	yypl=player\cameray0
	
	xx=xxpl
	yy=yypl
	
	basesrcy=yy/blockh
	basesrcx=xx/blockw
	offy=((basesrcy)*blockh)-yy
	offx=((basesrcx)*blockw)-xx
	
	For y=0 To blockhonscreen+1
		
		blockposy=offy + y*blockh
		
		srcy=TiledRange(basesrcy+y,0,playfieldmaxy)

		
		If((srcy)<0 Or (srcy)>playfieldh) Then 
			;
		Else
			For x=0 To blockwonscreen+1
				

				srcx=TiledRange(basesrcx+x,0,playfieldmaxx)

				
				blockposx=offx + x*blockw
				
								
				If(Not((srcx)<0 And (srcx)>playfieldw)) Then 

					block.blockdef=worldblks(srcx,srcy)
					drawn=0

					If(block\image>0) Then 
					
						If(block\effect2last) Then
														
							If(block\effect2=effect_tree) Then
							
								DrawImage  imgblocks(block\image),blockposx + Rand(0,1) ,blockposy + Rand (0,1)
								drawn=1
							Else
								DrawImage  imgblocks(block\image),blockposx,blockposy
								drawn=1
							EndIf 
						EndIf 
						
						If(block\blocktype=bkt_door) Then
								
								If(block\alliedcode=1) Then
									image=door_beige
								Else
									image=door_green
								EndIf 
								percent=block\status
								
								
								distance=(percent*64)/100
								
								pixels=64 + distance
								If(pixels Mod 2 =1 ) Then pixels=pixels+1
								
								DrawImageRect image,blockposx,blockposy,0,0+pixels,blockw,blockh-pixels
								DrawImageRect image,blockposx,blockposy+pixels,0,0,blockw,blockh-pixels
	
								DrawImage  imgblocks(block\image),blockposx,blockposy
								
								

								
						Else
							
								If(drawn=0) Then 
									DrawImage  imgblocks(block\image),blockposx,blockposy
								EndIf 
							
						EndIf 	
							
						
						If(block\hasflag=1) Then
							
							xoff=blockw/2
							yoff=blockh/2
							
							If(alliedcode=1) Then
								DrawImage  flagimg1,blockposx+xoff,blockposy+yoff
							Else
								DrawImage  flagimg2,blockposx+xoff,blockposy+yoff
							EndIf 


						EndIf 
						
						If(block\blocktype=bkt_cannon) Then

							frame=(Int (block\effect / 22.5)) Mod 15 
							
							xoff=block\overlayoffsetx
							yoff=block\overlayoffsety

							DrawImage  cannonimg_shadow,blockposx+xoff+20,blockposy+yoff+8, frame 
							DrawImage  canonroof_shadow,blockposx+xoff+20,blockposy+yoff+8 

							DrawImage  cannonimg,blockposx+xoff,blockposy+yoff, frame 
							
							If(block\alliedcode=1) Then 
								DrawImage  canonroof_beige,blockposx+xoff,blockposy+yoff 
							Else
								DrawImage  canonroof_green,blockposx+xoff,blockposy+yoff 
							EndIf 

							
							
						EndIf 
						
					EndIf 
					
				EndIf 
			Next 
		EndIf 
		
	Next 
End Function


Function drawmsktiles2(xxpl,yypl,frame)

	xx=xxpl-g_winwidth2
	yy=yypl-g_winheight2
	
	basesrcy=yy/blockh
	basesrcx=xx/blockw
	offy=(((basesrcy)*blockh)-yy) /2
	offx=(((basesrcx)*blockw)-xx) /2

	timg=tankimg_msk
	tx=(g_winwidth2/2) + 2
	ty=(g_winheight2/2) + 2

	
	collide=0
	For y=0 To blockhonscreen+1
		
		blockposy=offy + y*blockh2
		
		srcy=TiledRange(basesrcy+y,0,playfieldmaxy)

		
		If((srcy)<0 Or (srcy)>playfieldh) Then 
			;
		Else
			For x=0 To blockwonscreen+1
				
				srcx=TiledRange(basesrcx+x,0,playfieldmaxx)

				blockposx=offx + x*blockw2

				If(Not((srcx)<0 And (srcx)>playfieldw)) Then 

					blockimg=worldblks(srcx,srcy)\mskimage

					bx=blockposx+5
					by=blockposy+5

					
					DrawImage  imgblocks_msk(blockimg),bx,by
					Color 255,0,0
					Text bx,by,":"+blockimg

				EndIf 
			Next 
		EndIf 
		
	Next 
	
	If(collide=1) Then 
		;Color 255,255,0
		;Text 200,200,"COLLISION"
		;Text 202,200,"COLLISION"
		;Text 200,202,"COLLISION"
		
	EndIf 
	DrawImage(timg,tx,ty,frame)
	
End Function



Function movepixparticles(xoffset,yoffset)


	For b.pixpartdef= Each pixpartdef
			
			If(b\used=1) Then

				If(b\height>0) Then 
				
					b\x=b\x+b\dx
					b\y=b\y+b\dy
					
					x=b\x - xoffset
					y=b\y - yoffset
					
				EndIf 
				
				If(b\height<0) Then 
					b\dalley=b\dalley-1
					If(b\dalley<0) Then
						b\used=0
					EndIf 
				Else
					b\height=b\height - 1
				EndIf 
				
				If(x<-g_winwidth2 Or x> (g_winwidth2*3) Or y<-g_winheight2 Or y> (3*g_winheight2)) Then 
					b\used=0
				EndIf 
			EndIf 
			
	Next 

End Function 

Function movebullets()


	For b.bulletdef= Each bulletdef
			
			If(b\used=1) Then
			
				b\x=b\x+b\dx
				b\y=b\y+b\dy
				
				If(b\x<0) Then 
					b\x = maxx 
				Else If (b\x >maxx) Then 
					b\x=0
				EndIf 

				If(b\y<0) Then 
					b\y = maxy
				Else If (b\y >maxy) Then 
					b\y=0
				EndIf 
				
				b\lifetime=b\lifetime-1
				If(b\lifetime<0) Then
					b\used=0
				EndIf 
				
			EndIf 
			
	Next 

End Function 


Function Turn (oldangle#, targetangle#, aStep#)

	
	sign=AngleCompareSign(oldangle,targetangle)
	
	If(sign=0) Then 
		Return oldangle
	EndIf 
	
	Return AngleNormalize(oldangle + (aStep * sign))
	

End Function 

Function AngleNormalize(a)

	ang=a
	While ang< 0
		ang=ang+360
	Wend  
	ang=ang Mod 360 
	
	Return ang
End Function 
	
Function AngleCompare(a1,a2)
	
	ang1=AngleNormalize(a1)
	ang2=AngleNormalize(a2)
	
	;both angles now between 0 and 360
	
	div=ang2-ang1
	If(div > 180 ) Then 
		div=div-360
	ElseIf (div <- 180 )
		div=div+360
	EndIf 

Return div

End Function 

Function AngleCompareSign(a1#,a2#)
	
	ang1=AngleNormalize(a1)
	ang2=AngleNormalize(a2)
	;both angles now between 0 and 360
	
	div#=ang2-ang1
	If(div > 180 ) Then 
		div=div-360
	ElseIf (div <- 180 )
		div=div+360
	EndIf 

	If(div=0) Return 0
	
	Return div / Abs(div)

End Function 




Global dontshoot=50




Function collidePlayerWorld(player.playerdef)
    
		
	xx=player\camerax0
	yy=player\cameray0
	
	txo=player\camerax-player\camerax0
	tyo=player\cameray-player\cameray0

	tx=txo/2
	ty=tyo/2
		
	basesrcy=yy/blockh
	basesrcx=xx/blockw
	offy=((basesrcy)*blockh)-yy
	offx=((basesrcx)*blockh)-xx
	timg=tankimg_msk
		
	collide=0
	
	colisioninfo\collided=Null
	colisioninfo\driveover=Null
	
	frame=Int(((AngleNormalize((player\angle)+90)*16)/360) )
	
	For y=0 To blockhonscreen+1
		
		blockposy=offy + y*blockh
		
		srcy=TiledRange(basesrcy+y,0,playfieldmaxy)

		ymatch=((Int(player\y) / blockh) = srcy)
		If(ymatch) Then thisblocky=srcy
		
		

			For x=0 To blockwonscreen+1
				
				
				
		
				srcx=TiledRange(basesrcx+x,0,playfieldmaxx)

				blockposx=offx + x*blockw
				
				
			
				If(ymatch) Then 
		
					xmatch=((Int(player\x) / blockw) = srcx)
					If(xmatch) Then 
						thisblockx=srcx
						If(collide=0) Then collide=1
						colisioninfo\driveover = worldblks(srcx,srcy)
						
								
					EndIf 
					
				EndIf 
				
				
		
					
				
				blockimg=worldblks(srcx,srcy)\mskimage

				bx=blockposx/2
				by=blockposy/2
									
				If(debugcollission=1) Then DrawImage  imgblocks_msk(blockimg),bx,by
				
				If(ImagesOverlap(imgblocks_msk(blockimg),bx,by, timg,tx,ty)) Then
					
					
					If(worldblks(srcx,srcy)\hasflag=1 And player\alliedcode<> worldblks(srcx,srcy)\alliedcode) Then
						
							worldblks(srcx,srcy)\hasflag=0
							player\hasflag=1
							player\flagblock=worldblks(srcx,srcy)

					Else If(worldblks(srcx,srcy)\code=player\homecode And player\hasflag=1) Then
						
						; YOU WIN
						Cls
						Color 255,255,255
						Text 0,0 , "YOU WIN"
						Flip 
						WaitKey 													
					
					Else
						If(ImagesCollide(imgblocks_msk(blockimg),bx,by,0, timg,tx,ty,frame)) Then
							collide=2
							colisioninfo\collided = worldblks(srcx,srcy)
							If(debugcollission=0) Then Exit 
						EndIf 
					EndIf 
											
				EndIf 
				
			
			Next 
		
	Next 
	
	For b.bulletdef= Each bulletdef
			
			
			If(b\used=1) Then
		
		
					
					bux= (b\x -player\camerax0) 
					buy= (b\y -player\cameray0) 
					If(ImagesOverlap(g_shell,bux,buy, tankimg,txo,tyo)) Then
						
						
						If(ImagesCollide(g_shell,bux,buy,0, tankimg,txo,tyo,frame)) Then
							collide=3
							b\used=0
							
							PlaySound(gsfx_explosion5)
							
							Exit  
						EndIf 
						
					EndIf 
			EndIf 
	Next 
	
	If(collide>0) Then 
		Color 255,255,0
	;	Text 200,200,"COLLISION:"+collide
	;	
	;	If(collide=1) Then 
	;		If ( colisioninfo\driveover\blocktype=bkt_door) Then 
	;			Color 255,255,0
	;			Text 200,220,"COLLISION1 Door"
	;		EndIf 
	;		
	;	EndIf 
	EndIf 
		
	If(debugcollission=1) Then DrawImage(timg,tx,ty,frame)
	Return collide

End Function 



Function collideBulletsWorld2()

	For b.bulletdef= Each bulletdef
			
		If(b\used=1 And b\id=1) Then
		
							
			For srcy=0 To playfieldw-1
						
				yoff=((blockh*srcy )+g_winheight2 ) /2
				
				For srcx=0 To playfieldh-1
						
					xoff=((blockw*srcx ) +g_winwidth2) /2 
					blockimg=worldblks(srcx,srcy)\mskimage
					
					If(ImagesOverlap(imgblocks_msk(blockimg),xoff,yoff, g_shell_mask,b\x/2,b\y/2)) Then
							
							
						If(ImagesCollide(imgblocks_msk(blockimg),xoff,yoff, 0, g_shell_mask,b\x/2,b\y/2,0)) Then
									
							blo.blockdef=worldblks(srcx,srcy)
							
							b\used=False
							blo\damage=blo\damage+1
							handleblockdamage(blo)
							
							If(blo\blocktype=bkt_rubble) Then
							
;								effects_blockexplode2(srcx,srcy,player)
								
							EndIf 
							
							If(blo\blocktype=bkt_tree) Then

								blo\effect2last=5
								blo\effect2=effect_tree ;shake the tree

							EndIf 										
		
						EndIf 
					EndIf 						 
				Next 
			Next 
		EndIf 
	Next 		
	 
	
End Function 


Function collideBulletsWorld1(player.playerdef)


	For b.bulletdef= Each bulletdef
			
			If(b\used=1 And b\id=1) Then
			
				If(g_debugstop) Then Stop 

				bux=  (b\x - player\camerax0) / 2  
				buy=  (b\y - player\cameray0) / 2  

				bux=  TiledRange(bux  , 0 , maxx/2)
				buy=  TiledRange(buy  , 0 , maxy/2)

				xoffset=-(xxpl Mod 256)
				yoffset=-(yypl Mod 256)
					
				xx=player\camerax0
				yy=player\cameray0
				
				basesrcy=yy/blockh
				basesrcx=xx/blockw
				offy=((basesrcy)*blockh)-yy
				offx=((basesrcx)*blockw)-xx
				
				For y=0 To blockhonscreen+1
					
					blockposy=offy + y*blockh

					srcy=TiledRange(basesrcy+y,0,playfieldmaxy)

					

					If True 
						For x=0 To blockwonscreen+1
							
							
							If(g_debugstop) Then Stop 

							srcx=TiledRange(basesrcx+x,0,playfieldmaxx)

							blockposx=offx + x*blockw
											
							
							If True 	
							
								blockimg=worldblks(srcx,srcy)\mskimage
			
								bx=blockposx/2
								by=blockposy/2
													
								If(debugcollission=1) Then DrawImage  g_shell_mask,bux,buy
								
								If(ImagesOverlap(imgblocks_msk(blockimg),bx,by, g_shell_mask,bux,buy)) Then
									
									
									If(ImagesCollide(imgblocks_msk(blockimg),bx,by, 0,g_shell_mask,bux,buy,0)) Then
										
										blo.blockdef=worldblks(srcx,srcy)
										
										b\used=False
										blo\damage=blo\damage+1
										handleblockdamage(blo)
										
										If(blo\blocktype=bkt_rubble) Then
																		
											effects_blockexplode2(srcx,srcy)

										EndIf 
										
										If(blo\blocktype=bkt_tree) Then
			
											blo\effect2last=5
											blo\effect2=effect_tree ;shake the tree
			
										EndIf 										
										
									EndIf 
									
								EndIf 
								
						
							EndIf 
						Next 
					EndIf 
					
	Next 
			EndIf 
			
	Next 
	


End Function 


Function effects_explode(x,y)

	xx=x
	yy=y
	
	
	For t=1 To 50
	
	
	a#=Rand(0,360)
	
	rdx#=Cos(a) * (t * 50)
	rdy#=Sin(a) * (t * 50)
	
	xefx=xx+rdx
	yefx=yy+rdy
	
	
	If(t=1) Then
		h=New3DSprite(xx,yy,120/t,120/t,g_lighttexture,0 ,0,1.0,0.0,1)
	
		Set3DSpriteVelocity(h,0,0)
		Set3DSpriteFade(h,0.992)
		Set3DSpriteBlowup(h,1.01)
	ElseIf(t=2) Then
		h=New3DSprite(xx,yy,120/t,120/t,g_lighttexture,0 ,0,1.0,0.0,1)
	
		Set3DSpriteVelocity(h,0,0)
		Set3DSpriteFade(h,0.97)
		Set3DSpriteBlowup(h,1.28)
	Else
		h=New3DSprite(xx,yy,120/t,120/t,g_lighttexture,0 ,0,1.0,0.0,1)
		Set3DSpriteVelocity(h,rdx/50,rdy/50)
		Set3DSpriteFade(h,0.99)
		Set3DSpriteBlowup(h,.98)
	
	EndIf 
	
	Next 
	
	For t=1 To 1000 Step 2
	
		dist#=Rand(100,500)
		dist=dist / 10
		
		a#=Rand(-20,20)
		a=t+(a/10)
		
		xp#=Sin(a)*dist
		yp#=Cos(a)*dist
		
		speed#=Rand(1,120)
		speed=speed/20
		
		xs#=Sin(a)*speed
		ys#=Cos(a)*speed
		
		
	
		pox= g_winwidth2+x
		poy= g_winheight2+y
	

		p.pixpartdef=newSimplePixParticle(pox+xp,poy+yp,xs,ys, 2,50+Rand(100),Rand(2000)+1000)
	Next 

											
End Function


Function effects_blockexplode2(srcx,srcy)

	blockposx=(srcx * blockw)
	blockposy=(srcy * blockh)  
	
	
	For t=1 To 10
	
	
	a#=Rand(0,360)
	
	rdx#=Cos(a) * (t * 5)
	rdy#=Sin(a) * (t * 5)
	
	xefx=(blockposx+blockw2)+rdx
	yefx=(blockposy+blockh2)+rdy
	
	h=New3DSprite(xefx,yefx,120/t,120/t,g_lighttexture,0 ,0,1.0,0.0,1)
	
	If(t=1) Then
		Set3DSpriteVelocity(h,0,0)
		Set3DSpriteFade(h,0.98)
		Set3DSpriteBlowup(h,1.01)
	Else
		Set3DSpriteVelocity(h,-rdx/280,-rdy/280)
		Set3DSpriteFade(h,0.98)
		Set3DSpriteBlowup(h,.98)
	
	EndIf 
	
	Next 
	
	For t=1 To 1000 Step 3
	
		dist#=Rand(100,500)
		dist=dist / 10
		
		a#=Rand(-20,20)
		a=t+(a/10)
		
		xp#=Sin(a)*dist
		yp#=Cos(a)*dist
		
		speed#=Rand(1,120)
		speed=speed/20
		
		xs#=Sin(a)*speed
		ys#=Cos(a)*speed
		
		
		;Stop 
		pox= (blockw/2)+blockw*srcx
		poy= (blockh/2)+blockh*srcy
	
		;Stop 
		p.pixpartdef=newSimplePixParticle(pox+xp,poy+yp,xs,ys, 2,50+Rand(100),Rand(2000)+1000)
	Next 
	
	;makedustparticles()
											
End Function



Function effects_blockexplode(srcx,srcy,blockposx,blockposy,xxpl,yypl)

	For t=1 To 10
	
	
	a#=Rand(0,360)
	
	rdx#=Cos(a) * (t * 5)
	rdy#=Sin(a) * (t * 5)
	
	xefx=xxpl+(blockposx+blockw2)+rdx
	yefx=yypl+(blockposy+blockh2)+rdy
	
	h=New3DSprite(xefx,yefx,120/t,120/t,g_lighttexture,0 ,0,1.0,0.0,1)
	
	If(t=1) Then
		Set3DSpriteVelocity(h,0,0)
		Set3DSpriteFade(h,0.98)
		Set3DSpriteBlowup(h,1.05)
	Else
		Set3DSpriteVelocity(h,-rdx/280,-rdy/280)
		Set3DSpriteFade(h,0.98)
		Set3DSpriteBlowup(h,.98)
	
	EndIf 
	
	Next 
	
	For t=1 To 1000 Step 3
	
		dist#=Rand(100,500)
		dist=dist / 10
		
		a#=Rand(-20,20)
		a=t+(a/10)
		
		xp#=Sin(a)*dist
		yp#=Cos(a)*dist
		
		speed#=Rand(1,120)
		speed=speed/20
		
		xs#=Sin(a)*speed
		ys#=Cos(a)*speed
		
		
		;Stop 
		pox= (blockw/2)+g_winwidth2+blockw*srcx
		poy= (blockh/2)+g_winheight2+blockh*srcy
	
		;Stop 
		p.pixpartdef=newSimplePixParticle(pox+xp,poy+yp,xs,ys, 2,50+Rand(100),Rand(2000)+1000)
	Next 
	
	;makedustparticles()
											
End Function 



Function ByteValtoString$(val)
	
	s$=val
	If(Len(s$)<3) Then s$=" "+s$
	If(Len(s$)<3) Then s$=" "+s$
	Return s$
End Function 


Function isroadtile(tile)
	
	If(tile>=5 And tile<=16) Then Return True 
	
	If(tile=24 Or tile=36 Or tile=44 Or tile=67) Then Return True

End Function 


Function  TiledRange(x,min,max)
	
	;Stop
	
	size=(max-min)+1
	
	If(x>=min And  x<=max) Then Return x
	
	If(x<min) Then 
		
		x=x+size
		While(x<min)
			x=x+size
		Wend 
		Return x
		
	Else ; >=max
		
		While(x>max)
			x=x-size
		Wend 
		Return x

	EndIf 
	
	Return 0 ; error
	
End Function 




Function InputPressed(code)
	Return KeyHit(code)
End Function 

Function InputDown(code)
	Return KeyDown (code)
End Function 