package tailor.editor;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import tailor.description.Description;
import tailor.description.DescriptionFactory;
import tailor.description.ProteinDescription;
import tailor.structure.Level;

public class DescriptionTreeView extends JPanel  {

	private JTree descriptionTree;
    
    private ArrayList<Description> descriptions;
    
	private DefaultTreeModel model;
    
	private JScrollPane scrollPane;
	
	/**
     * Set up a basic empty tree, but add the root and a single chain.
     * The expand flag is for the TreeList in the main app.
     * 
	 * @param isExpanded whether to expand the tree   
	 */
	public DescriptionTreeView(boolean isExpanded) {
        
        /*
         * Note that this root is the parent for one or more Description trees.
         * That is to say, the top node (the root) of a description is a child
         * of this root.
         */ 
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		this.model = new DefaultTreeModel(root);
		this.descriptionTree = new JTree(this.model);
        Dimension pref = new Dimension(180, 300);
//        this.descriptionTree.setPreferredSize(pref);

		this.descriptionTree.setShowsRootHandles(false);
        this.descriptionTree.setRootVisible(false);
        
        // FIXME : this is wrong somehow.
//        if (isExpanded) {
            this.descriptionTree.setExpandsSelectedPaths(true);
            this.descriptionTree.setScrollsOnExpand(true);
            this.descriptionTree.setSelectionRow(1);
//        }
        
		this.descriptionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.scrollPane = new JScrollPane(this.descriptionTree);
        this.scrollPane.setPreferredSize(pref);
		
		this.add(this.scrollPane);
        this.setPreferredSize(pref);
        
        this.descriptions = new ArrayList<Description>();
	}
    
    /**
     * Create the tree from an existing Description.
     * 
     * @param description the Description tree to display
     */
    public DescriptionTreeView(Description description) {
        this(true);
        
        this.addTree(description, 0);
        this.model.reload();
    }
    
    /**
     * Count the number of trees in this view.
     * 
     * @return the number of Description trees contained herein.
     */
    public int numberOfTrees() {
        return this.getRoot().getChildCount();
    }

    public int getTreeSize(int i) {
        return this.getDescriptionRoot(i).getLeafCount();
    }
    
    public DefaultMutableTreeNode getDescriptionRoot(int i) {
        return ((DefaultMutableTreeNode) this.getRoot().getChildAt(i));
    }
 
    // TODO : the distinction between ProteinDescriptions and Descriptions needs to be sorted out!
    
    /**
     * Adds a Description tree to the end of the list.
     * 
     * @param descriptionTree the tree to add
     */
    public void addDescriptionTree(ProteinDescription descriptionTree) {
        int position = this.numberOfTrees();
        this.addTree(descriptionTree, position);
    }

    /**
     * Add a tree to the list of trees at a particular 
     * index in the children of the root. If the index
     * is greater than the number of trees (-1), then 
     * add to the end of the list.
     * 
     * @param description the root of the tree to add
     * @param index the position to add it at
     */
    public void addTree(Description description, int index) {
        System.out.println("adding tree " + description.toPathString());
        DefaultMutableTreeNode root = this.getRoot();
        DefaultMutableTreeNode descriptionRoot = new DefaultMutableTreeNode();
        
        int childCount = root.getChildCount();
        if (index > childCount - 1) {
            index = childCount;
        }
        
        this.descriptions.add(description);
        root.insert(descriptionRoot, index);
        this.recursivelyAdd(descriptionRoot, description);
        
        this.model.reload();
        
        System.out.println("new tree size " + this.getTreeSize(index));
    }
    
    // TODO : the distinction between ProteinDescriptions and Descriptions needs to be sorted out!
    
    public void recursivelyAdd(DefaultMutableTreeNode current, Description description) {
        this.addDescriptionToNode(current, description);
        for (Description child : description.getSubDescriptions()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
            current.add(childNode);
            this.recursivelyAdd(childNode, child);
        }
    }

    /**
     * The simplest method for adding new Descriptions to
     * the tree. Asks the tree for its current selection
     * (if any) and adds the appropriate type of child.
     * Dangerous, as it assumes that if there is no
     * current selection, there is no tree...
     * 
     * @param name the Description's name.
     * @param position in case the Description is a group.
     */
    public void addDescriptionToChildOfCurrentlySelectedNode(String name, int position) {
        DefaultMutableTreeNode currentlySelectedNode = this.getCurrentlySelectedNode();
        
        // no current protein root...make one
        // FIXME : this doesn't really make sense. Perhaps throw error.
        if (currentlySelectedNode == null) {
            DefaultMutableTreeNode root = this.getRoot();
            Description description = 
                DescriptionFactory.createFromLevel(Level.PROTEIN, name);
            this.addDescriptionToNewChildNode(root, description);
        } else {
            Level currentLevel = 
                ((Description) currentlySelectedNode.getUserObject()).getLevel();
            Level childLevel = DescriptionFactory.getSubLevel(currentLevel);
            if (position == -1) {
                position = currentlySelectedNode.getChildCount();
            }
            Description description = 
                DescriptionFactory.createFromLevel(childLevel, name);
            this.addDescriptionToNewChildNode(currentlySelectedNode, description);
        }
    }

