package tailor.engine.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tailor.api.AtomListDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.description.group.GroupSequenceDescription;

/**
 * Union-find over {@link GroupDescription}s, where each outer (cross-group)
 * {@link AtomListDescription} is a hyperedge connecting all the groups it
 * references. Calling {@link #union(List)} merges components across every
 * hyperedge, and {@link #getComponents()} returns the resulting sets.
 */
public class GroupUnionFind {
	
	public record Component(List<GroupDescription> groupDescriptions, 
							Set<AtomListDescription> atomListDescriptions,
							Set<GroupSequenceDescription> groupSequenceDescriptions) {}
	
   /**
    * Maps each group to its current root (parent). LinkedHashMap preserves
    *  insertion order so component lists come out in a predictable sequence.
    */
    private final Map<GroupDescription, GroupDescription> parent = new LinkedHashMap<>();
    
    /**
     * Associate groups with their atom list descriptions
     */
    private final Map<GroupDescription, List<AtomListDescription>> groupDescriptionToAtomListDescriptionMap = new HashMap<>();
    
    /**
     * Associate groups with their group sequence descriptions
     */
    private final Map<GroupDescription, GroupSequenceDescription> groupDescriptionToGroupSeqDescriptionMap = new HashMap<>();

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
    public void union(List<AtomListDescription> atomListDescriptions, List<GroupSequenceDescription> groupSequenceDescriptions) {
    	List<List<GroupDescription>> groupDescriptionLists = new ArrayList<>();
        for (AtomListDescription description : atomListDescriptions) {
        	List<GroupDescription> groups = description.getGroupDescriptions();
            add(description, groups);
            groupDescriptionLists.add(groups);
        }
        
        for (GroupSequenceDescription groupSequenceDescription : groupSequenceDescriptions) {
        	groupDescriptionLists.add(List.of(groupSequenceDescription.getStart(), groupSequenceDescription.getEnd()));
        	groupDescriptionToGroupSeqDescriptionMap.put(groupSequenceDescription.getStart(), groupSequenceDescription);
        	groupDescriptionToGroupSeqDescriptionMap.put(groupSequenceDescription.getEnd(), groupSequenceDescription);
        }
        
        for (List<GroupDescription> groups : groupDescriptionLists) {
        	 // Union every group in the hyperedge with the first — transitivity
            // of union ensures all groups in the edge land in one component.
            GroupDescription pivot = groups.get(0);
            for (int i = 1; i < groups.size(); i++) {
                union(pivot, groups.get(i));
            }
        }
        
    }
    
    private void add(AtomListDescription description, List<GroupDescription> groups) {
         for (GroupDescription groupDescription : groups) {
         	List<AtomListDescription> atomListDescriptions;
         	if (groupDescriptionToAtomListDescriptionMap.containsKey(groupDescription)) {
         		atomListDescriptions = groupDescriptionToAtomListDescriptionMap.get(groupDescription);
         	} else {
         		atomListDescriptions = new ArrayList<>();
         		groupDescriptionToAtomListDescriptionMap.put(groupDescription, atomListDescriptions);
         	}
         	atomListDescriptions.add(description);
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
    public List<Component> getComponents() {
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
        return buckets.values().stream().map(this::makeComponent).toList();
    }

	private Component makeComponent(List<GroupDescription> component) {
		Set<AtomListDescription> atomListDescriptions = new HashSet<>();
		Set<GroupSequenceDescription> groupSequenceDescriptions = new HashSet<>();
		for (GroupDescription groupDescription : component) {
			if (groupDescriptionToAtomListDescriptionMap.containsKey(groupDescription)) {
				atomListDescriptions.addAll(groupDescriptionToAtomListDescriptionMap.get(groupDescription));
			}
			if (groupDescriptionToGroupSeqDescriptionMap.containsKey(groupDescription)) {
				groupSequenceDescriptions.add(groupDescriptionToGroupSeqDescriptionMap.get(groupDescription));
			}
		}
		return new Component(component, atomListDescriptions, groupSequenceDescriptions);
	}
}
