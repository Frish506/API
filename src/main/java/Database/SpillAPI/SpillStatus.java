package Database.SpillAPI;

public enum SpillStatus {
    unknown(0),
    received(1),
    inProgress(2),
    complete(3);

    public final int value;
    SpillStatus(int value) {
        this.value = value;
    }

    static SpillStatus getStatus(int value) {
        switch (value) {
            case 1:
                return received;
            case 2:
                return inProgress;
            case 3:
                return complete;
        }
        return unknown;
    }
}
