clear all; commandwindow;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% parameters
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

stimvhdim = 100;
ncycles = 10;

backgroundcolor = 0;
screennr = 0;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% stimulus computation
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

M = sinewave(stimvhdim,ncycles);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% screen presentation
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

[w,screenrect] = Screen('Openwindow',screennr);

Screen('fillrect',w,backgroundcolor);
Screen('Flip', w);

sr = [0 0 stimvhdim stimvhdim];
dst = CenterRect(sr,screenrect);

tex=Screen('MakeTexture', w, M);

Screen('DrawTexture', w, tex,sr,dst);
Screen('Flip', w);

waituntilspacepress;

Screen closeall;