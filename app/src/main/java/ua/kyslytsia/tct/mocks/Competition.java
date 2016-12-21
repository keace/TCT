package ua.kyslytsia.tct.mocks;

import java.util.LinkedHashMap;

import ua.kyslytsia.tct.MembersActivity;
import ua.kyslytsia.tct.R;
import ua.kyslytsia.tct.database.Contract;

public class Competition {

    private int id;
    private String date;
    private String name;
    private String place;
    private int type_id; //of tourism
    private String type_name;
    private int distance_id;
    private String distance_name;
    private int rank;
    private int penalty_time;
    private int is_closed;

    private LinkedHashMap<String, String> resultMap;

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

    public String getType() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getDistance_id() {
        return distance_id;
    }

    public void setDistance_id(int distance_id) {
        this.distance_id = distance_id;
    }

    public String getDistance() {
        return distance_name;
    }

    public void setDistance_name(String distance_name) {
        this.distance_name = distance_name;
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

    public LinkedHashMap getResultMap() {
        resultMap = new LinkedHashMap<>();
        resultMap.put(Contract.CompetitionEntry.COLUMN_DATE, getDate());
        resultMap.put(Contract.CompetitionEntry.COLUMN_NAME, getName());
        resultMap.put(Contract.CompetitionEntry.COLUMN_PLACE, getPlace());
        resultMap.put(Contract.TypeEntry.COLUMN_NAME, getType()); //ad
        resultMap.put(Contract.DistanceEntry.COLUMN_NAME, getDistance()); //ad
        resultMap.put(Contract.CompetitionEntry.COLUMN_RANK, String.valueOf(getRank()));
        resultMap.put(Contract.CompetitionEntry.COLUMN_PENALTY_COST, String.valueOf(getPenalty_time()));
        return resultMap;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", type_id=" + type_id +
                ", type_name='" + type_name + '\'' +
                ", distance_id=" + distance_id +
                ", distance_name='" + distance_name + '\'' +
                ", rank=" + rank +
                ", penalty_time=" + penalty_time +
                ", is_closed=" + is_closed +
                '}';
    }
}
