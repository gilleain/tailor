from Tailor.Description import predefined_descriptions
from Tailor.Run import Run

nest = predefined_descriptions["RLnest"]
run = Run(nest, "data", ["test.pdb"], [])
run.run()
