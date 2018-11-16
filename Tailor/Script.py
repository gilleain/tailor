import os, sys
from pyparsing import *
from Condition import PropertyCondition, HBondCondition, DistanceBoundCondition, AngleBoundCondition, TorsionBoundCondition
from Description import ChainDescription, ResidueDescription, AtomDescription, predefined_descriptions
from Measure import AngleMeasure, DistanceMeasure, TorsionMeasure, HBondMeasure, PropertyMeasure
from Run import Run, Pipe

def makeKeywords(keywordlist):
    keywords = Forward()
    for keyword in keywordlist:
        keywords ^= Keyword(keyword)
    return keywords

def makeGrammar():
    lbracket  = Literal("[").suppress()
    rbracket  = Literal("]").suppress()
    lbrace    = Literal("(").suppress()
    rbrace    = Literal(")").suppress()
    equals    = Literal("=").suppress()
    comma     = Literal(",").suppress()
    dot       = Literal(".")
    tilde     = Literal("~")
    backslash = Literal("/")
    colon     = Literal(":")
    slash     = Literal("\\")   # confusing!
    hash      = Literal("#")
    plusminus = Literal("+/-").suppress()
    minus     = Literal("-")
    quote     = Literal("'").suppress()

    # some basic tokens
    word = Word(alphas + nums)
    quotedWord = quote + word + quote

    num = Word(nums)

    integer = Combine(Optional(minus) + num)
    integer.setParseAction(lambda s, l, t: int(t[0]))

    real = Combine(integer + Literal(".") + Optional(num))
    real.setParseAction(lambda s, l, t: float(t[0]))

    #reallist = lbracket + real + ZeroOrMore( Suppress(",") + real) + rbracket
    anyNum = real ^ integer
    numberWithRange = anyNum + plusminus + anyNum

    comment = hash + Optional(restOfLine)

    # motif description
    selection = Group(integer + dot.suppress() + word)
    selectionList = Group(lbrace + OneOrMore(selection) + rbrace).setResultsName("selectionList")
    attributeName = makeKeywords(
        ["chainID", "chainType", "position", "resname", "hydrogen-bond", "torsion-bound", 
            "distance-bound", "angle-bound"])
    value = quotedWord ^ integer ^ real ^ numberWithRange
    attributeValue = Optional(selectionList) + OneOrMore(value)
    property = Group(attributeName + equals + attributeValue)

    levelName = makeKeywords(["Chain", "Residue", "Atom"]).setResultsName("levelName")
    level = Group(levelName + OneOrMore(property))

    predefined = Dict(Group(Keyword("motifDescription") + equals + Keyword("PREDEFINED") + quotedWord))
    defined    = Dict(Group(Keyword("motifDescription") + equals + OneOrMore(level)))

    motifDescription = defined ^ predefined

    # filenames and filepaths
    validfilechars = alphanums + "_.-"
    filename = Word(validfilechars)
    root = tilde ^ dot
    unixStylePath = Combine(root + OneOrMore(backslash + filename) + ZeroOrMore(backslash))
    unixStylePath.setParseAction(lambda s, l, t: os.path.expanduser(t[0]))

    drive = Word(alphas) + colon + slash + slash
    winStylePath = Combine(drive + OneOrMore(filename + slash) + ZeroOrMore(filename))

    path = Literal(".") ^ winStylePath ^ unixStylePath
    filepath = Dict(Group(Keyword("filepath") + equals + path))

    filenames = Dict(Group(Keyword("filenames") + equals + lbracket + ZeroOrMore(filename) + rbracket))

    # measures
    geometricMeasureType = makeKeywords(["Distance", "Angle", "Torsion", "HBond"])
    geometricMeasure = Group(geometricMeasureType + selectionList)

    #residueSelectionList = Group(lbrace + OneOrMore(num) + rbrace).setResultsName("selectionList")
    #propertyMeasure = Group(Keyword("Property") + residueSelectionList + valueType)

    valueType = makeKeywords(["int", "float", "str"])
    propertyMeasure = Group(Keyword("Property") + integer + word + valueType)
    measure = geometricMeasure ^ propertyMeasure
    measures = Dict(Group(Keyword("measures") + equals + OneOrMore(measure)))

    grammar = Each([motifDescription, filepath, filenames, measures])
    grammar.ignore(comment)
    
    return grammar

"""
    Various mappings
"""
descriptionToSubDescriptionMap = { ChainDescription : ResidueDescription, ResidueDescription : AtomDescription }
levelNameDict = { "Chain" : ChainDescription, "Residue" : ResidueDescription, "Atom" : AtomDescription }
keywordToConditionMap = {   "chainID" : PropertyCondition,
                            "chainType" : PropertyCondition,
                            "position" : PropertyCondition,
                            "resname" : PropertyCondition,
                            "distance-bound" : DistanceBoundCondition,
                            "angle-bound" : AngleBoundCondition,
                            "torsion-bound" : TorsionBoundCondition,
                            "hydrogen-bond" : HBondCondition
                        }

