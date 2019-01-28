package velites.java.utility.merge;

import de.danielbechler.diff.selector.ElementSelector;

public class FieldElementSelector extends ElementSelector {
    private String name;

    public FieldElementSelector(String name) {
        this.name = name;
    }

    @Override
    public String toHumanReadableString() {
        return this.name;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final FieldElementSelector that = (FieldElementSelector) o;

        if (!name.equals(that.name))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
