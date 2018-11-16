import sys
from Tailor.Category import SplitRange, Range, Category, CategorySet

columnNumbers = [2, 3, 4, 5]

regions = {
            "R" : (Range(-180, 0), Range(-180, 180)),
            "L" : (Range(0, 180), Range(-180, 180)),
            "X" : (Range(0, 180), SplitRange(-90, 90)),
          }

categorySet = CategorySet(columnNumbers, regions)
categorySet.createCategoryFromRegions("R", "R")
categorySet.createCategoryFromRegions("L", "L")
#categorySet.createCategoryFromRegions("R", "L")
categorySet.createCategoryFromRegions("R", "X")
categorySet.createCategoryFromRegions("L", "R")

categorySet.fromFile(sys.argv[1])
categorySet.printResults()
print("total", categorySet.total)

for category in categorySet:
    for member in category:
        print(category.name, member)
