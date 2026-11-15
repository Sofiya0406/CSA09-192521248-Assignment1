interface Pass { boolean isValid(); }

class OneDayPass implements Pass {
    private final boolean valid;
    OneDayPass(boolean valid) { this.valid = valid; }
    public boolean isValid() { return valid; }
}

abstract class Vehicle {
    abstract double ratePerHour();
    double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }
    // NEW overload: time-aware pricing, default ignores entry hour
    double calculateFee(int minutes, int entryHour) {
        return calculateFee(minutes);
    }
}

class Car extends Vehicle {
    double ratePerHour() { return 30; }
}

class EBike extends Vehicle {
    double ratePerHour() { return 0; }
    @Override double calculateFee(int minutes) { return 15; } // flat fee
}

class DeliveryVan extends Car {
    @Override double calculateFee(int minutes, int entryHour) {
        int hours = (minutes + 59) / 60;
        boolean peak = entryHour >= 9 && entryHour < 17;
        return hours * ratePerHour() * (peak ? 2 : 1);
    }
}

class Billing {
    static double bill(Vehicle v, Pass p, int minutes, int entryHour) {
        if (p != null && p.isValid()) return 0;
        return v.calculateFee(minutes, entryHour);
    }
}

class Main {
    public static void main(String[] args) {
        System.out.println("1 EBike 300 min -> " + Billing.bill(new EBike(), null, 300, 10));
        System.out.println("2 Van 120 min, entry 10 AM -> " + Billing.bill(new DeliveryVan(), null, 120, 10));
        System.out.println("3 Van 120 min, entry 6 PM -> " + Billing.bill(new DeliveryVan(), null, 120, 18));
        System.out.println("4 Car 120 min -> " + Billing.bill(new Car(), null, 120, 10));
        System.out.println("5 Car + valid one-day pass -> " + Billing.bill(new Car(), new OneDayPass(true), 120, 10));
        System.out.println("6 Car + expired pass -> " + Billing.bill(new Car(), new OneDayPass(false), 120, 10));
        int[] floors = {10, 20, 30, 40, 50};
        int total = 0;
        for (int n : floors) total += n;
        System.out.println("7 Total slots on 5 floors -> " + total);
    }
          }
