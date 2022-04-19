engine=LoadSound("Car-Engine.wav")

Stop 
enginechannel=PlaySound (engine)

rev#=1
While 1=1


If(KeyDown (57)) Then
	rev=rev+.01
Else
	rev=rev-1
	If(rev<1) Then rev=1
EndIf 

rev2#=rev Mod 20
gear=rev/20
If(rev>10) Then rev2=rev2+(2*gear)
hertz#=6000 + (rev2*1000)
 
ChannelPitch enginechannel, hertz 
SoundVolume enginechannel,0.2



Flip True 

Wend 
