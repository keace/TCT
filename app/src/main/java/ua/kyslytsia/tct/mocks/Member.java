package ua.kyslytsia.tct.mocks;

import java.io.Serializable;

public class Member implements Serializable {

    private int competitionId;
    private int personId;
    private int teamId;
    private int startNumber;
    private String sportRank;
    private String bike;
    private int time;
    private int place;

    /**
     *
     * @param competitionId
     * @param personId
     */
    public Member(int competitionId, int personId) {
        this.competitionId = competitionId;
        this.personId = personId;
    }

    public Member(int competitionId, int personId, int teamId, int startNumber, String sportRank, String bike) {
        this.competitionId = competitionId;
        this.personId = personId;
        this.teamId = teamId;
        this.startNumber = startNumber;
        this.sportRank = sportRank;
        this.bike = bike;
    }

    public Member(int competitionId, int personId, int teamId, int startNumber, String sportRank, String bike, int time, int place) {
        this.competitionId = competitionId;
        this.personId = personId;
        this.teamId = teamId;
        this.startNumber = startNumber;
        this.sportRank = sportRank;
        this.bike = bike;
        this.time = time;
        this.place = place;
    }

    public int getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(int competitionId) {
        this.competitionId = competitionId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(int startNumber) {
        this.startNumber = startNumber;
    }

    public String getSportRank() {
        return sportRank;
    }

    public void setSportRank(String sportRank) {
        this.sportRank = sportRank;
    }

    public String getBike() {
        return bike;
    }

    public void setBike(String bike) {
        this.bike = bike;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "Member{" +
                "competitionId=" + competitionId +
                ", personId=" + personId +
                ", teamId=" + teamId +
                ", startNumber=" + startNumber +
                ", sportRank='" + sportRank + '\'' +
                ", bike='" + bike + '\'' +
                ", time=" + time +
                ", place=" + place +
                '}';
    }
}

