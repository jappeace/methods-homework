clear all; commandwindow;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% parameters
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

stimvhdim =  200;
ncycles = 10;

backgroundcolor = [200, 200, 200];
screennr = 0;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% stimulus computation
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

M = concentricgrating(stimvhdim,ncycles, 50);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% screen presentation
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

[w,screenrect] = openwindow();

Screen('fillrect',w,backgroundcolor);
Screen('Flip', w);

sr = [0 0 stimvhdim stimvhdim];
dst = CenterRect(sr,screenrect);

tex=Screen('MakeTexture', w, M);

Screen('DrawTexture', w, tex,sr,dst);
Screen('Flip', w);

waituntilspacepress;

Screen closeall;