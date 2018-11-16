# read in the output of a tailor search and count the numbers in predefined categories

import math, sys

def getStartEnd(rowData):
    s, e = rowData[1].split(' ')[1].split('-')
    return int(s), int(e)

def multislice(alist, indices):
    slice = []
    for index in indices:
        slice.append(alist[index])
    return slice

class AndCombiner(object):

    def __init__(self, filters):
        self.filters = filters

    def accepts(self, row):
        for filter in self.filters:
            if filter.accepts(row):
                continue
            else:
                return False
        return True

    def __repr__(self):
        return "AND [%s]" % ", ".join([str(f) for f in self.filters])

class UpperBound(object):

    def __init__(self, maxValue, columnIndex):
        self.maxValue = maxValue
        self.columnIndex = columnIndex

    def accepts(self, row):
        return float(row[self.columnIndex]) <= self.maxValue

    def __repr__(self):
        return "upper bound %s" % self.maxValue

class LowerBound(object):

    def __init__(self, minValue, columnIndex):
        self.minValue = minValue
        self.columnIndex = columnIndex

    def accepts(self, row):
        return float(row[self.columnIndex]) >= self.minValue

    def __repr__(self):
        return "upper bound %s" % self.minValue

class Range(object):

    def __init__(self, min, max):
        self.min = min
        self.max = max

    def satisfiedBy(self, value):
        return self.min < float(value) < self.max

    def __repr__(self):
        return "range (%s - %s)" % (self.min, self.max)

class SplitRange(object):

    def __init__(self, lower, upper):
        self.upper = upper
        self.lower = lower

    def satisfiedBy(self, value):
        return -180 < float(value) < self.lower or self.upper < float(value) < 180

    def __repr__(self):
        return "splitrange (%s - %s)" % (self.lower, self.upper)

class Category(object):

    def __init__(self, name, columnNumbers=[], conditions=[]):
        self.name = name
        self.columnNumbers = columnNumbers
        self.conditions = conditions
        self.members = []

    def clear(self):
        self.members = []

    def __iter__(self):
        return iter(self.members)

    def __len__(self):
        return len(self.members)

    def filter(self, filter):
        filtered_members = []
        for member in self:
            if filter.accepts(member):
                filtered_members.append(member)
        return filtered_members

    def accepts(self, bits):
        for i, columnNumber in enumerate(self.columnNumbers):
            value = bits[columnNumber]
            condition = self.conditions[i]
            if condition.satisfiedBy(value):
                continue
            else:
                return False
        return True

    def addMember(self, bits):
        self.members.append(bits)

    def numberOfMembers(self):
        return len(self.members)

    count = property(numberOfMembers)

    def calculateBounds(self):
        bounds = [[sys.maxint, -sys.maxint] for i in range(len(self.columnNumbers))]
        for member in self:
            for i, value in enumerate(multislice(member, self.columnNumbers)):
                value = float(value)
                min, max = bounds[i]
                if value < min:
                    bounds[i][0] = value
                if value > max: 
                    bounds[i][1] = value
        bounds = ["%4i to %4i" % (f1, f2) for f1, f2 in bounds]
        return bounds 

    bounds = property(calculateBounds)

    def calculateMeans(self):
        means = [0] * len(self.conditions)
        for member in self:
            for i, value in enumerate(multislice(member, self.columnNumbers)):
                value = float(value)
                means[i] += value
                
        means = [mean / float(self.count) for mean in means]
        return means

    means = property(calculateMeans)

    def calculateMeansWithStdDeviations(self):
        stdDevs = [0] * len(self.conditions)
        means = self.means
        #sqrt(sum(sqr(sample), ...) - sqr(sum(sample, ...)) / count) / (count - 1)
        for member in self:
            for i, value in enumerate(multislice(member, self.columnNumbers)):
                value = float(value)
                x = value - means[i]
                stdDevs[i] += x * x

        meanStdDev = []
        for i, stdDev in enumerate(stdDevs):
            stdDev = math.sqrt(stdDev / float(self.count - 1))
            meanStdDev.append((means[i], stdDev))
        
        return meanStdDev

    meanWithStdDevs = property(calculateMeansWithStdDeviations)

    def calculateStdErrs(self):
        rootSize = math.sqrt(self.size)
        return [d / rootSize for d in self.stdDevs]

    stdErrs = property(calculateStdErrs)

    def countOverlap(self, other):
        offsets = {}
        for member in self:
            pdbid = member[0]
            s1, e1 = getStartEnd(member)
            for otherMember in other:
                if pdbid != otherMember[0]: continue
                s2, e2 = getStartEnd(otherMember)
                if s2 < s1 < e2 or s1 < s2 < e1 or s2 < e1 < e2 or s1 < e2 < e2:
                    offset = s2 - s1
                    if offset in offsets:
                        offsets[offset] += 1
                    else:
                        offsets[offset] = 1
        return offsets

    def shallowCopy(self):
        return Category(self.name, self.columnNumbers, self.conditions)    

    def __repr__(self):
        return "%s %s %s" % (self.name, self.columnNumbers, self.conditions)

