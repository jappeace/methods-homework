Exam probably has Games and game forms excersize...

game form = what are possible actions and possible outcomes
N = set of agents agents
A = A_1 x ... x A_n set of possible actions

O = a set of outcomes is the result of performing an action
g = a function..o

Strategic game is a game form + utility function. utality is minmax evaluation.
u_i = A \to R
You can use (N, A, u)

complete = alle waardes zijn bekend

(N, A, u(a_1)) == (N,A,u(g(a_1)))

Secuirty level is the amount of evaluation you can achieve in the worst
case scenario.

A = row, B = collumn

s = a function that returns the probablity of a given action.
Delta is a function.
Delta returns the set of probablities of a set of actions
(from either one or a bunch of players).

expectedUtility(a:action){
	u match {
		case a:pure => normal utility (lookup from the table)
		case a:mixed => expectedUtility(a)
	}
}

pareto efficient is also called social equilibrium. Its good for some, and
maybe bad for others, but overall pretty good

Dominant strategy is a strategy that doesn't consider interaction.

Nash equilibrium, if you know what the other player is gonna do, then its
stable, otherwise it isn't.


Design a game that doesn't have a Nash equilibrium.

