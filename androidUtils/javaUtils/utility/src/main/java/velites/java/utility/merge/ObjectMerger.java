package velites.java.utility.merge;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;

public final class ObjectMerger
{
    private final ObjectDiffer objectDiffer;

    public ObjectMerger(boolean useField)
    {
        ObjectDifferBuilder odb = ObjectDifferBuilder.startBuilding();
        if (useField) {
            odb.introspection().setDefaultIntrospector(new IntrospectorByFields());
        }
        this.objectDiffer = odb.build();
    }

    public <T> T merge(final T modified, final T base, T head)
    {
        if (base == null) {
            return head == null ? modified : head;
        }
        if (head == null) {
            head = base;
        }
        if (modified == null) {
            return head;
        }
        final DiffNode.Visitor visitor = new MergingDifferenceVisitor<T>(head, modified);
        final DiffNode difference = objectDiffer.compare(modified, base);
        difference.visit(visitor);
        return head;
    }

    private static final class MergingDifferenceVisitor<T> implements DiffNode.Visitor
    {
        private final T head;
        private final T modified;

        public MergingDifferenceVisitor(final T head, final T modified)
        {
            this.head = head;
            this.modified = modified;
        }

        public void node(final DiffNode node, final Visit visit)
        {
            if (node.getState() == DiffNode.State.ADDED)
            {
                node.canonicalSet(head, node.canonicalGet(modified));
            }
            else if (node.getState() == DiffNode.State.CHANGED)
            {
                if (node.hasChildren())
                {
                    node.visitChildren(this);
                    visit.dontGoDeeper();
                }
                else
                {
                    node.canonicalSet(head, node.canonicalGet(modified));
                }
            }
        }
    }
}

