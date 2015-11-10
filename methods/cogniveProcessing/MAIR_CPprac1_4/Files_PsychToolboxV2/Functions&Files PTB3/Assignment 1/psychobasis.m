clear all; commandwindow;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% parameters
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

stimvhdim = 500;
ncycles = 10;

backgroundcolor = 30;
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