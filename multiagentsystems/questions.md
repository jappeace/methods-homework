#Inteligent agents
How do you measure performance of multiple tasks?

Are there any examples of agent syntecies? I'm very curious how one could
implement something like that.

#Deductive reasoning agents
Has Concurrent MetateM had any real world applications?

Can Concurrent MetateM run into classical threading problems (such as deadlocks
or race conditions)?

#Practical reasoning agent
Can a plan library contain plans to create new plans?

Could you use an adaptive reconsider function that will reconsider more or
less often depending on how good its working?
ie reconsider the reconsideration process.

#Reactive and hybrid agents
In the TouringMachines, why did they add a control subsystem? It seems to defy
the entire purpose of the architecture. Because the control subsystem needs
to know what is best in a given situation, so it needs to know about the 
working of the other layers, so why not just create one big control subsystem
and throw the layers away?

So in the 3T architecture the reactive skills sortoff work like reflexes?
They do something and then inform the rest of the architecture they're doing that,
or do they work more like "autopilot", where it gets assigned a task and the rest
of the agent can go on and think about other things?
