package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	public record Component(List<GroupDescription> groupDescriptions, List<AtomListDescription> atomListDescriptions) {}

    // Maps each group to its current root (parent). LinkedHashMap preserves
    // insertion order so component lists come out in a predictable sequence.
    private final Map<GroupDescription, GroupDescription> parent = new LinkedHashMap<>();
    
    private final Map<GroupDescription, AtomListDescription> groupDescriptionToAtomListDescriptionMap = new HashMap<>();

    public GroupUnionFind(List<GroupDescription> groups) {
        for (GroupDescription group : groups) {
            parent.put(group, group);
        }
    }

    /**
     * Merge the components connected by each atom-list description.
     * Each description is a hyperedge in the description graph: 
     * all groups it references end up in the same component.
     */
    public void union(List<AtomListDescription> descriptions) {
        for (AtomListDescription description : descriptions) {
            List<GroupDescription> groups = description.getGroupDescriptions();
            groups.stream()
            	  .forEach(g -> groupDescriptionToAtomListDescriptionMap.put(g, description));
            
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
            buckets.computeIfAbsent(root, k -> new ArrayList<GroupDescription>()).add(group);
        }
        return new ArrayList<>(buckets.values());
    }
    
    public List<Component> getComponents2() {
        Map<GroupDescription, List<GroupDescription>> buckets = new LinkedHashMap<>();
        for (GroupDescription group : parent.keySet()) {
            GroupDescription root = find(group);
            List<GroupDescription> componentGroups = null;
            if (buckets.containsKey(root)) {
            	componentGroups = buckets.get(root);
            } else {
            	componentGroups = new ArrayList<>();
            	buckets.put(root, componentGroups);
            }
            componentGroups.add(group);
        }
        return buckets.values().stream().map(c -> new Component(c, get(c))).toList();
    }

	private List<AtomListDescription> get(List<GroupDescription> component) {
		List<AtomListDescription> atomListDescriptions = new ArrayList<>();
		for (GroupDescription groupDescription : component) {
			if (groupDescriptionToAtomListDescriptionMap.containsKey(groupDescription)) {
				atomListDescriptions.add(groupDescriptionToAtomListDescriptionMap.get(groupDescription));
			}
		}
		return atomListDescriptions;
	}
}
