function stair = stairUpdate(stair,response);stair.trialnr = stair.trialnr+1;stair.response(stair.trialnr) = response;if stair.nswitch >= 2	changefact = stair.changefactors(2);else	changefact = stair.changefactors(1);endif response	stair.ndown = stair.ndown+1;	stair.nup = 0;	if stair.ndown == stair.downandup(1)		changetrial = 1;		stair.changetrial = stair.changetrial+1;		stair.downorupchange(stair.changetrial) = 0;		tint = stair.intensity(end)/changefact;		stair.intensity = [stair.intensity tint];		stair.ndown = 0;	else		changetrial = 0;		tint = stair.intensity(end);		stair.intensity = [stair.intensity tint];	endelse	stair.nup = stair.nup+1;	stair.ndown = 0;	if stair.nup == stair.downandup(2)		changetrial = 1;		stair.changetrial = stair.changetrial+1;		stair.downorupchange(stair.changetrial) = 1;		tint = stair.intensity(end)*changefact;		stair.intensity = [stair.intensity tint];		stair.nup = 0;	else		changetrial = 0;		tint = stair.intensity(end);		stair.intensity = [stair.intensity tint];	endendif changetrial	if stair.changetrial>1		if stair.downorupchange(stair.changetrial) ~= stair.downorupchange(stair.changetrial-1)			stair.nswitch = stair.nswitch + 1;			stair.intswitch = [stair.intswitch stair.intensity(end-1)];			stair.switchtrial = [stair.switchtrial stair.trialnr-1];		end	endendif stair.nswitch == stair.terminateswitch	stair.keepgoing = 0;else	stair.keepgoing = 1;end