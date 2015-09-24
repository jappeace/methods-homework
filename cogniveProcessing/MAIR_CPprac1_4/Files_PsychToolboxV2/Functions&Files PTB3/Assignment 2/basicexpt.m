clear all;%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% parameters %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%dummymode = 1;% parameters of the sinewavegratingstimvhdim = 100; % width and height of the stimulus in pixelsncycles = 3; % number of cycles of the gratingtargetorientation = 0; % orientation of the grating (degrees from vertical)ISI = 1;% procedural & instructionspresentationtime = 1; % secondsnumberoftrials=4;% general parametersmonitorfrequency = 85; % Hzbackgroundcolor = 255/2; % grayscreennr = 0; % standard%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% computation %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% how many frames will an image be presentedpresentationframes = round(presentationtime*monitorfrequency);% compute the standard stimulusstandardstim = orisinuswave(stimvhdim,ncycles,targetorientation); stair = stairSetup([2, 1.5], [3,1], 20, 4);    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  %%%%%%%%% initialize the screen %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% %%  [w,screenrect]=Screen('OpenWindow',screennr, [], [0 0 500 300]);Screen(w,'fillrect',backgroundcolor);Screen('Flip', w);sr = [0 0 stimvhdim stimvhdim]; dst = CenterRect(sr,screenrect);tex1=Screen('MakeTexture', w, 255*standardstim);crossDimens = 10;crossWidth = 2;%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% start the experiment %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%HideCursor;while stair.keepgoing        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    %%%%%%%%% prepare a trial %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    % determine whether the orientation of the 2nd interval is more clockwise or counterclockwise    if dummymode        orichange = 10;    else        orichange = stair.intensity(end);    end    counterclock = Sample([0 1]);    if counterclock == 1        testori = targetorientation-orichange; % meer tegen klok    else        testori = targetorientation+orichange; % meer met klok    end        % compute the stimulus for th  e 2nd interval    teststim = orisinuswave(stimvhdim,ncycles,testori);    tex2=Screen('MakeTexture', w, 255*teststim);              % make the screen gray    Screen(w,'fillrect',backgroundcolor);    Screen('Flip', w);                                          % and now we wait    writetexttoscreen(w, strcat('start of trial (', num2str(stair.trialnr), '),  press space to continue'), screenrect);    waituntilspacepress;    %%%% show fancy pants cross    fixationcross(w, crossWidth, crossDimens, screenrect);    Screen('Flip', w);    WaitSecs(ISI);    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    %%%%%%%%% show the 1st interval %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%        for frame = 1:presentationframes        Screen('DrawTexture', w, tex1,sr,dst);        fixationcross(w, crossWidth, crossDimens, screenrect);        Screen('Flip', w);    end        % make the screen gray    Screen(w,'fillrect',backgroundcolor);    Screen('Flip', w);        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    %%%%%%%%% show the 2nd interval %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    %%%% show fancy pants cross    fixationcross(w, crossWidth, crossDimens, screenrect);    Screen('Flip', w);     WaitSecs(ISI);    for frame = 1:presentationframes        Screen('DrawTexture', w, tex2,sr,dst);        fixationcross(w, crossWidth, crossDimens, screenrect);        Screen('Flip', w);    end         % erase the screen    Screen(w,'fillrect',backgroundcolor);    Screen('Flip', w);    writetexttoscreen (w, 'Press right or left arrow', screenrect);    stair = stairUpdate(stair, counterclock == getaresponse());         endScreen closeall;save(input('give a filename: ','s'),'stair','targetorientation');ShowCursor;