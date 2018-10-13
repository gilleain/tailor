# Scripting

## Purpose of scripts

The script interface to Tailor is there to make it simpler to write programs. They are also easier to read than even a quite simple program, although they are less flexible. Also it is not necessary to know how to program in python to use the scripts. Scripts are just plain text files.

## Script basics

The four essential elements of a script are:

* The motif description
* One or more measures
* A filepath
* A list of filenames

In no particular order. Spaces and newlines are not important, so it should be possible to format as desired.

An example script:

```
filepath         = ./data
filenames        = [test.pdb]
motifDescription = PREDEFINED 'RLnest'
measures         = Distance (1.N 3.N)
```

## Motif description

The most complicated part of the script is the description of the motif. Motifs are hierarchical, like proteins, and have a number of conditions. All conditions must be satisfied for the description to match some part of a structure.

A motif can have any number of conditions, but the more it has the less common it will be. Also, motifs are not currently checked for conflicting conditions. A number of predefined motifs are provided, as in the example above.

New or altered motifs can be defined like so:

```
motifDescription = Chain
    chainID = 'RLnestPlusHBond'
    torsion-bound = (1.C 2.N  2.CA 2.C) -90 +/- 30
    torsion-bound = (2.N 2.CA 2.C  3.N)   0 +/- 30

    torsion-bound = (2.C 3.N  3.CA 3.C)  90 +/- 30
    torsion-bound = (3.N 3.CA 3.C  4.N)   0 +/- 30

    hydrogen-bond = (4.N 4.H 2.OD1 2.CG) 3.0 120.0 90.0

    Residue position = 1
    Residue position = 2 resname = 'ASN'
    Residue position = 3
    Residue position = 4
```

This is the motif defined in the file `nestmeasure.scr` in the `scripts` directory.

Each of the `torsion-bound` lines are conditions. They specify that the features that match this description must have phi and psi angles within the bounds set. The `hydrogen-bond` line is similarly a condition that must be satisfied. It specifies some conditions that the ASN sidechain OD1 and fourth NH must meet.

Following these conditions are the sub levels in the description. It is essential that any conditions for a description level come before any sub levels. In this case, only one of the sublevels has a complicated description - residue 2 must be an ASN.

## Measures

Measures are specified as a type (Distance, Angle, Torsion) followed by a list of atom selections. Once a motif has matched its description, the measures are applied to it. The resulting numbers are printed out in the order that they are defined in the script.

To measure geometric parameters that were used in the definition of the motif it is necessary to redefine them. For example, to print out the phi, psi torsions of the nest motifs found with the example above:

```
measures = Torsion  (1.C 2.N 2.CA 2.C)
           Torsion  (2.N 2.CA 2.C 3.N)

           Torsion  (2.C 3.N 3.CA 3.C)
           Torsion  (3.N 3.CA 3.C 4.N)

           HBond    (4.N 4.H 2.OD1 2.CG)
```

This will also provide the particular combination of N-H-OD / H-OD-CG angles and the OD-H distance found in the hydrogen bond.

## Files and paths

Finally, the data to search through is specified as a path and a list of filenames. The path must be to a directory, and the list can have one or more filenames or be just empty. An empty list - like "[]" - indicates that all the files in that directory should be used. Just make sure that they are all pdb files!

It is a good idea to try out a script on just one or a few files first. That way, if some subtle error in the definition has been made it should show up quickly. The alternative of waiting for hundreds of incorrect results is less agreeable...