    /**
     * Append a new child node to the parent node and add the description to it. 
     * 
     * @param node the parent node
     * @param description the user object to add
     */
    public void addDescriptionToNewChildNode(DefaultMutableTreeNode node, Description description) {
    	DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
        this.model.insertNodeInto(childNode, node, node.getChildCount());
    	this.addDescriptionToNode(childNode, description);
        Object parentObject = node.getUserObject();
        
        if (parentObject == null) {
            // node is a description root
            int descriptionIndex = this.getRoot().getIndex(childNode);
            this.descriptions.add(descriptionIndex, description);
        } else {
            Description parentDescription = (Description) parentObject;
            parentDescription.addSubDescription(description);
        }
    }

    /**
     * Add a description to a node. Note that this method does NOT
     * have the side-effect of adding the description to the parent
     * node's description.
     * 
     * @param node the treenode to add the description to
     * @param description the description to add
     */
    public void addDescriptionToNode(DefaultMutableTreeNode node, Description description) {
        node.setUserObject(description);
        System.out.println("Added user object : " + description);
        this.model.reload();
        TreePath path = new TreePath(node.getPath());
        this.descriptionTree.scrollPathToVisible(path);
        this.descriptionTree.setSelectionPath(path);
    }

    public DefaultMutableTreeNode getRoot() {
        Object root = this.model.getRoot();
        if (root == null) {
            root = new DefaultMutableTreeNode();
            this.model.setRoot((DefaultMutableTreeNode) root);
            return (DefaultMutableTreeNode) this.model.getRoot();
        } else {
            return (DefaultMutableTreeNode) root;
        }
    }

    public void expandAll() {
        int leafCount = ((DefaultMutableTreeNode)this.model.getRoot()).getLeafCount();
        
        // FIXME : surely all these calls are not necessary!
        this.descriptionTree.setVisibleRowCount(leafCount);
        for (int i = 0; i <= leafCount; i++) {
            this.descriptionTree.expandRow(i);
            this.descriptionTree.scrollRowToVisible(i);
        }
    }
    
    /**
     * Removes all Description trees from the view.
     */
    public void clear() {
        this.model.setRoot(new DefaultMutableTreeNode());
        this.descriptions.clear();
    }
    
    public void clearSelection() {
        this.descriptionTree.clearSelection();
    }
	
	public void addTreeSelectionListener(TreeSelectionListener listener) {
		this.descriptionTree.addTreeSelectionListener(listener);
	}
	
	public Level getLevelBelowCurrentlySelectedLevel() {
		Level currentLevel = this.getCurrentlySelectedLevel();
		return DescriptionFactory.getSubLevel(currentLevel);
	}
    
    public DefaultMutableTreeNode getCurrentlySelectedNode() {
        TreePath path = this.descriptionTree.getSelectionPath();
        if (path != null) {
            return (DefaultMutableTreeNode) path.getLastPathComponent();
        } else {
            return null;
        }
    }
    
	public Level getCurrentlySelectedLevel() {
		DefaultMutableTreeNode currentlySelectedNode = this.getCurrentlySelectedNode();
        if (currentlySelectedNode == null) {
            return null;
        } else {
            return ((Description) currentlySelectedNode.getUserObject()).getLevel();
        }
	}
    
    /**
     * Note that this method does NOT return the whole Description
     * of which a particular node may be selected, but only the
     * Description at that node (which references all subdescriptions).
     * 
     * @return the Description at the currently selected node
     */
    public Description getCurrentlySelectedDescription() {
        DefaultMutableTreeNode currentlySelectedNode = this.getCurrentlySelectedNode();
        if (currentlySelectedNode == null) {
            return null;
        } else {
            return (Description) currentlySelectedNode.getUserObject();
        }
    }
    
    /**
     * Creates a Description path to the selected node.
     * 
     * @return the root of a new description path
     */
    public Description createPathToSelected() {
        TreePath path = this.descriptionTree.getSelectionPath();
        if (path == null) {
            return null;
        } else {
            Description descriptionPathRoot = null;
            Description currentLevel = null;
            Object[] pathArray = path.getPath();
            for (int i = 1; i < pathArray.length; i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathArray[i];
                Description description = (Description) node.getUserObject();
                System.err.println("adding " + description);
                if (descriptionPathRoot == null) {
                    descriptionPathRoot = description.shallowCopy();
                    currentLevel = descriptionPathRoot;
                } else {
                    Description copy = description.shallowCopy();
                    currentLevel.addSubDescription(copy);
                    currentLevel = copy;
                }
            }
            System.err.println("returning " + descriptionPathRoot.toPathString());
            return descriptionPathRoot;
        }
    }

    public ProteinDescription[] getAllDescriptions() {
        ProteinDescription[] proteinDescriptions = new ProteinDescription[this.descriptions.size()];
        for (int i = 0; i < this.descriptions.size(); i++) {
            proteinDescriptions[i] = this.getDescription(i);
        }
        return proteinDescriptions;
    }
    
    public ProteinDescription getDescription(int index) throws ArrayIndexOutOfBoundsException {
//        return this.convertTreeModelToDescription(this.getRoot(), index);
        return (ProteinDescription) this.descriptions.get(index);
    }
	
	public void deleteSelectedDescription() {
		DefaultMutableTreeNode currentNode = this.getCurrentlySelectedNode();
		if (currentNode != null) {
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentNode.getParent();
			if (parent != null ) {
				this.model.removeNodeFromParent(currentNode);
			}
		}
	}
	
}
