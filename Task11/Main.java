import java.util.function.Supplier;

abstract class Vehicle {
    private final String regNo;
    Vehicle(String regNo) {
        if (regNo == null || regNo.isEmpty()) throw new IllegalArgumentException("Empty regNo");
        this.regNo = regNo;
    }
    String getRegNo() { return regNo; }
    abstract double ratePerHour();
    double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Negative minutes");
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String r) { super(r); }
    double ratePerHour() { return 10; }
}
class Car extends Vehicle {
    Car(String r) { super(r); }
    double ratePerHour() { return 30; }
}
class ElectricCar extends Car {
    ElectricCar(String r) { super(r); }
}
class Ticket {
    private static int counter = 0;
    private final int no;
    private final Vehicle vehicle;
    private final int floor, slot;
    Ticket(Vehicle v, int floor, int slot) {
        this.no = ++counter; this.vehicle = v; this.floor = floor; this.slot = slot;
    }
    Vehicle getVehicle() { return vehicle; }
    public String toString() {
        return "Ticket#" + no + " " + vehicle.getRegNo() + " [" + floor + "," + slot + "]";
    }
}
class ParkingFacility {
    private final Ticket[][] slots;
    ParkingFacility(int[] n) {
        slots = new Ticket[n.length][];
        for (int f = 0; f < n.length; f++) slots[f] = new Ticket[n[f]];
    }
    static Vehicle create(String type, String reg) {
        switch (type.toLowerCase()) {
            case "bike": return new TwoWheeler(reg);
            case "car": return new Car(reg);
            case "ev": return new ElectricCar(reg);
            default: throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
    private boolean isParked(String reg) {
        for (Ticket[] row : slots)
            for (Ticket t : row)
                if (t != null && t.getVehicle().getRegNo().equals(reg)) return true;
        return false;
    }
    Ticket enter(String type, String reg) {
        Vehicle v = create(type, reg);
        if (isParked(reg)) throw new IllegalStateException("Duplicate registration: " + reg);
        for (int f = 0; f < slots.length; f++)
            for (int s = 0; s < slots[f].length; s++)
                if (slots[f][s] == null) {
                    Ticket t = new Ticket(v, f, s);
                    slots[f][s] = t;
                    return t;
                }
        return null;
    }
    double exit(int f, int s, int minutes) {
        if (f < 0 || f >= slots.length || s < 0 || s >= slots[f].length)
            throw new IllegalArgumentException("Invalid slot");
        if (slots[f][s] == null) throw new IllegalStateException("Slot already free");
        double fee = slots[f][s].getVehicle().calculateFee(minutes);
        slots[f][s] = null;
        return fee;
    }
}
class Main {
    static void test(String name, Supplier<Object> s) {
        try { System.out.println(name + " -> " + s.get()); }
        catch (RuntimeException e) { System.out.println(name + " -> ERROR: " + e.getMessage()); }
    }
    public static void main(String[] args) {
        ParkingFacility pf = new ParkingFacility(new int[]{1, 2});
        test("1 bike TN01", () -> pf.enter("bike", "TN01"));
        test("2 car TN02", () -> pf.enter("car", "TN02"));
        test("3 ev TN03 (last slot)", () -> pf.enter("ev", "TN03"));
        test("4 car TN04 (full)", () -> pf.enter("car", "TN04"));
        test("5 duplicate TN02", () -> pf.enter("car", "TN02"));
        test("6 unknown type bus", () -> pf.enter("bus", "TN05"));
        test("7 exit 0 min", () -> pf.exit(0, 0, 0));
        test("8 exit 60 min", () -> pf.exit(1, 0, 60));
        test("9 exit 61 min", () -> pf.exit(1, 1, 61));
        test("10 invalid slot", () -> pf.exit(5, 0, 10));
        test("11 already free", () -> pf.exit(0, 0, 10));
    }
}
