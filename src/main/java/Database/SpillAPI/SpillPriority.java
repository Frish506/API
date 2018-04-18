package Database.SpillAPI;

public enum SpillPriority {
    unknown(0),
    veryLow(1),
    low(2),
    medium(3),
    high(4),
    veryHigh(5);

    public int value;
    SpillPriority(int value) {
        this.value = value;
    }

    public static SpillPriority getPriority(int value) {
        switch (value) {
            case 1:
                return veryLow;
            case 2:
                return low;
            case 3:
                return medium;
            case 4:
                return high;
            case 5:
                return veryHigh;
        }
        return unknown;
    }
}
