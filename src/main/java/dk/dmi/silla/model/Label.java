package dk.dmi.silla.model;

public class Label {
    private Long label = 0L;

    public Label() {
    }

    public Label(Long label) {
        set(label);
    }

    public void set(Long newLabel) {
        label = newLabel;
    }

    public Long get() {
        return label;
    }

    public void setQC() {
        setFlag(4, 2);
    }

    public void clearQC() {
        setFlag(4, 1);
    }

    public boolean isQC() {
        return getFlag(4) == 2;
    }

    public void setExclude() {
        setFlag(9, 3);
    }

    public void clearExclude() {
        setFlag(9, 0);
    }

    public boolean isExcluded() {
        return getFlag(9) == 3;
    }

    // Position: index of the flag counted from the right of the label (starts at 1)
    // Value: the value to set the flag to (valid values are in the range 0 to 9)
    private void setFlag(Integer position, Integer value) {
        long positionPower = (long) Math.pow(10, position - 1);
        label = label - (label % (positionPower * 10)) + ((value % 10) * positionPower) + (label % positionPower);
    }

    private Integer getFlag(Integer position) {
        Long positionPower = (long) Math.pow(10, position - 1);
        return (int) ((label / positionPower) % 10);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Label otherLabel = (Label) obj;
        return (label.equals(otherLabel.get()));
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public String toString() {
        return label.toString();
    }
}
