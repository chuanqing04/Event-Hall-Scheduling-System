package objcls;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BookingManagement extends UserManagement {
    private FileHandler fileHandler;

    @Override
    public FileHandler getFileHandler() {
        return fileHandler;
    }

    @Override
    public void setFileHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }
  
    public BookingManagement(FileHandler fileHandler, String currentAdmin) {
        super(fileHandler, currentAdmin);
        this.fileHandler = new FileHandler("Bookings.txt");
    }

    public ArrayList<String[]> searchBookings(String query) {
    ArrayList<String[]> allBookings = fileHandler.getData(); 
    if (query.isEmpty()) {
        return new ArrayList<>(allBookings);
    }
    return allBookings.stream()
            .filter(booking -> booking[0].toLowerCase().startsWith(query.toLowerCase())) // Adjust index if necessary
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<String[]> filterBookings(String hallType, String sortOrder) {
        boolean ascending = "Ascending".equals(sortOrder);
        return fileHandler.filterBookingsData(hallType, ascending); // Use the ascending variable here
    }
}
