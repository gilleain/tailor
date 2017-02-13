import sys, re

p = re.compile("(.{4}) (.+) (\d+) to\s+(\d+) matches (.+) : (.{3}) (\d+) - (.{3}) (\d+)")

hist = {}
for line in open(sys.argv[1]):
    m = p.match(line)
    if m:
        id, target_type, t_start, t_end, obs_type, a, start, b, end = m.groups() 
        t_start = int(t_start)
        t_end = int(t_end)
        start = int(start)
        end = int(end)
        absdiff = (t_end - t_start) - (end - start)
        pair_diff = (start - t_start), (t_end - end)
        #print absdiff, id, target_type, obs_type, t_start, t_end, start, end
        #if absdiff in hist: hist[absdiff] += 1
        #else: hist[absdiff] = 1

        print pair_diff, id, target_type, obs_type, t_start, t_end, start, end
        if pair_diff in hist: hist[pair_diff] += 1
        else: hist[pair_diff] = 1
#for k, v in hist.iteritems(): print v, k
