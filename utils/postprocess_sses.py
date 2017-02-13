import sys, re

match_p = re.compile("(.{4}) (.+) .?(\d+) to\s+.?(\d+) matches (.+) : .{3} (\d+) - .{3} (\d+)")
nomatch_p = re.compile("(.{4} (.+) .?(\d+) to\s+.?(\d+) has no match")
superf_p = re.compile("(.{4}) (.+) : .{3} (\d+) - .{3} (\d+)")

bypdbid = {}

class SSE(object):

    def __init__(self, ssetype, start, end):
        self.ssetype = ssetype
        self.start = start
        self.end = end

    

class SSEMatch(object):

    def __init__(self, target, obs):
        self.target = target
        self.obs = obs

    def fits(self, sse):
        

for line in open(sys.argv[1]):
    m = match_p.match(line)
    if m:
        target = SSE(m.group(2), m.group(3), m.group(4))
        obs = SSE(m.group(5), m.group(6), m.group(7))
        match = SSEMatch(target, obs)
    else:
        m = nomatch_p.match(line)
        if m:
            missing = SSE(m.group(1), m.group(2), m.group(3))
        else:
            m = superf_p.match(line)
            if m:
                superf = SSE(m.group(1), m.group(2), m.group(3)) 
