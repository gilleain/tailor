class State(object):

    def __init__(self, name, label):
        self.name = name
        self.label = label

    def __repr__(self): 
        return "%s %s" % (self.name, self.label)

class Transition(object):

    def __init__(self, startState, endState, action):
        self.startState = startState
        self.endState = endState
        self.action = action

    def namesMatch(self, startStateName, endStateName):
        return self.startState.name == startStateName and self.endState.name == endStateName

    def __repr__(self):
        return "%s -> %s : %s" % (self.startState, self.endState, self.action)

class TransitionTable(object):

    def __init__(self, states, defaultHandler):
        self.states = states
        self.transitions = []
        self.defaultHandler = defaultHandler

    def addTransitionByName(self, startStateName, endStateName, action):
        startState = self.lookupState(startStateName)
        endState = self.lookupState(endStateName)
        self.transitions.append(Transition(startState, endState, action))

    def addTransition(self, startState, endState, action):
        self.transitions.append(Transition(startState, endState, action))

    def lookupState(self, stateName):
        for state in self.states:
            if state.name == stateName:
                return state
        return None

    def lookupTransition(self, startStateName, endStateName):
        for transition in self.transitions:
            if transition.namesMatch(startStateName, endStateName):
                return transition
        return None

    def handle(self, startStateName, endStateName, args):
        transition = self.lookupTransition(startStateName, endStateName)
        if transition is None:
            self.defaultHandler(startStateName, endStateName, args)
        else:
            transition.action(startStateName, endStateName, *args)     

    def __repr__(self):
        return "\n".join([str(t) for t in self.transitions])

def printArgs(*args):
    print(args)

class Counter(object):

    def __init__(self):
        self.count = 0

    def __call__(self, startStateName, endStateName, *args):
        self.count += 1 

    def __repr__(self):
        return str(self.count)

class DoNothingHandler(object):

    def __init__(self):
        pass

    def __call__(self, startStateName, endStateName, *args):
        pass

class Emitter(object):

    def __init__(self, emitSymbol):
        self.emitSymbol = emitSymbol

    def __call__(self, startStateName, endStateName, *args):
        print(endStateName, self.emitSymbol)

    def __repr__(self):
        return "[%s]" % self.emitSymbol

class HelixBuilder(object):

    def __init__(self):
        self.start = None
        self.length = 0
        self.hCount = 0
        self.gCount = 0

    def __call__(self, startStateName, endStateName, *args):
        if endStateName == " ":
            print("Helix", self.start, self.start + self.length    )
        elif endStateName == 'H':
            self.length += 1
            self.hCount += 1
        elif endStateName == 'G':
            self.length += 1
            self.gCount += 1
        #elif 

class SSEMachine(object):

    def __init__(self):
        states = []
        states.append(State(" ", "Blank"))
        states.append(State("H", "AlphaHelix"))
        states.append(State("G", "ThreeTenHelix"))
        states.append(State("E", "Extended"))
        states.append(State("B", "IsoBridge"))
        states.append(State("S", "S-Bend"))
        states.append(State("T", "Turn"))

        self.table = TransitionTable(states, DoNothingHandler())
        self.table.addTransitionByName(" ", "H", Emitter("H"))
        self.table.addTransitionByName(" ", "G", Emitter("H"))
        self.table.addTransitionByName("G", "H", Emitter("H"))
        self.table.addTransitionByName("H", "G", Emitter("H"))
        self.table.addTransitionByName("H", " ", Emitter(" "))
        self.table.addTransitionByName("G", " ", Emitter(" "))
        self.table.addTransitionByName("G", "G", Emitter("H"))
        self.table.addTransitionByName("H", "H", Emitter("H"))

    def handle(self, lastStateName, currentStateName, *args):
        self.table.handle(lastStateName, currentStateName, *args)

if __name__ == "__main__":

    seq = " EEEE HHHHGGG HHHHHH GGGGGG EEEEEEETTEEEEE HHHHTHHHH "
    machine = SSEMachine()
    last = " "
    for i, stateName in enumerate(seq):
        machine.handle(last, stateName, *[i])
        last = stateName 
    print(machine.table)
