clear all; commandwindow;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% parameters
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

screennr = 0;
monitorfreq = 60;

presentationduration = 20;

stimvhdim = 300;
ncycles = 5;
backgroundcolor = 255/2;
framestocycle = 30;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% stimulus computation
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

M = motiongrating(stimvhdim,ncycles,backgroundcolor,framestocycle);
presentationframes = round(presentationduration*monitorfreq);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% screen presentation
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

[w,screenrect] = openwindow();

Screen('fillrect',w,backgroundcolor);
Screen('Flip', w);

sr = [0 0 stimvhdim stimvhdim];
dst = CenterRect(sr,screenrect);

for i = 1:framestocycle
    tex(i)=Screen('MakeTexture', w, M(:,:,i));
end

donotstop = 1;
teller = 0;
for frame = 1:presentationframes;
    Screen('DrawTexture', w, tex(1+mod(frame,framestocycle)),sr,dst);
    Screen('Flip', w);
end
stationaryTex = Screen('MakeTexture', w, concentricgrating(stimvhdim,ncycles, 50))
for frame = 1:presentationframes;
    Screen('DrawTexture', w, stationaryTex,sr,dst);
    Screen('Flip', w);
end

Screen closeall;