measureDict = { "Property" : PropertyMeasure, "Distance" : DistanceMeasure, "Angle" : AngleMeasure, "Torsion" : TorsionMeasure, "HBond" : HBondMeasure }

# could just use eval() instead...
typeDict = { "str" : str, "int" : int, "float" : float }

def lookupMotif(motifDescriptionText):
    name = motifDescriptionText[1]
    return predefined_descriptions[name]

def makeMotif(motifDescriptionText):
    currentLevel = None
    currentLevelSubType = None
    motifDescription = None

    # create the Description hierarchy first, along with simple PropertyConditions
    for levelText in motifDescriptionText:
        # create a Description object of the appropriate type
        level = levelNameDict[levelText.levelName]({})

        # add the Description into the motifDescription
        if motifDescription is None:
            motifDescription = level
            currentLevel = motifDescription
            currentLevelSubType = descriptionToSubDescriptionMap[type(level)]
        else:
            if type(level) == currentLevelSubType:
                currentLevel.addSubDescription(level)
            else:
                # not sure! might need this for more complex (Chain-Residue-Atom) hierarchies
                pass

        # this is simply a hack
        if levelText.levelName == "Residue":
            # XXX line below commented out for proline hack!
            #for name in ["N", "CA", "C", "O", "H"]:
            for name in ["N", "CA", "C", "O"]:
                level.addAtom(name)

        # setup the PropertyConditions so that they can be selected
        for group in levelText[1:]:
            key, values = group[0], group[1:] 
            if key == "levelName": continue
            if keywordToConditionMap[key] == PropertyCondition:
                level.addPropertyCondition(key, values[0])
        #print("level", levelText)

    # in a second pass, create those Conditions which rely on the Description tree
    for levelText in motifDescriptionText:
        for group in levelText[1:]:
            key, attr = group[0], group[1:]
        
            if key in keywordToConditionMap and keywordToConditionMap[key] != PropertyCondition:
                params = makeSelections(motifDescription, attr[0])                    
                for otherParam in attr[1:]:
                    params.append(otherParam)
                #print(keywordToConditionMap[key], params)
                condition = keywordToConditionMap[key](*params)
                
                # this is absolutely wrong! Need to match the conditions to the right level!
                motifDescription.conditions.append(condition)

    motifDescription.name = "default"

    return motifDescription

def makeSelections(motifDefinition, selectionTextList):
    selectionList = []
    for selectionText in selectionTextList:

        # this is not very general!
        #print(selectionText)
        residueNumber, atomName = selectionText
        selection = motifDefinition.selectResidue(residueNumber).selectAtom(atomName)

        selectionList.append(selection)
    return selectionList 

def makeMeasures(motifDefinition, measureTextList):
    measures = []

    # again this ensures that single measures work!
    if type(measureTextList[0]) == str:
        tmp = []
        tmp.append(measureTextList[0])
        tmp.append(measureTextList[1])
        measureTextList = [tmp]

    for measureText in measureTextList:
        #print("debug", measureText)
        if measureText[0] == "Property":
            measureType, residueNumber, property, valueType = measureText
            measure = PropertyMeasure(motifDefinition.selectResidue(residueNumber), property, typeDict[valueType])
            measures.append(measure)
        else:
            measureType, selectionTextList = measureText
            selectionList = makeSelections(motifDefinition, selectionTextList)

            # this is possibly the most beautiful line of python I have ever written
            measure = measureDict[measureType](*selectionList)
            measures.append(measure)

    return measures

def translate(parsed):
    motifDescriptionText = parsed.motifDescription
    if motifDescriptionText[0] == "PREDEFINED":
        motifDescription = lookupMotif(motifDescriptionText)
    else:
        motifDescription = makeMotif(motifDescriptionText)
    filenames = parsed.filenames
    if filenames == "":
        filenames = []
    elif type(filenames) == str:
        filenames = [filenames]
    measureList = makeMeasures(motifDescription, parsed.measures)

    pipes = [Pipe(motifDescription, measureList, sys.stdout)]

    return Run(pipes, parsed.filepath, filenames)

if __name__ == "__main__":

    try:

        lines = open(sys.argv[1]).readlines()
        filestr = "".join(lines)
        print(filestr)
        grammar = makeGrammar()
        parsed = grammar.parseString(filestr)
        run = translate(parsed)
        run.run()

    except ParseException, p:
        print(p)
        col = p.col
        line = p.line
        print(line[0:col], "!", line[col:])
