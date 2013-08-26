package se.moveit;

public class PriceInquiry {
    final String fromAddress;
    final String toAddress;
    final int distance; // km
    final int houseArea; // kvm 
    final int otherArea; // kvm
    final boolean hasPiano;
    final boolean packaging;
    
    public PriceInquiry(String fromAddress, String toAddress, 
            int distance, int houseArea, int otherArea, boolean hasPiano, boolean packaging) {

        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.distance = distance;
        this.houseArea = houseArea;
        this.otherArea = otherArea;
        this.hasPiano = hasPiano;
        this.packaging = packaging;
    }
}

