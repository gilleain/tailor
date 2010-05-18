"""
This module provides:
    - a Matcher object to match abstract Descriptions to Structures
    - lookup(description, structure) function to find concrete Descriptions in Structures.
"""
from Feature import Structure, Model, Chain
from Geometry import distance

import copy, logging, sys
logging.basicConfig(level=logging.INFO, format="%(levelname)s %(message)s")
logging.basicConfig(level=logging.DEBUG, format="%(levelname)s %(message)s")

def lookup(description, example):
    """ Return part of an example (which is itself a fragment of a whole structure) 
        given a description of what to look for.
    """
    if description.levelCode == "C":
        return lookupChain(description, example)
    elif description.levelCode == "R":
        return lookupResidue(description, example)

def lookupChain(chainDescription, structureExample):
    # XXX TODO : what if there is more than one chain?
    # XXX also : this could really do with not being a generator!
    chains = structureExample.chainsOfType(chainDescription.chainType)
    return lookupResidue(chainDescription.children[0], chains[0])
            
def lookupResidue(residueDescription, chainExample):
    atomDescription = residueDescription.children[0]
    residue = chainExample[residueDescription.position - 1]
    for atom in residue:
        if atomDescription.name == atom.name:
            #sys.stderr.write("%s found in %s\n" % (atomDescription.name, chainExample)) 
            return atom
    #sys.stderr.write("NO %s found in %s\n" % (atomDescription.name, chainExample)) 
    return None

def generateGaps(pairlist):
    """ Given a list like [(0, 1), (0, 1), (0, 1)] will return all combinations.
        This should be something like:
            [0, 0, 0]
            [0, 0, 1]
            [0, 1, 0]
            [0, 1, 1]
            [1, 0, 0]
            [1, 0, 1]
            [1, 1, 0]
            [1, 1, 1]
        Drives the recursivelyGenerateGaps function.
    """
    return [gaplist for gaplist in recursivelyGenerateGaps(pairlist)]

def recursivelyGenerateGaps(pairlist):
    """ A generator for lists of numbers.
    """
    if len(pairlist) > 1:
        min, max = pairlist[0]
        for i in range(min, max + 1):
            for rest in recursivelyGenerateGaps(pairlist[1:]):
                yield [i] + rest
    else:
        min, max = pairlist[0]
        for i in range(min, max + 1):
            yield [i]

def combine(*seqin):
    '''returns a list of all combinations of argument sequences.
    for example: combine((1,2),(3,4)) returns
    [[1, 3], [1, 4], [2, 3], [2, 4]]'''
    def rloop(seqin, listout, combinations):
        '''recursive looping function'''
        if seqin:                                           # any more sequences to process?
            for item in seqin[0]:
                newcombinations = combinations + [item]     # add next item to current comb
                # call rloop w/ rem seqs, newcomb
                rloop(seqin[1:], listout, newcombinations)
        else:                                               # processing last sequence
            listout.append(combinations)                    # comb finished, add to list
    listout = []                                            # listout initialization
    rloop(seqin, listout, [])                               # start recursive process
    return listout

class Matcher(object):

    def __init__(self, description):
        self.description = description
        logging.debug("matcher with description %s" % description)

    def findAll(self, structure):
        """ Use the description of this matcher to find all examples in structure.
        """
        matches = []

        # top level is Structure -> search through combinations of Chains
        if self.description.levelCode == "S":
            logging.debug("structure description")

            # select chains of a particular type and get matches
            matchMap = {}
            chainIDLists = []
            for chainDescription in self.description:
                chainIDs = []
                for chain in structure.chainsOfType(chainDescription.chainType):
                    matchMap[chain.chainID] = self.findMatchesInChain(chainDescription, chain)
                    logging.debug("adding chain %s" % chain.chainID)
                    chainIDs.append(chain.chainID)
                chainIDLists.append(chainIDs)

            # work on tuples of chains
            chainCombinations = combine(*chainIDLists)
            for chainCombo in chainCombinations:
                logging.debug("trying chains: %s" % chainCombo)

                # combine these matches into whole motifs
                # TODO XXX this should really be cross-product!
                # currently only works with pairs...
                for matchA in matchMap[chainCombo[0]]:
                    for matchB in matchMap[chainCombo[1]]:

                        # XXX AARGH! : we make the fragment before testing??!
                        fragment = Structure(self.description.name)
                        model = Model(0)
                        fragment.add(model)
                        model.add(matchA)
                        model.add(matchB)

                        if self.matchConditions(fragment):
                            matches.append(fragment)

        # top level is Chain -> search through combinations of Residues
        elif self.description.levelCode == "C":
            logging.debug("chain description")

            # find the next suitable chain to look through
            for chain in structure.chainsOfType(self.description.chainType):
                logging.debug("finding matches in chain %s" % chain.chainID)
                possibles = self.findMatchesInChain(self.description, chain)
                for p in possibles:
                    if self.matchConditions(p):
                        matches.append(p)
        return matches

    # XXX this is surely mis-named!
    def matchConditions(self, fragment):
        for condition in self.description.conditions:
            logging.debug("checking condition %s" % condition)
            if condition.__dict__.has_key('propertyKey') and condition.propertyKey == "chainID": continue
            if condition.satisfiedBy(fragment):
                continue
            else:
                return False
        return True

    def findMatchesInChain(self, chainDescription, chain):
        chainMatches = []

        # the maximum index a match can start at
        descriptionLength = len(chainDescription)
        maxIndex = len(chain) - descriptionLength

        for startIndex in range(0, maxIndex):

            # its cheaper to store the indices than to construct partial matches
            matchingIndices = []
            for i, residueDescription in enumerate(chainDescription):
                currentIndex = startIndex + i
                if self.matchResidue(residueDescription, chain[currentIndex]):
                    matchingIndices.append(currentIndex)
                else:
                    break

            # store whole matches
            if len(matchingIndices) == descriptionLength:
                chainExample = Chain(chain.chainID, chain.chainType)
                for index in matchingIndices:
                    chainExample.subFeatures.append(copy.copy(chain[index]))
                logging.debug("storing %s" % chainExample)
                chainMatches.append(chainExample)
            
        return chainMatches

    # TODO : this needs to be implemented!
    def matchResidue(self, residueDescription, residue):
        logging.debug("%s -> %s" % (residueDescription, residue))
        return True
