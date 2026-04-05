package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tailor.experiment.api.AtomListDescription;
import tailor.experiment.description.GroupDescription;

/**
 * Union-find over {@link GroupDescription}s, where each outer (cross-group)
 * {@link AtomListDescription} is a hyperedge connecting all the groups it
 * references. Calling {@link #union(List)} merges components across every
 * hyperedge, and {@link #getComponents()} returns the resulting sets.
 */
public class GroupUnionFind {

    // Maps each group to its current root (parent). LinkedHashMap preserves
    // insertion order so component lists come out in a predictable sequence.
    private final Map<GroupDescription, GroupDescription> parent = new LinkedHashMap<>();

    public GroupUnionFind(List<GroupDescription> groups) {
        for (GroupDescription group : groups) {
            parent.put(group, group);
        }
    }

    /**
     * Merge the components connected by each outer atom-list description.
     * Each description is a hyperedge: all groups it references end up in
     * the same component.
     */
    public void union(List<AtomListDescription> outerDescriptions) {
        for (AtomListDescription description : outerDescriptions) {
            List<GroupDescription> groups = description.getGroupDescriptions();
            // Union every group in the hyperedge with the first — transitivity
            // of union ensures all groups in the edge land in one component.
            GroupDescription pivot = groups.get(0);
            for (int i = 1; i < groups.size(); i++) {
                union(pivot, groups.get(i));
            }
        }
    }

    /** Find the root of {@code group}'s component, with path compression. */
    public GroupDescription find(GroupDescription group) {
        GroupDescription p = parent.get(group);
        if (p != group) {
            p = find(p);
            parent.put(group, p); // path compression
        }
        return p;
    }

    /** Merge the components containing {@code a} and {@code b}. */
    private void union(GroupDescription a, GroupDescription b) {
        GroupDescription rootA = find(a);
        GroupDescription rootB = find(b);
        if (rootA != rootB) {
            parent.put(rootB, rootA);
        }
    }

    /**
     * Return the connected components as a list of groups, one list per
     * component. Order within each component matches insertion order.
     */
    public List<List<GroupDescription>> getComponents() {
        Map<GroupDescription, List<GroupDescription>> buckets = new LinkedHashMap<>();
        for (GroupDescription group : parent.keySet()) {
            GroupDescription root = find(group);
            buckets.computeIfAbsent(root, k -> new ArrayList<>()).add(group);
        }
        return new ArrayList<>(buckets.values());
    }
}