class CategorySet(object):

    def __init__(self, columnNumbers=[], regions={}):
        self.categories = []
        self.columnNumbers = columnNumbers
        self.regions = regions
        self.unmatched = Category("?", columnNumbers)

    def clear(self):
        for category in self.categories:
            category.clear()
        self.unmatched.clear()

    def countInCategories(self, categoryNameList):
        count = 0
        for categoryName in categoryNameList:
            count += len(self.getCategory(categoryName))
        return count

    def getUnmatched(self):
        return self.unmatched

    def getCategory(self, categoryName):
        for category in self:
            if category.name == categoryName:
                return category
        return None

    def __iter__(self):
        return iter(self.categories)

    def createCategoriesWithNames(self, names):
        for name in names:
            regionKeys = tuple(name)
            self.createCategoryFromRegions(*regionKeys)

    def createCategoryFromRegions(self, *regionKeys):
        conditionPairs = [self.regions[r] for r in regionKeys]
        categoryName = "".join(regionKeys)
        self.createCategory(categoryName, conditionPairs)

    """
        Create categories from a name and a list of pairs of
        conditions.
    """
    def createCategory(self, categoryName, conditionPairs):
        conditions = []
        for pair in conditionPairs:
            conditions.append(pair[0])
            conditions.append(pair[1])
        self.categories.append(Category(categoryName, self.columnNumbers, conditions))

    def assign(self, row):
        for category in self.categories:
            if category.accepts(row):
                category.addMember(row)
                return
        self.unmatched.addMember(row)

    def countAllMembers(self):
        total = 0
        for category in self.categories:
            total += category.count
        total += self.unmatched.count
        return total

    total = property(countAllMembers)

    def getOverlapCounts(self, otherCategorySet):
        overlapCounts = []
        for category in self:
            for other in otherCategorySet:
                offsets = category.countOverlap(other)
                data = (category.name, category.count, other.name, other.count, offsets)
                overlapCounts.append(data)
        return overlapCounts

    def printResults(self):
        for category in self:
            if category.count > 1:
                #print "%s %4d %s" % (category.name, category.count, category.bounds)
                meanStdDev = ["%4.0f +/- %4.0f" % ms for ms in category.meanWithStdDevs]
                print("%s %4d %s" % (category.name, category.count, meanStdDev))
            else:
                print("%s %4d" % (category.name, category.count))

        if self.unmatched.count > 0:
            print("%s %4d %s" % (self.unmatched.name, self.unmatched.count, self.unmatched.bounds))
        else:
            print("%s %4d" % (self.unmatched.name, self.unmatched.count))

    def fromFile(self, filename):
        fp = open(filename)
        self.fromLines(fp.readlines())
        fp.close()

    def fromLines(self, lines):
        for line in lines[1:]:
            bits = line.strip("\n").split("\t")
            self.assign(bits)

    def shallowCopy(self):
        shallowCopy = CategorySet(self.columnNumbers, self.regions)
        shallowCopy.categories = [c.shallowCopy() for c in self.categories]
        return shallowCopy

    def __repr__(self):
        return "%s categories covering %s members" % (len(self.categories), self.total)
