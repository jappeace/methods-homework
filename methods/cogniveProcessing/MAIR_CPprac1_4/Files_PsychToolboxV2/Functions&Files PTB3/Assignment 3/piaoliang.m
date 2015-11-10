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

load('picturemat.mat');
stimvhdim = size(GrayM,2);
MCo1(:,:,1) = RedM;
MCo1(:,:,2) = GreenM;
MCo1(:,:,3) = BlueM;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% screen presentation
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

[w,screenrect] = openwindow();

Screen('fillrect',w,backgroundcolor);
Screen('Flip', w);

sr = [0 0 stimvhdim stimvhdim];
dst = CenterRect(sr,screenrect);

tex=Screen('MakeTexture', w, MCo1);

Screen('DrawTexture', w, tex,sr,dst);
Screen('Flip', w);

waituntilspacepress;

Screen closeall;