package ua.kyslytsia.tct.mocks;

public class Competition {

    private int id;
    private String date;
    private String name;
    private String place;
    private int type_id; //of tourism
    private int distance_id;
    private int rank;
    private int penalty_time;
    private int is_closed;

    /**
     * Constructor with All parameters
     * @param date
     * @param name
     * @param place
     * @param type_id
     * @param distance_id
     * @param rank
     * @param penalty_time
     * @param is_closed
     */
    public Competition (int id, String date, String name, String place, int type_id, int distance_id, int rank, int penalty_time, int is_closed) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.place = place;
        this.type_id = type_id;
        this.distance_id = distance_id;
        this.rank = rank;
        this.penalty_time = penalty_time;
        this.is_closed = is_closed;
    }

    /**
     * Constructor with only NOT NULL parameters and IS_CLOSED = 0
     * @param date
     * @param name
     * @param type_id
     * @param distance_id
     * @param rank
     * @param penalty_time
     */
    public Competition (int id, String date, String name, int type_id, int distance_id, int rank, int penalty_time) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.type_id = type_id;
        this.distance_id = distance_id;
        this.rank = rank;
        this.penalty_time = penalty_time;
        this.is_closed = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public int getDistance_id() {
        return distance_id;
    }

    public void setDistance_id(int distance_id) {
        this.distance_id = distance_id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getPenalty_time() {
        return penalty_time;
    }

    public void setPenalty_time(int penalty_time) {
        this.penalty_time = penalty_time;
    }

    public int getIs_closed() {
        return is_closed;
    }

    public void setIs_closed(int is_closed) {
        this.is_closed = is_closed;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", type_id=" + type_id +
                ", distance_id=" + distance_id +
                ", rank=" + rank +
                ", penalty_time=" + penalty_time +
                ", is_closed=" + is_closed +
                '}';
    }
}
