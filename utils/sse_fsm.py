from fsm import FSM, Error

def startHelix(fsm):
    print "startHelix"
    helices.append([fsm.input_symbol])

def continueHelix(fsm):
    print "continueHelix"
    helices[-1].append(fsm.input_symbol)

def finishThreeTen(fsm):
    print "finishThreeTen"
    print "310 :", helices[-1]

def finishAlpha(fsm):
    print "finishAlpha"
    print "Alpha :", helices[-1]

def anyTransition(fsm):
    print "anyTransition"

helices = []
non_helix_symbols = " BTSE"
f = FSM('NON_HELIX', helices)
f.set_default_transition(Error, 'NON_HELIX')
f.add_transition_any('NON_HELIX', anyTransition, 'NON_HELIX')
f.add_transition('H', 'NON_HELIX', startHelix, 'ALPHA_HELIX')
f.add_transition('H', 'ALPHA_HELIX', continueHelix, 'ALPHA_HELIX')
f.add_transition('G', 'ALPHA_HELIX', continueHelix, 'ALPHA_HELIX')
f.add_transition('G', 'NON_HELIX', startHelix, '310_HELIX')
f.add_transition('G', '310_HELIX', continueHelix, '310_HELIX')
f.add_transition('H', '310_HELIX', continueHelix, 'ALPHA_HELIX')
f.add_transition_list(non_helix_symbols, '310_HELIX', finishThreeTen, 'NON_HELIX')
f.add_transition_list(non_helix_symbols, 'ALPHA_HELIX', finishAlpha, 'NON_HELIX')

seq = " HHHGG TTHHHHH GGHH GGGG "
for s in seq:
    f.process(s)
    print s, f.current_state
