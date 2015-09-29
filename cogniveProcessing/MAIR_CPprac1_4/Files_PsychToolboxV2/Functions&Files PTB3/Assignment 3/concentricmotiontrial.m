function duration = concentricmotiontrial(presentationduration, w, screenrect)

% parameters (magic numbers)
monitorfreq = 60;
stimvhdim = 300;
ncycles = 5;
backgroundcolor = 255/2;
framestocycle = 30;

% stimulus computation
M = motiongrating(stimvhdim,ncycles,backgroundcolor,framestocycle);
presentationframes = round(presentationduration*monitorfreq);

% screen presentation
Screen('fillrect',w,backgroundcolor);
flips(w,screenrect);

sr = [0 0 stimvhdim stimvhdim];
dst = CenterRect(sr,screenrect);

for i = 1:framestocycle
    tex(i)=Screen('MakeTexture', w, M(:,:,i));
end

donotstop = 1;
teller = 0;
for frame = 1:presentationframes;
    Screen('DrawTexture', w, tex(1+mod(frame,framestocycle)),sr,dst);
    flips(w,screenrect);
end

stationaryTex = Screen('MakeTexture', w, concentricgrating(stimvhdim,ncycles, 50));
duration = [];
%fix up user part
for i = 1:3
    

        
    % show how crazy he is
    Screen('DrawTexture', w, stationaryTex,sr,dst);
    onset = flips(w,screenrect);

    waituntilspacepress;
    Screen('fillrect',w,backgroundcolor);
    offset = flips(w,screenrect);
    
    duration(i) = offset - onset;
    % blank first I guess
    for frame = 1:(monitorfreq - 1);
        Screen('fillrect',w,backgroundcolor);
        flips(w,screenrect);
    end

end
end