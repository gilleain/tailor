import sys
from Tailor.Category import SplitRange, Range, Category, CategorySet

columnNumbers = [2, 3, 4, 5]

regions = {
            "A" : (Range(-180, 0), Range(-90, 90)),
            "B" : (Range(-180, 0), Range(90, 180)),
            "R" : (Range(-180, 0), Range(-180, 180)),
            "L" : (Range(0, 180), Range(-180, 180)),
          }

categorySet = CategorySet(columnNumbers, regions)
categorySet.createCategoryFromRegions("A", "B")
categorySet.createCategoryFromRegions("B", "A")
categorySet.createCategoryFromRegions("R", "R")
categorySet.createCategoryFromRegions("L", "L")
categorySet.createCategoryFromRegions("L", "R")
categorySet.createCategoryFromRegions("R", "L")

categorySet.fromFile(sys.argv[1])

if len(sys.argv) == 2:
    categorySet.printResults()
    print("total", categorySet.total)

else:
    selectedName = sys.argv[2]
    for member in categorySet.getCategory(selectedName):
        print("\t".join(member))

#for category in categorySet:
    #for member in category: print(category.name, member)
