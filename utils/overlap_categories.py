import sys
from Tailor.Category import Range, Category, CategorySet

columnNumbers = [2, 3, 4, 5, 6, 7]

regions = {
            "R" : (Range(-180, 0), Range(-180, 180)),
            "L" : (Range(0, 180), Range(-180, 180)),
            "A" : (Range(-150, -30), Range(-60, 50)),
            "a" : (Range(30, 150), Range(-30, 90)),
            "B" : (Range(-180, 30), Range(90, 180)),
          }

categorySet = CategorySet(columnNumbers, regions)
categorySet.createCategoryFromRegions("A", "A", "A")
categorySet.createCategoryFromRegions("A", "A", "B")
categorySet.createCategoryFromRegions("A", "B", "B")
categorySet.createCategoryFromRegions("A", "B", "A")
categorySet.createCategoryFromRegions("R", "R", "R")

categorySet.createCategoryFromRegions("R", "B", "a")
categorySet.createCategoryFromRegions("R", "B", "L")

categorySet.createCategoryFromRegions("R", "A", "a")
categorySet.createCategoryFromRegions("R", "A", "L")

categorySet.createCategoryFromRegions("R", "R", "L")

categorySet.createCategoryFromRegions("R", "L", "A")
categorySet.createCategoryFromRegions("R", "a", "B")
categorySet.createCategoryFromRegions("R", "L", "B")
categorySet.createCategoryFromRegions("R", "L", "R")

categorySet.createCategoryFromRegions("R", "a", "a")
categorySet.createCategoryFromRegions("R", "L", "L")

categorySet.createCategoryFromRegions("L", "R", "L")
categorySet.createCategoryFromRegions("L", "L", "R")
categorySet.createCategoryFromRegions("L", "R", "R")
categorySet.createCategoryFromRegions("L", "L", "L")

categorySet.fromFile(sys.argv[1])

categorySetCopy = categorySet.shallowCopy()
categorySetCopy.fromFile(sys.argv[2])

overlapCounts = categorySet.getOverlapCounts(categorySetCopy)
for c1, c1count, c2, c2count, offsets in overlapCounts:
    count = 0
    for offset in offsets:
        count += offsets[offset]
    if count > 0:
        print(count, c1, c1count, c2, c2count, offsets)
#categorySet.printResults()
#print "total", categorySet.total
