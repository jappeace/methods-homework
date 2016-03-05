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

#Understanding each other
Are programming languages ontologies? Could you use these for agent to
agent communication? I mean what they describe with OWL looks a lot
like a type system.

What is the difference between an instance and a class in an ontology?
#Communication
What is the difference between language and ontology?

So if you can reduce agent communication to a conventional program
verification problem, (end of 7.2.2) why is it not clear how to
do this verification with an agent implemented in Java?

#Working together
Can you give examples of problems that can't be solved by homogeneous
parallelization but still can be solved by agent based parallelization?
I find it hard to imagine these kind of problems.

Could you also use coordination trough joint intentions with help of Rao and
Georgeff, or alternatively Karo logic? How would these implementations from CDS or ARCHON.
Are there any real world examples that do this? (I thought that both Rao 
and Georgeff and Karo surpassed Cohen and Levesque, remembering the intelligent agents course)
