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

#Bargaining
At 15.3, I don't quite see how this will scale up to more than 2 agents?
Would it just be a tree like form of sharing? or will there be one task manager?
How does the flowchart go, for example: some tasks arrive at an agent. Then
that agent will start negotiating with all other agents?

At 15.3.3 Could you give a better example of how hidden tasks work? I mean
I understand decoy tasks, the agent just says he has something else to do
because he's lazy. But I don't understand where the utility comes from
in the hidden tasks example

#Arguing
Stamping ones feet seems like a rather ineffective method of argumentation.
Can you give me some examples of how this would be effective (the visceral mode)?
It sounds like a stupid question, but maybe the example is wrong could you
give a better example of the visceral mode?

Humans will often resort to wrong argumentation methods: Such as argumentum ad hominem
or argumentum ad verecundiam. Is it possible to model these kind of arguments with
help of Dung style for example? It would be funny if you could create
some agents that try to resolve conflicts by calling each other names.

# Logical foundation
I'm interested in the refinement methodology. Coming from software
engineering we used different techniques to design large systems.
(Basically we would prioritize requirements and would work from high
to low and call it SCRUM to sound fancy). Could you give an example
how this works for a simple system? (for example the functional one)

Considering the book is several years old, has there been any progress
on model checking? It seems like a really good methodology to do unit/integration
testing with (or even build into a type checker for a compiler).

#  A logic for normative multi-agent programs
In figure 1, if agents have no access to the counts-as rules, how are they supposed to know which norms should be followed.
Or are the agents supposed to figure out what is allowed and what isn't just by doing things wrong and getting sanctions?

Honestly I skimmed most of chapter 4, because its a little to logical for me.
But then I saw 4.5 Does this chapter mean that you could build a type-checker
for these "semantics", in that you get some compile error if you do something,
stupid (such as talking about rules that don't exist). As a programmer I really
like type checkers. Another example if you use an instance of this syntax
and try to let your agent borrow a book without being registered,
could you get a type error?

# Social norms

> Evolution and fairness norms

I get that the 3,3 contract is the most fair. However I'm asked to perform
evolution in my head somehow, could you explain step by step why the evolution
would force 3,3?

> Consider a competition between alternative forms of money..."

Can you explain this further?
I mean I think the norms got to me while trying to read this sentence
correctly. Maybe provide a different example.

# How to social goal

# On the logic of normative systems

# Power in game forms

# Designing intelligent gameplay

