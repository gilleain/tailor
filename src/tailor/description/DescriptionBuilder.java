package tailor.description;

public class DescriptionBuilder {
    
    private Description root;
    
    private Description currentParent;
    
    private DescriptionBuilder() {
        this.root = null;
    }
    
    public Description get() {
        return this.root;
    }
    
    public GroupDescription getGroup() {
        return (GroupDescription) this.root;
    }
    
    public static DescriptionBuilder makeDescription() {
        return new DescriptionBuilder();
    }
    
    private void setRoot(Description description) {
        this.root = description;
        this.currentParent = this.root;
    }
    
    public DescriptionBuilder protein() {
        this.root = new ProteinDescription();
        this.currentParent = this.root;
        return this;
    }
    
    public DescriptionBuilder chain(String name) {
        ChainDescription chain = new ChainDescription(name);
        if (root == null) {
            setRoot(chain);
        } else {
            this.currentParent.addSubDescription(chain);
            this.currentParent = chain;
        }
        return this;
    }
    
    public DescriptionBuilder group(String name) {
        GroupDescription group = new GroupDescription(name);
        if (root == null) {
            setRoot(group);
        } else {
            this.currentParent.addSubDescription(group);
            this.currentParent = group;
        }
        return this;
    }
    
    public DescriptionBuilder atoms(String... atomNames) {
        if (root == null) {
            // TODO : throw exception?
        } else {
            for (String atomName : atomNames) {
                currentParent.addSubDescription(new AtomDescription(atomName));
            }
        }
        return this;
    }

}
